from channels import include

channel_routing = [
    include('core.routing.websocket_routing'),
]
"""from channels.routing import route
from core.consumers import message_from_client
channel_routing = [
    route('websocket.receive', message_from_client),
    #route("http.request", "myapp.consumers.http_consumer"),
]"""