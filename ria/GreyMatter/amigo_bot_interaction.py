

def send_message(speech_txt, intent, riaId):
    """
    Sends message to Amigo chatbot service.
    Waits for the response in a loop until the response is success or failed.
    
    :param speech_txt: actual message spoken by user
    :param intent: intent inferred from the message
    :param riaId: unique id of RIA device that identifies an Amigo user to the backend
    :return: generic message of success or failure of submission of the message to amigo bot
    """
