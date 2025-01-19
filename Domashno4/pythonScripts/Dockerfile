FROM python:3.9

WORKDIR /app

COPY python-scripts/my_api_script.py .
COPY python-scripts/requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

EXPOSE 4000

CMD ["python", "my_api_script.py"]