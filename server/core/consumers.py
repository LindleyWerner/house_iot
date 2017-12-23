from core.actions import action

# TODO send messages to all group (see chat example)


def message_from_client(message):
    print("\nMessage: ", end="")
    print(message.content['text'])

    message.reply_channel.send({
        "text": action(message.content['text'])
    })
