import os
import requests
from bs4 import BeautifulSoup
from datetime import datetime
from multiprocessing import Pool, Manager, cpu_count

BASE_URL = "https://www.mse.mk/mk/stats/symbolhistory/"
FOLDER_NAME = "pythonScripts//Fetched_Files2"
DATA_FILE_SUFFIX = "_stock_data.csv"
LAST_SAVED_DATE_FILE = "pythonScripts//lastsaveddate2.txt"


def get_dropdown_values():
    """Fetch dropdown values from the website."""
    url = BASE_URL + "STB"
    response = requests.get(url)
    response.raise_for_status()
    soup = BeautifulSoup(response.text, 'html.parser')
    dropdown = soup.find('select', {'id': 'Code'})
    values = []
    if dropdown:
        for option in dropdown.find_all('option'):
            value = option.get('value')
            if value and not any(char.isdigit() for char in value):
                values.append(value)
    return values


def fetch_historical_data(symbol, from_date, to_date):
    """Fetch historical stock data for a given symbol and date range."""
    url = f"{BASE_URL}{symbol}"
    payload = {
        'FromDate': from_date,
        'ToDate': to_date,
        'Code': symbol,
        'submit': 'Прикажи'  # Cyrillic for "Show"
    }
    response = requests.post(url, data=payload)
    response.raise_for_status()

    soup = BeautifulSoup(response.text, 'html.parser')
    results_table = soup.find('table', {'id': 'resultsTable'})
    if not results_table:
        print(f"No data available for {symbol} from {from_date} to {to_date}.")
        return []

    data = []
    for row in results_table.find('tbody').find_all('tr'):
        cols = [col.text.strip() for col in row.find_all('td')]
        data.append(cols)
    return data


def save_data(symbol, data, lock):
    """Save the fetched data to a CSV file."""
    os.makedirs(FOLDER_NAME, exist_ok=True)
    file_path = os.path.join(FOLDER_NAME, f"{symbol}{DATA_FILE_SUFFIX}")

    with lock:  # Ensure thread-safe access to the file
        with open(file_path, 'a', encoding='utf-8') as file:
            for row in data:
                # Skip rows where the last two columns are both '0'
                if len(row) >= 2 and row[-1] == '0' and row[-2] == '0':
                    continue

                processed_row = [
                    col if index == 0 else col.replace('.', '').replace(',', '.')
                    for index, col in enumerate(row)
                ]
                file.write(','.join(f'"{col}"' for col in processed_row) + '\n')


def get_last_saved_date():
    """Retrieve the last saved date from a file."""
    if os.path.exists(LAST_SAVED_DATE_FILE):
        with open(LAST_SAVED_DATE_FILE, 'r', encoding='utf-8') as file:
            return file.readline().strip()
    return ""


def update_last_saved_date():
    """Update the last saved date to today's date."""
    today_date = datetime.now().strftime('%d.%m.%Y')
    with open(LAST_SAVED_DATE_FILE, 'w', encoding='utf-8') as file:
        file.write(today_date)


def process_symbol(symbol, last_date, today_date, lock):
    """Process data fetching and saving for a single symbol."""
    if last_date:
        data = fetch_historical_data(symbol, last_date, today_date)
        if data:
            save_data(symbol, data, lock)
    else:
        current_year = datetime.now().year
        for year in range(current_year - 10, current_year + 1):
            from_date = f"01.01.{year}"
            to_date = today_date if year == current_year else f"31.12.{year}"
            data = fetch_historical_data(symbol, from_date, to_date)
            if data:
                save_data(symbol, data, lock)


def main():
    start_time = datetime.now()

    # Ensure the data folder exists
    os.makedirs(FOLDER_NAME, exist_ok=True)

    # Fetch dropdown values
    dropdown_values = get_dropdown_values()

    # Check for last saved date
    last_date = get_last_saved_date()
    today_date = datetime.now().strftime('%d.%m.%Y')

    if last_date == today_date:
        print(f"ALL DATA IS FETCHED up to {today_date}.")
        return

    # Create a lock for thread-safe file access
    with Manager() as manager:
        lock = manager.Lock()

        # Use multiprocessing to process symbols in parallel
        with Pool(processes=cpu_count()) as pool:
            pool.starmap(
                process_symbol,
                [(symbol, last_date, today_date, lock) for symbol in dropdown_values]
            )

    # Update the last saved date
    update_last_saved_date()

    end_time = datetime.now()
    print(f"Total execution time: {end_time - start_time}")


if __name__ == "__main__":
    main()