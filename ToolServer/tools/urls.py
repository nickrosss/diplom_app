from django.urls import path
from .views import (
    ToolListCreateView,
    ToolRetrieveUpdateDestroyView,
    RentLogListCreateView,
    RentLogRetrieveUpdateDestroyView,
    TakeToolView,
    ReturnToolView
)

app_name = 'tools'

urlpatterns = [
    path('tools/', ToolListCreateView.as_view(), name='tool-list-create'),
    path('tools/<int:pk>/', ToolRetrieveUpdateDestroyView.as_view(), name='tool-retrieve-update-destroy'),
    path('rentlogs/', RentLogListCreateView.as_view(), name='rentlog-list-create'),
    path('rentlogs/<int:pk>/', RentLogRetrieveUpdateDestroyView.as_view(), name='rentlog-retrieve-update-destroy'),
    path('tools/<int:pk>/take/', TakeToolView.as_view(), name='take-tool'),
    path('tools/<int:pk>/return/', ReturnToolView.as_view(), name='return-tool'),
    path('rentlogs/', RentLogListCreateView.as_view(), name='rentlog-list'),
    path('rentlogs/tool/<int:tool_id>/', RentLogListCreateView.as_view(), name='rentlog-list-by-tool'),
    path('rentlogs/user/<int:user_id>/', RentLogListCreateView.as_view(), name='rentlog-list-by-user'),
]