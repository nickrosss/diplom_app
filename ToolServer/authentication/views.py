from rest_framework import status
from rest_framework.generics import RetrieveUpdateAPIView
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView

from .serializers import (
    RegistrationSerializer,
    LoginSerializer,
    UserSerializer
)
from .renderers import UserJSONRenderer
from .models import User


class RegistrationAPIView(APIView):
    """
    Разрешить всем пользователям (аутентифицированным и нет) доступ к эндпоинту
    регистрации.
    """
    permission_classes = (AllowAny,)
    serializer_class = RegistrationSerializer
    renderer_classes = (UserJSONRenderer,)

    def post(self, request):
        """
        Создать нового пользователя.
        """
        user = request.data.get('user', {})

        # Создаем экземпляр сериализатора, передаем данные пользователя и проверяем их валидность
        serializer = self.serializer_class(data=user)
        serializer.is_valid(raise_exception=True)
        serializer.save()

        # Возвращаем данные созданного пользователя и код 201 Created
        return Response(serializer.data, status=status.HTTP_201_CREATED)


class LoginAPIView(APIView):
    """
    Разрешить всем пользователям (аутентифицированным и нет) доступ к эндпоинту
    аутентификации.
    """
    permission_classes = (AllowAny,)
    renderer_classes = (UserJSONRenderer,)
    serializer_class = LoginSerializer

    def post(self, request):
        """
        Проверить учетные данные пользователя и вернуть токен.
        """
        user = request.data.get('user', {})

        # Создаем экземпляр сериализатора, передаем данные пользователя и проверяем их валидность
        serializer = self.serializer_class(data=user)
        serializer.is_valid(raise_exception=True)

        # Возвращаем данные аутентифицированного пользователя и код 200 OK
        return Response(serializer.data, status=status.HTTP_200_OK)


class UserRetrieveUpdateAPIView(RetrieveUpdateAPIView):
    """
    Обработка запросов GET, PUT и PATCH для модели User. Обновлять можно
    только свой профиль.
    """
    permission_classes = (IsAuthenticated,)
    renderer_classes = (UserJSONRenderer,)
    serializer_class = UserSerializer
    queryset = User.objects.all()

    def retrieve(self, request, *args, **kwargs):
        """
        Получение пользователя по ID.
        """
        # Используем метод get_object() из RetrieveUpdateAPIView для
        # получения объекта пользователя по ID из URL
        instance = self.get_object()
        serializer = self.serializer_class(instance)
        return Response(serializer.data)

    def update(self, request, *args, **kwargs):
        """
        Обновление данных пользователя.
        """
        serializer_data = request.data.get('user', {})

        # Используем метод get_object() из RetrieveUpdateAPIView для
        # получения объекта пользователя по ID из URL
        serializer = self.serializer_class(
            request.user, data=serializer_data, partial=True
        )
        serializer.is_valid(raise_exception=True)
        serializer.save()

        return Response(serializer.data, status=status.HTTP_200_OK)