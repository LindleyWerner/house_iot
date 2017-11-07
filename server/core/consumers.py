"""def message_from_client(message):
    message.reply_channel.send({
            "text": message.content['text'],
        })"""


def message_from_client(message):
    try:
        print("\nMensagem")
        print(message.content['text'])

        message.reply_channel.send({
            "text": message.content['text'],
        })
    except:
        print("ERRO")
