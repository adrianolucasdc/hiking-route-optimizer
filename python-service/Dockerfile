    # python-service/Dockerfile
    FROM python:3.9-slim-buster
    WORKDIR /app
    COPY requirements.txt .
    RUN pip install -r requirements.txt
    COPY . .
    CMD ["python", "your_app.py"] # Or gunicorn, uvicorn for production