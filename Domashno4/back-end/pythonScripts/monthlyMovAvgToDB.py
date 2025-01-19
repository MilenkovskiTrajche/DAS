import os
import numpy as np
import pandas as pd
import psycopg2

# Database connection settings
DATABASE_HOST = "db"
DATABASE_NAME = "stock_data"
DATABASE_USER = "postgres"
DATABASE_PASSWORD = "postgres"
TABLE_NAME = "moving_average"

input_directory = 'pythonScripts//Fetched_Files2'
# output_directory = 'monthly_averages'

# os.makedirs(output_directory, exist_ok=True)

# Connect to the PostgreSQL database
def get_last_saved_date():
    """Retrieve the last saved date from a file."""
    if os.path.exists(LAST_SAVED_DATE_FILE):
        with open(LAST_SAVED_DATE_FILE, 'r', encoding='utf-8') as file:
            return file.readline().strip()
    return ""

def get_db_connection():
    return psycopg2.connect(
        host=DATABASE_HOST,
        database=DATABASE_NAME,
        user=DATABASE_USER,
        password=DATABASE_PASSWORD
    )

def process_file(file_path, db_connection):
    try:
        file_name = os.path.basename(file_path)
        symbol = file_name.split('_')[0]

        df = pd.read_csv(file_path, header=None, delimiter=',')
        df.columns = ["Date", "PriceOfLastTransaction", "High", "Low", "AveragePrice", "Percentage", "Volume",
                      "Turnover1", "Turnover2"]

        df['Date'] = pd.to_datetime(df['Date'], format='%d.%m.%Y')
        df.set_index('Date', inplace=True)

        df_weekly = df.resample('M').last()

        # EMA CALCULATION
        df_weekly['EMA_5M'] = df_weekly['PriceOfLastTransaction'].ewm(span=5, adjust=False).mean()

        df_weekly['Signal_EMA'] = 'HOLD'
        df_weekly['EMA_5M'].fillna(0, inplace=True)

        for i in range(5, len(df_weekly)):
            if df_weekly['PriceOfLastTransaction'].iloc[i] > df_weekly['EMA_5M'].iloc[i]:
                df_weekly.loc[df_weekly.index[i], 'Signal_EMA'] = 'BUY'
            elif df_weekly['PriceOfLastTransaction'].iloc[i] < df_weekly['EMA_5M'].iloc[i]:
                df_weekly.loc[df_weekly.index[i], 'Signal_EMA'] = 'SELL'

        # SMA CALCULATION
        df_weekly['SMA_5M'] = df_weekly['PriceOfLastTransaction'].rolling(window=5).mean()
        df_weekly['SMA_5M'].fillna(0, inplace=True)

        df_weekly['Signal_SMA'] = 'HOLD'
        for i in range(5, len(df_weekly)):
            if df_weekly['PriceOfLastTransaction'].iloc[i] > df_weekly['SMA_5M'].iloc[i]:
                df_weekly.loc[df_weekly.index[i], 'Signal_SMA'] = 'BUY'
            elif df_weekly['PriceOfLastTransaction'].iloc[i] < df_weekly['SMA_5M'].iloc[i]:
                df_weekly.loc[df_weekly.index[i], 'Signal_SMA'] = 'SELL'

        # WMA CALCULATION
        weights = np.arange(1, 6)
        df_weekly['WMA_5M'] = df_weekly['PriceOfLastTransaction'].rolling(window=5).apply(
            lambda x: np.dot(x, weights[::-1]) / weights.sum())
        df_weekly['WMA_5M'].fillna(0, inplace=True)

        df_weekly['Signal_WMA'] = 'HOLD'
        for i in range(5, len(df_weekly)):
            if df_weekly['PriceOfLastTransaction'].iloc[i] > df_weekly['WMA_5M'].iloc[i]:
                df_weekly.loc[df_weekly.index[i], 'Signal_WMA'] = 'BUY'
            elif df_weekly['PriceOfLastTransaction'].iloc[i] < df_weekly['WMA_5M'].iloc[i]:
                df_weekly.loc[df_weekly.index[i], 'Signal_WMA'] = 'SELL'

        # TEMA CALCULATION
        ema1 = df_weekly['PriceOfLastTransaction'].ewm(span=5, adjust=False).mean()
        ema2 = ema1.ewm(span=5, adjust=False).mean()
        ema3 = ema2.ewm(span=5, adjust=False).mean()
        df_weekly['TEMA_5M'] = 3 * ema1 - 3 * ema2 + ema3
        df_weekly['TEMA_5M'].fillna(0, inplace=True)

        df_weekly['Signal_TEMA'] = 'HOLD'
        for i in range(5, len(df_weekly)):
            if df_weekly['PriceOfLastTransaction'].iloc[i] > df_weekly['TEMA_5M'].iloc[i]:
                df_weekly.loc[df_weekly.index[i], 'Signal_TEMA'] = 'BUY'
            elif df_weekly['PriceOfLastTransaction'].iloc[i] < df_weekly['TEMA_5M'].iloc[i]:
                df_weekly.loc[df_weekly.index[i], 'Signal_TEMA'] = 'SELL'

        # HMA CALCULATION
        wma1 = df_weekly['PriceOfLastTransaction'].rolling(5 // 2).apply(
            lambda x: ((np.arange(5 // 2) + 1) * x).sum() / (np.arange(5 // 2) + 1).sum(), raw=True)
        wma2 = df_weekly['PriceOfLastTransaction'].rolling(5).apply(
            lambda x: ((np.arange(5) + 1) * x).sum() / (np.arange(5) + 1).sum(), raw=True)
        hma = 2 * wma1 - wma2
        hma = hma.rolling(int(np.sqrt(5))).mean()
        df_weekly['HMA_5M'] = hma
        df_weekly['HMA_5M'].fillna(0, inplace=True)

        df_weekly['Signal_HMA'] = 'HOLD'
        for i in range(5, len(df_weekly)):
            if df_weekly['PriceOfLastTransaction'].iloc[i] > df_weekly['HMA_5M'].iloc[i]:
                df_weekly.loc[df_weekly.index[i], 'Signal_HMA'] = 'BUY'
            elif df_weekly['PriceOfLastTransaction'].iloc[i] < df_weekly['HMA_5M'].iloc[i]:
                df_weekly.loc[df_weekly.index[i], 'Signal_HMA'] = 'SELL'

        # Final signal based on majority rule
        def majority_signal(row):
            signals = [row['Signal_EMA'], row['Signal_SMA'], row['Signal_WMA'], row['Signal_TEMA'], row['Signal_HMA']]
            buy_count = signals.count('BUY')
            sell_count = signals.count('SELL')
            if buy_count >= 3:
                return 'BUY'
            elif sell_count >= 3:
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
                        symbol,  # Use the extracted symbol
                        "monthly",  # Type of data
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
