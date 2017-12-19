from django.db import models


class OnOff(models.Model):
    port = models.IntegerField(primary_key=True)
    name = models.CharField(max_length=50, default="Light")
    is_on = models.BooleanField(default=False)
    timer = models.IntegerField(default=0)  # Time in seconds
    # TODO Put scheduling
    # TODO Permission (here or in group?)

    def __str__(self):
        return str(self.name) + " - " + str(self.port)
