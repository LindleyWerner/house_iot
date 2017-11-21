from core.actions import action


def message_from_client(message):
    print("\nMessage: ", end="")
    print(message.content['text'])

    message.reply_channel.send({
        "text": action(message.content['text'])
    })
