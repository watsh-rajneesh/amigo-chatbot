import re
import wikipedia
from SenseCells.tts import tts


def define_subject(speech_text):
    words_of_message = speech_text.split()
    # change all words to lower text for case insensitive matching
    words_of_message[:] = [word.lower() for word in words_of_message]
    words_of_message.remove('define')
    cleaned_message = ' '.join(words_of_message)
    try:
        wiki_data = wikipedia.summary(cleaned_message, sentences=5)
        regEx = re.compile(r'([^\(]*)\([^\)]*\) *(.*)')
        m = regEx.match(wiki_data)
        while m:
            wiki_data = m.group(1) + m.group(2)
            m = regEx.match(wiki_data)
        wiki_data = wiki_data.replace("'", "")
        # Encode the text from wikipedia in utf8 so it is parsable by tts engine
        # see http://stackoverflow.com/a/5387966/2133539
        tts(wiki_data.encode('utf8'))
    except wikipedia.exceptions.DisambiguationError as e:
        tts('Can you please be more specific?')
        print("Can you please be more specific? You may choose something from the following.; {0}".format(e))