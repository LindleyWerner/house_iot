from channels import route
from .consumers import message_from_client


websocket_routing = [
    route('websocket.receive', message_from_client),
]
