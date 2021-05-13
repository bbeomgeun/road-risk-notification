from django.db import models

# Create your models here.
class Image(models.Model):
    image = models.ImageField()
    width = models.IntegerField()
    height = models.IntegerField()
    date = models.DateField()

    def __str__(self):
        return self.date