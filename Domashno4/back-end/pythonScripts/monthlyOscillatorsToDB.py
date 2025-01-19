import os
import numpy as np
import pandas as pd
import psycopg2
from datetime import datetime

# Database connection settings
DATABASE_HOST = "db"
DATABASE_NAME = "stock_data"
DATABASE_USER = "postgres"
DATABASE_PASSWORD = "postgres"
TABLE_NAME = "oscillator"

input_directory = 'pythonScripts//Fetched_Files2'

def get_last_saved_date():
    """Retrieve the last saved date from a file."""
    if os.path.exists(LAST_SAVED_DATE_FILE):
        with open(LAST_SAVED_DATE_FILE, 'r', encoding='utf-8') as file:
            return file.readline().strip()
    return ""

# Connect to the PostgreSQL database
def get_db_connection():
    return psycopg2.connect(
        host=DATABASE_HOST,
        database=DATABASE_NAME,
        user=DATABASE_USER,
        password=DATABASE_PASSWORD
    )

def calculate_rsi(data, period=14):
    delta = data.diff()
    gain = delta.where(delta > 0, 0)
    loss = -delta.where(delta < 0, 0)

    avg_gain = gain.rolling(period).mean()
    avg_loss = loss.rolling(period).mean()

    rs = avg_gain / avg_loss
    rsi = 100 - (100 / (1 + rs))

    rsi.iloc[:period] = np.nan
    return rsi

def process_file(file_path, db_connection):
    try:
        # Extract the symbol from the file name
        file_name = os.path.basename(file_path)
        symbol = file_name.split('_')[0]  # Extract the part before "_stock_data.csv"

        # Load the data
        df = pd.read_csv(file_path, header=None, delimiter=',')
        df.columns = ["Date", "PriceOfLastTransaction", "High", "Low", "AveragePrice", "Percentage", "Volume",
                      "Turnover1", "Turnover2"]

        df['Date'] = pd.to_datetime(df['Date'], format='%d.%m.%Y')
        df.set_index('Date', inplace=True)

        # RSI calculation
        df_weekly = df.resample('ME').last()
        df_weekly['RSI_14M'] = calculate_rsi(df_weekly['PriceOfLastTransaction'], period=14)
        df_weekly['Signal_RSI'] = 'HOLD'
        for i in range(20, len(df_weekly)):
            if df_weekly['RSI_14M'].iloc[i] < 30:
                df_weekly.loc[df_weekly.index[i], 'Signal_RSI'] = 'BUY'
            elif df_weekly['RSI_14M'].iloc[i] > 70:
                df_weekly.loc[df_weekly.index[i], 'Signal_RSI'] = 'SELL'

        # Final Signal Calculation
        def majority_signal(row):
            signals = [row['Signal_RSI']]
            buy_count = signals.count('BUY')
            sell_count = signals.count('SELL')
            if buy_count >= 1:
                return 'BUY'
            elif sell_count >= 1:
                return 'SELL'
            else:
                return 'HOLD'

        df_weekly['Final_Signal'] = df_weekly.apply(majority_signal, axis=1)

        # Insert data into the database
        cursor = db_connection.cursor()
        for index, row in df_weekly.iterrows():
            # Check if all required fields are non-empty
            if not row[['PriceOfLastTransaction', 'High', 'Low', 'AveragePrice',
                        'Percentage', 'Volume', 'Turnover1', 'Turnover2']].isnull().any():
                cursor.execute(
                    f"""
                    INSERT INTO {TABLE_NAME} (symbol, type, date, signal)
                    VALUES (%s, %s, %s, %s)
                    """,
                    (
                        symbol,        # Use the extracted symbol
                        "monthly",     # Type of data
                        index,
                        row['Final_Signal']
                    )
                )

        db_connection.commit()
        print(f"Processed and saved data for file: {file_path}")

    except Exception as e:
        print(f"An error occurred: {e}")
        db_connection.rollback()

def main():
    db_connection = get_db_connection()
    try:
        for file in os.listdir(input_directory):
            if file.endswith(".csv"):
                file_path = os.path.join(input_directory, file)
                process_file(file_path, db_connection)
    finally:
        db_connection.close()

if __name__ == "__main__":
    main()