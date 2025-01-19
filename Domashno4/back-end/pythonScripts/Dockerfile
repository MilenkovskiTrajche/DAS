FROM python:3.9

WORKDIR /app

COPY webscrapper.py .
COPY weeklyOscToDB.py .
COPY weeklyMovAvgToDB.py .
COPY monthlyOscillatorsToDB.py .
COPY monthlyMovAvgToDB.py .
COPY requirements.txt .

COPY run_all.py .

RUN pip install --no-cache-dir -r requirements.txt

EXPOSE 4000

CMD ["python", "run_all.py"]