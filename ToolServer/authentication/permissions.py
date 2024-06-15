from rest_framework import permissions


class IsOwnerOrReadOnly(permissions.BasePermission):
    """
    Пользовательское разрешение, разрешающее доступ только владельцу объекта.
    """

    def has_object_permission(self, request, view, obj):
        # Разрешения на чтение доступны всем
        if request.method in permissions.SAFE_METHODS:
            return True
#
        # Разрешения на запись доступны только владельцу объекта
        return obj.owner == request.user