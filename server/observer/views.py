from django.shortcuts import render
from django.core.exceptions import ObjectDoesNotExist, MultipleObjectsReturned
from rest_framework import viewsets

from django.http import JsonResponse

from .models import Image
from .serializers import ImageSerializers


class ImageViewSet(viewsets.ModelViewSet):
    queryset = Image.objects.all()

    serializer_class = ImageSerializers

    def create(self, request, *args, **kwargs):
        image = request.data['image']
        width = request.data['width']
        height = request.data['height']
        date = request.data['date']

        print( 'image income. -> width :', width, ', height :', height, 'time :', date )
        print( 'type : ', type(width), type(height), type(date) )
        print( "data type :", type(image), "data[0] type :",  type(image[0]), "data Len", len(image))
                # imageData = np.array(image)
        # imageData = imageData.reshape(height, width)
        # imageData = imageData[:, width - height:] # 1:1 사이즈로 ..
        #
        # for d in imageData:
        #     print( d, end =', ')

        #fileName = date.split(' ')[3].replace(':','_') # '2:32:13'
        # fileName = "inputImage"
        # #f = open("./data/" + fileName + ".txt", 'w')
        # f = open(fileName, 'w')
        # for d in image:
        #     f.write(str(d) + '\n')
        # f.close()
       # print( "data : ", image )

        return JsonResponse(  {"date":"Good"} )