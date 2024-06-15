from rest_framework import generics, status
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework.views import APIView

from authentication.models import User
from tools import serializers
from tools.models import Tool, RentLog
from tools.serializers import ToolSerializer, RentLogSerializer
from rest_framework.exceptions import ValidationError


class ToolListCreateView(generics.ListCreateAPIView):
    queryset = Tool.objects.all()
    serializer_class = ToolSerializer
    permission_classes = [IsAuthenticated]


class ToolRetrieveUpdateDestroyView(generics.RetrieveUpdateDestroyAPIView):
    queryset = Tool.objects.all()
    serializer_class = ToolSerializer
    permission_classes = [IsAuthenticated]


    def perform_update(self, serializer):
        instance = serializer.save()
        if instance.status == 'RENTED' and 'current_user' not in serializer.validated_data:
            # Добавляем проверку, чтобы при обновлении статуса на RENTED всегда был указан current_user
            raise ValidationError("При установке статуса 'RENTED' необходимо указать 'current_user'")
        return instance


class RentLogListCreateView(generics.ListCreateAPIView):
    queryset = RentLog.objects.all()
    serializer_class = RentLogSerializer
    permission_classes = [IsAuthenticated]



class RentLogRetrieveUpdateDestroyView(generics.RetrieveUpdateDestroyAPIView):
    queryset = RentLog.objects.all()
    serializer_class = RentLogSerializer
    permission_classes = [IsAuthenticated]


class TakeToolView(APIView):
    permission_classes = [IsAuthenticated]

    @api_view(['POST'])
    def take_tool(request, pk):
        """
        Endpoint для взятия инструмента.
        """
        try:
            tool = Tool.objects.get(pk=pk)
        except Tool.DoesNotExist:
            return Response({"error": "Инструмент не найден"}, status=status.HTTP_404_NOT_FOUND)

        if tool.status == 'RENTED':
            return Response({"error": "Инструмент уже взят"}, status=status.HTTP_400_BAD_REQUEST)

        # Получаем ID пользователя из тела запроса
        user_id = request.data.get('user_id')
        if not user_id:
            return Response({"error": "Не указан ID пользователя"}, status=status.HTTP_400_BAD_REQUEST)

        try:
            user = User.objects.get(pk=user_id)
        except User.DoesNotExist:
            return Response({"error": "Пользователь не найден"}, status=status.HTTP_404_NOT_FOUND)

        tool.status = 'RENTED'
        tool.current_user = user
        tool.save()

        RentLog.objects.create(user=user, tool=tool, action='CHECKOUT')

        return Response(ToolSerializer(tool).data)


class ReturnToolView(APIView):
    permission_classes = [IsAuthenticated]

    @api_view(['POST'])
    def return_tool(request, pk):
        """
        Endpoint для возврата инструмента.
        """
        try:
            tool = Tool.objects.get(pk=pk)
        except Tool.DoesNotExist:
            return Response({"error": "Инструмент не найден"}, status=status.HTTP_404_NOT_FOUND)

        if tool.status == 'AVAILABLE':
            return Response({"error": "Инструмент уже возвращен"}, status=status.HTTP_400_BAD_REQUEST)

        tool.status = 'AVAILABLE'
        tool.current_user = None
        tool.save()

        RentLog.objects.create(user=request.user, tool=tool, action='RETURN')

        return Response(ToolSerializer(tool).data)

