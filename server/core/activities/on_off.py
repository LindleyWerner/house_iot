# On/off energy from whatever you want
import _thread
import json
import time

from django.core import serializers

from core.codes import Code
from core.models import OnOff

'''
import RPi.GPIO as gpio

gpio.setwarnings(False)
gpio.setmode(gpio.BCM)
# TODO this setup must be in create
gpio.setup(23, gpio.OUT)
gpio.setup(24, gpio.OUT)
'''


def create(name="Light", port=None):
    if port is not None:
        try:
            OnOff.objects.get(port=port)
        except OnOff.DoesNotExist:
            entity = OnOff(name=name, port=port)
            entity.save()
            return read()
        else:
            return error(Code.ALREADY_EXIST)
    return error(Code.MISSING_PARAMETER)


def read():
    try:
        data = serializers.serialize('json', OnOff.objects.all())
        return json.loads('{"sensors": ' + data + '}')
    except OnOff.DoesNotExist:
        return error(Code.NOT_FOUND)


def update(id_port=None, name="Light", port=None):
    if id_port is not None and port is not None:
        try:
            # Check if already exist an entity in the destiny port
            OnOff.objects.get(port=port)
            return error(Code.ALREADY_EXIST)
        except OnOff.DoesNotExist:
            try:

                entity = OnOff.objects.get(port=id_port)
                delete(id_port)
                entity.name = name
                entity.port = port
                entity.save()
                return read()
            except OnOff.DoesNotExist:
                return error(Code.NOT_FOUND)
    return error(Code.MISSING_PARAMETER)


def delete(id_port=None):
    if id_port is not None:
        try:
            entity = OnOff.objects.get(port=id_port)
            entity.delete()
            return read()
        except OnOff.DoesNotExist:
            return error(Code.NOT_FOUND)
    return error(Code.MISSING_PARAMETER)


def on(id_port=None, timer=0):
    if id_port is not None:
        try:
            entity = OnOff.objects.get(port=id_port)
            entity.is_on = True
            entity.timer = timer
            entity.save()
            # TODO Turn on the light
            '''
            try:
                gpio.output(id_port, gpio.HIGH)
            except:
                # Can not turn on the sensor
                pass
            '''
            print("Light " + entity.name + " is on")
            if timer != 0:
                _thread.start_new_thread(off_timer, (id_port, timer))

            return read()
        except OnOff.DoesNotExist:
            return error(Code.NOT_FOUND)
    return error(Code.MISSING_PARAMETER)


def off(id_port=None):
    if id_port is not None:
        try:
            entity = OnOff.objects.get(port=id_port)
            entity.is_on = False
            entity.timer = 0
            entity.save()
            # TODO Turn off the light
            '''
            try:
                gpio.output(id_port, gpio.LOW)
            except:
                # Can not turn off the sensor
                pass
            '''
            print("Light " + entity.name + " is off")
            return read()
        except OnOff.DoesNotExist:
            return error(Code.NOT_FOUND)
    return error(Code.MISSING_PARAMETER)


def error(code):
    return {"error": code}


def off_timer(id_port, timer):
    time.sleep(timer)
    # off(id_port)
    # TODO Must return that the light is off
