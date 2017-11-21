# On/off energy from whatever you want
from core.codes import Code
from core.models import OnOff
from django.forms.models import model_to_dict
from django.core import serializers
import json


def create(name="Light", port=None):
    if port is not None:
        try:
            entity = OnOff(name=name, port=port)
            entity.save()
            return model_to_dict(entity)
        except:  # TODO What exception?
            return {"error": Code.ERROR}
    return {"error": Code.MISSING_PARAMETER}


def read():
    try:
        data = serializers.serialize('json', OnOff.objects.all())
        return json.loads(data)
    except OnOff.DoesNotExist:
        return {"error": Code.NOT_FOUND}


def update(id=None, name="Light", port=None):
    if id is not None:
        try:
            entity = OnOff.objects.get(port=id)
            entity.name = name
            entity.port = port
            entity.save()
            return model_to_dict(OnOff.objects.get(port=port))
        except OnOff.DoesNotExist:
            return {"error": Code.NOT_FOUND}
    return {"error": Code.MISSING_PARAMETER}


def delete(id=None):
    if id is not None:
        try:
            entity = OnOff.objects.get(port=id)
            entity.delete()
            return Code.SUCCESS
        except OnOff.DoesNotExist:
            return {"error": Code.NOT_FOUND}
    return {"error": Code.MISSING_PARAMETER}


def on(id=None, timer=0):
    if id is not None:
        try:
            entity = OnOff.objects.get(port=id)
            entity.is_on = True
            entity.timer = timer
            entity.save()
            # TODO Turn on the light
            return model_to_dict(OnOff.objects.get(port=id))
        except OnOff.DoesNotExist:
            return {"error": Code.NOT_FOUND}
    return {"error": Code.MISSING_PARAMETER}


def off(id=None):
    if id is not None:
        try:
            entity = OnOff.objects.get(port=id)
            entity.is_on = False
            entity.timer = 0
            entity.save()
            # TODO Turn off the light
            return model_to_dict(OnOff.objects.get(port=id))
        except OnOff.DoesNotExist:
            return {"error": Code.NOT_FOUND}
    return {"error": Code.MISSING_PARAMETER}
