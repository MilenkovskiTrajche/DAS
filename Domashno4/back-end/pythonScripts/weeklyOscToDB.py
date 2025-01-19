import os
import numpy as np
import pandas as pd
import psycopg2

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

        # CALCULATE RSI
        df_weekly = df.resample('W').last()
        df_weekly['RSI_14W'] = calculate_rsi(df_weekly['PriceOfLastTransaction'], period=14)

        df_weekly['Signal_RSI'] = 'HOLD'
        for i in range(20, len(df_weekly)):
            if df_weekly['RSI_14W'].iloc[i] < 30:
                df_weekly.loc[df_weekly.index[i], 'Signal_RSI'] = 'BUY'
            elif df_weekly['RSI_14W'].iloc[i] > 70:
                df_weekly.loc[df_weekly.index[i], 'Signal_RSI'] = 'SELL'

        # CALCULATE STOCHASTIC
        stochastic = 100 * (df_weekly['PriceOfLastTransaction'] - df_weekly['Low'].rolling(14).min()) / (
                    df_weekly['High'].rolling(14).max() - df_weekly['Low'].rolling(14).min())
        df_weekly['STC_14W'] = stochastic.rolling(3).mean()

        df_weekly['Signal_STC'] = 'HOLD'
        for i in range(20, len(df_weekly)):
            if df_weekly['STC_14W'].iloc[i] < 20:
                df_weekly.loc[df_weekly.index[i], 'Signal_STC'] = 'BUY'  # Oversold, potential buy signal
            elif df_weekly['STC_14W'].iloc[i] > 80:
                df_weekly.loc[df_weekly.index[i], 'Signal_STC'] = 'SELL'  # Overbought, potential sell signal

        # CALCULATE MACD
        df_weekly['EMA_12'] = df_weekly['PriceOfLastTransaction'].ewm(span=12, adjust=False).mean()
        df_weekly['EMA_26'] = df_weekly['PriceOfLastTransaction'].ewm(span=26, adjust=False).mean()

        df_weekly['MACD_14W'] = df_weekly['EMA_12'] - df_weekly['EMA_26']

        df_weekly['Signal_Line'] = df_weekly['MACD_14W'].ewm(span=9, adjust=False).mean()

        df_weekly['Signal_MACD'] = 'HOLD'
        for i in range(1, len(df_weekly)):
            if df_weekly['MACD_14W'].iloc[i] > df_weekly['Signal_Line'].iloc[i] and df_weekly['MACD_14W'].iloc[i - 1] <= \
                    df_weekly['Signal_Line'].iloc[i - 1]:
                df_weekly.loc[df_weekly.index[i], 'Signal_MACD'] = 'BUY'  # MACD crosses above Signal Line
            elif df_weekly['MACD_14W'].iloc[i] < df_weekly['Signal_Line'].iloc[i] and df_weekly['MACD_14W'].iloc[
                i - 1] >= \
                    df_weekly['Signal_Line'].iloc[i - 1]:
                df_weekly.loc[df_weekly.index[i], 'Signal_MACD'] = 'SELL'

        # AWESOME OSCILATOR
        df_weekly['Median_Price'] = (df_weekly['High'] + df_weekly['Low']) / 2

        # Calculate AO: Difference between 5-period and 34-period SMA of Median Price
        df_weekly['SMA_5'] = df_weekly['Median_Price'].rolling(window=5).mean()
        df_weekly['SMA_34'] = df_weekly['Median_Price'].rolling(window=34).mean()
        df_weekly['AO_14W'] = df_weekly['SMA_5'] - df_weekly['SMA_34']

        # Generate AO Signals
        df_weekly['Signal_AO'] = 'HOLD'
        for i in range(1, len(df_weekly)):
            if df_weekly['AO_14W'].iloc[i] > 0 and df_weekly['AO_14W'].iloc[i - 1] <= 0:
                df_weekly.loc[df_weekly.index[i], 'Signal_AO'] = 'BUY'  # Positive crossover
            elif df_weekly['AO_14W'].iloc[i] < 0 and df_weekly['AO_14W'].iloc[i - 1] >= 0:
                df_weekly.loc[df_weekly.index[i], 'Signal_AO'] = 'SELL'  # Negative crossover

        # CALCULATE CCI
        df_weekly['Typical_Price'] = (df_weekly['High'] + df_weekly['Low'] + df_weekly['PriceOfLastTransaction']) / 3

        # Calculate SMA of Typical Price
        period = 20  # Typical period for CCI
        df_weekly['SMA_TP'] = df_weekly['Typical_Price'].rolling(window=period).mean()

        # Calculate Mean Deviation
        df_weekly['Mean_Deviation'] = df_weekly['Typical_Price'].rolling(window=period).apply(
            lambda x: np.mean(np.abs(x - x.mean())), raw=True
        )

        # Calculate CCI
        df_weekly['CCI_14W'] = (df_weekly['Typical_Price'] - df_weekly['SMA_TP']) / (
                    0.015 * df_weekly['Mean_Deviation'])

        # Generate CCI Signals
        df_weekly['Signal_CCI'] = 'HOLD'
        for i in range(period, len(df_weekly)):
            if df_weekly['CCI_14W'].iloc[i] < -100:
                df_weekly.loc[df_weekly.index[i], 'Signal_CCI'] = 'BUY'  # Oversold condition
            elif df_weekly['CCI_14W'].iloc[i] > 100:
                df_weekly.loc[df_weekly.index[i], 'Signal_CCI'] = 'SELL'  # Overbought condition

        # FINAL SIGNAL CALCULATION
        def majority_signal(row):
            signals = [row['Signal_RSI'], row['Signal_STC'], row['Signal_MACD'], row['Signal_AO'],
                       row['Signal_CCI']]
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
            if not row[['PriceOfLastTransaction', 'High', 'Low', 'AveragePrice', 'Percentage', 'Volume', 'Turnover1', 'Turnover2']].isnull().any():
                cursor.execute(
                    f"""
                    INSERT INTO {TABLE_NAME} (symbol, type, date, signal)
                    VALUES (%s, %s, %s, %s)
                    """,
                    (
                        symbol,        # Use the extracted symbol
                        "weekly",      # Type of data
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
