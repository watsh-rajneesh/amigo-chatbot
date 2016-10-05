# ria (Raspberry Pi Intelligent Assistant)
##A Virtual assistant for Raspberrry Pi

It is made following the nice book on the subject by Tanay Pant - Building a Virtual Assistant for Raspberry Pi (APress) [https://www.amazon.com/Building-Virtual-Assistant-Raspberry-voice-controlled/dp/1484221664]

The idea is to use the Raspberry Pi based Virtual Assistant as one of the interfaces for Amigo chatbot project [https://github.com/sjsucohort6/amigo-chatbot-aws].

Ria uses Google STT (Speech to text) API and espeak on Linux (or say on OSX) for TTS (Text to speech).

Currently supported features:
  1. Play music from configured path on local disk
  2. Read news headlines scrapping news from news.google.com
  3. Take notes and store in local sqllite DB
  4. Tell time
  5. Tell local weather of configured city
  6. Post tweets on twitter
  7. Look up wikipedia and read a short description for any term, person or place (anything on wikipedia).


To add in future:
  1. Make adding extensions for Ria pluggable at runtime.
  2. Integrate with Amigo chatbot.


To setup the environment do the following:
```
virtualenv venv
source venv/bin/activate
pip install -r requirements.txt
cp profile.yaml.default profile.yaml
cp memory.db.default memory.db
```

Edit the profile.yaml for your environment.

Then run the app as:
```
python ./main.py
```
