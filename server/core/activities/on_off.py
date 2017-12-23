# On/off energy from whatever you want
from core.codes import Code
from core.models import OnOff
from django.forms.models import model_to_dict
from django.core import serializers

import json


def create(name="Light", port=None):
    if port is not None:
        try:
            OnOff.objects.get(port=port)
        except OnOff.DoesNotExist:
            entity = OnOff(name=name, port=port)
            entity.save()
            return read()
        else:
            return {"error": Code.ALREADY_EXIST}
    return {"error": Code.MISSING_PARAMETER}


def read():
    try:
        data = serializers.serialize('json', OnOff.objects.all())
        return json.loads('{"sensors": ' + data + '}')
    except OnOff.DoesNotExist:
        return {"error": Code.NOT_FOUND}


def update(id_port=None, name="Light", port=None):
    # TODO if I update the port to one that already exist, this will update the another entity
    if id_port is not None:
        try:
            entity = OnOff.objects.get(port=id_port)
            entity.name = name
            entity.port = port
            entity.save()
            return read()
        except OnOff.DoesNotExist:
            return {"error": Code.NOT_FOUND}
    return {"error": Code.MISSING_PARAMETER}


def delete(id_port=None):
    if id_port is not None:
        try:
            entity = OnOff.objects.get(port=id_port)
            entity.delete()
            return read()
        except OnOff.DoesNotExist:
            return {"error": Code.NOT_FOUND}
    return {"error": Code.MISSING_PARAMETER}


def on(id_port=None, timer=0):
    if id_port is not None:
        try:
            entity = OnOff.objects.get(port=id_port)
            entity.is_on = True
            entity.timer = timer
            entity.save()
            # TODO Turn on the light
            print("Light " + entity.name + " is on")
            return read()
        except OnOff.DoesNotExist:
            return {"error": Code.NOT_FOUND}
    return {"error": Code.MISSING_PARAMETER}


def off(id_port=None):
    if id_port is not None:
        try:
            entity = OnOff.objects.get(port=id_port)
            entity.is_on = False
            entity.timer = 0
            entity.save()
            # TODO Turn off the light
            print("Light " + entity.name + " is off")
            return read()
        except OnOff.DoesNotExist:
            return {"error": Code.NOT_FOUND}
    return {"error": Code.MISSING_PARAMETER}
