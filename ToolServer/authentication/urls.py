from django.urls import path

from .views import (
    RegistrationAPIView,
    LoginAPIView,
    UserRetrieveUpdateAPIView
)

app_name = 'authentication'

urlpatterns = [
    path('users/<int:pk>/', UserRetrieveUpdateAPIView.as_view()),  # Изменяем маршрут для получения пользователя по ID
    path('users/', RegistrationAPIView.as_view()),
    path('users/login/', LoginAPIView.as_view()),
]