from django.db import models

import jwt
from datetime import datetime, timedelta

from django.conf import settings
from django.contrib.auth.models import AbstractBaseUser, BaseUserManager, PermissionsMixin


class UserManager(BaseUserManager):

    def create_user(self, username, password=None, email=None):
        if username is None:
            raise TypeError('Users must have a username.')

        if email is None:
            raise TypeError('Users must have an email address.')

        user = self.model(username=username, email=self.normalize_email(email))
        user.set_password(password)
        user.save()

        return user

    def create_superuser(self, username, email, password):
        if password is None:
            raise TypeError('Superusers must have a password.')

        user = self.create_user(username, email, password)
        user.is_superuser = True
        user.is_staff = True
        user.save()

        return user


class User(AbstractBaseUser, PermissionsMixin):
    username = models.CharField(db_index=True, max_length=255, unique=True)
    email = models.EmailField(db_index=True, unique=True, default='example@example.com')
    role = models.CharField(max_length=20, choices=[('WORKER', 'Worker'), ('MANAGER', 'Manager')], default='WORKER')

    USERNAME_FIELD = 'email'  # настройка поля для входа в систему
    REQUIRED_FIELDS = ['username']

    objects = UserManager()

    def __str__(self) -> str:  # строковое представление модели для отображения в консоли
        return self.username

    def _generate_jwt_token(self):  # генерация jwt сроком на 1 день с идентефикатором пользователя

        dt = datetime.now() + timedelta(days=1)

        token = jwt.encode({
            'id': self.pk,
            'exp': round(dt.timestamp())
        }, settings.SECRET_KEY, algorithm='HS256')

        return token  # .decode('utf-8')

    @property
    def token(self):
        return self._generate_jwt_token()

    def get_full_name(self):  # служебный метод django
        return self.username

    def get_short_name(self):  # аналогично
        return self.username