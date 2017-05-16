import speech_recognition as sr
import sys, os, time
import yaml
from GreyMatter.SenseCells.tts import tts
from GreyMatter import play_music

from brain import brain

profile = open('profile.yaml')
profile_data = yaml.safe_load(profile)
profile.close()

# Functioning Variables
name = profile_data['name']
city_name = profile_data['city_name']
city_zip = str(profile_data['city_zip'])
music_path = profile_data['music_path']
access_token = profile_data['twitter']['access_token']
access_token_secret = profile_data['twitter']['access_token_secret']
consumer_key = profile_data['twitter']['consumer_key']
consumer_secret = profile_data['twitter']['consumer_secret']
amigo_host_port = profile_data['amigo']['host_port']
play_music.mp3gen(music_path)



tts('Welcome ' + name + ', systems are now ready to run. How can I help you?')

def main():
    r = sr.Recognizer()
    while True:
        # obtain audio from the microphone
        with sr.Microphone() as source:
            print("Say something!")
            audio = None
            speech_text = None
            while audio is None:
                audio = r.listen(source)
                time.sleep(1)

        # recognize speech using Google Speech Recognition
        try:
            # for testing purposes, you're just using the default API key
            # to use another API key, use `r.recognize_google(audio, key="GOOGLE_SPEECH_RECOGNITION_API_KEY")`
            # instead of `r.recognize_google(audio)`
            speech_text = r.recognize_google(audio)
            print("Ria thinks you said " + speech_text)
        except sr.UnknownValueError:
            #print("Google Speech Recognition could not understand audio")
            pass
        except sr.RequestError as e:
            print("Could not request results from Google Speech Recognition service;{0}".format(e))

        if speech_text is not None:
            brain(name, speech_text, music_path, city_name, city_zip,
                consumer_key, consumer_secret, access_token, access_token_secret, amigo_host_port)

main()