from core.activities import on_off
from core.codes import Code
import json


def action(message):
    d = decode(message)

    try:
        if d["target"] in "on_off":
            if d["action"] in "create":
                return encode(on_off.create(d["name"], int(d["port"])))
            elif d["action"] in "read":
                return encode(on_off.read())
            elif d["action"] in "update":
                return encode(on_off.update(int(d["id"])), d["name"], int(d["port"]))
            elif d["action"] in "delete":
                return encode(on_off.delete(int(d["id"])))
            elif d["action"] in "on":
                return encode(on_off.on(int(d["id"]), int(d["port"])))
            elif d["action"] in "off":
                return encode(on_off.off(int(d["id"])))
            else:
                print("None of options above 2")
                return encode({"error": Code.NO_OPTION})
        elif d["target"] in "logout":
                print("Logout")
                return ""
        else:
            print("None of options above 1")
            return encode({"error": Code.NO_OPTION})
    except KeyError:
        return encode({"error": Code.KEY_ERROR})


def decode(message):
    return json.loads(message)


def encode(dictionary):
    return json.dumps(dictionary)
