from rest_framework import serializers

from authentication.models import User
from .models import Tool, RentLog

class ToolSerializer(serializers.ModelSerializer):
    current_user = serializers.PrimaryKeyRelatedField(
        queryset=User.objects.all(),
        required=False,
        allow_null=True
    )

    class Meta:
        model = Tool
        fields = '__all__'


class RentLogSerializer(serializers.ModelSerializer):
    class Meta:
        model = RentLog
        fields = '__all__'