from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('api/', include('authentication.urls', namespace='authentication')),
    path('api/', include('tools.urls', namespace='tools')),  # Добавляем маршрутизатор для приложения tools
]