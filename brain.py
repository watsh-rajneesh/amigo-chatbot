from GreyMatter import general_conversations, tell_time, weather, define_subject

def brain(name, speech_text, city_name, city_zip):
    def check_message(check):
        """
        This function checks if the items in the list (specified in
        argument) are present in the user's input speech.
        :param check:
        :return:
        """
        words_of_message = speech_text.split()
        # change all words to lower text for case insensitive matching
        words_of_message[:] = [word.lower() for word in words_of_message]

        if set(check).issubset(set(words_of_message)):
            return True
        else:
            return False

    if check_message('who are you'):
        general_conversations.who_are_you()
    elif check_message(['how', 'i', 'look']) or check_message(['how', 'am', 'i']):
        general_conversations.how_am_i()
    elif check_message(['tell', 'joke']):
        general_conversations.tell_joke()
    elif check_message(['who', 'am', 'i']):
        general_conversations.who_am_i(name)
    elif check_message(['where', 'born']):
        general_conversations.where_born()
    elif check_message(['how', 'are', 'you']):
        general_conversations.how_are_you()
    elif check_message(['time']):
        tell_time.what_is_time()
    elif check_message(['how', 'weather']) or \
            check_message(['how\'s', 'weather']) or \
            check_message(['what', 'weather']):
        weather.weather(city_name, city_zip)
    elif check_message(['define']):
        define_subject.define_subject(speech_text)
    else:
        general_conversations.undefined()

