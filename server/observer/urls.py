from django.conf.urls import url, include
from rest_framework import routers
from rest_framework_swagger.views import get_swagger_view

from . import views

router = routers.DefaultRouter()
router.register(r'image', views.ImageViewSet)

schema_view = get_swagger_view(title='server API')

urlpatterns = [
    url(r'^', include(router.urls)),
    url(r'^docs$', schema_view),
    url(r'^api-v1/', include('rest_framework.urls', namespace='rest_framework_category')),
]