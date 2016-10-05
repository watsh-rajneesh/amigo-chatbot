from GreyMatter import general_conversations, tell_time, \
    weather, define_subject, news_reader, open_firefox, sleep, \
    play_music, notes, twitter_interaction

def brain(name, speech_text, music_path, city_name, city_zip, consumer_key, consumer_secret, access_token, access_token_secret):
    """
    Virtual Assistant's logic module or BRAIN.

    :param name:
    :param speech_text:
    :param music_path:
    :param city_name:
    :param city_zip:
    :param consumer_key:
    :param consumer_secret:
    :param access_token:
    :param access_token_secret:
    :return:
    """
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

    if check_message(['who', 'are', 'you']) or check_message(["what's", 'your', 'name']):
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
    elif check_message(['news']):
        news_reader.news_reader()
    elif check_message(['open', 'firefox']):
        open_firefox.open_firefox()
    elif check_message(['play', 'music']) or check_message(['music']):
        play_music.play_random(music_path)
    elif check_message(['play']):
        play_music.play_specific_music(speech_text, music_path)
    elif check_message(['party', 'time']) or check_message(['party', 'mix']):
        play_music.play_shuffle(music_path)
    elif check_message(['note']):
        notes.note_something(speech_text)
    elif check_message(['all', 'notes']) or check_message(['notes']):
        notes.show_all_notes()
    elif check_message(['tweet']):
        twitter_interaction.post_tweet(speech_text, consumer_key,
                                       consumer_secret, access_token, access_token_secret)
    elif check_message(['sleep']):
        sleep.go_to_sleep()
    else:
        general_conversations.undefined()

