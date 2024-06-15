import os
from celery import Celery

# Установка настроек Django для Celery
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'ToolServer.settings')

app = Celery('ToolServer')

# Автоматическое обнаружение задач Celery в приложениях Django
app.autodiscover_tasks()