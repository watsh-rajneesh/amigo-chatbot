# ria (Raspberry Pi Intelligent Assistant)
##  A Virtual assistant for Raspberrry Pi

It is made following the nice book on the subject by Tanay Pant - [Building a Virtual Assistant for Raspberry Pi (APress)](https://www.amazon.com/Building-Virtual-Assistant-Raspberry-voice-controlled/dp/1484221664)

This is similar though much simplified version of products like [Google Home](https://madeby.google.com/home/) or [Amazon Echo](https://www.amazon.com/echo).

The idea is to use the Raspberry Pi based Virtual Assistant as one of the interfaces for [Amigo chatbot project](https://github.com/sjsucohort6/amigo-chatbot-aws).

Ria uses Google STT (Speech to text) API and espeak on Linux (or say on OSX) for TTS (Text to speech).

Currently supported [skills](https://developer.amazon.com/alexa-skills-kit)/features:
  1. Play music from configured path on local disk
  2. Read news headlines scrapping news from news.google.com
  3. Take notes and store in local sqllite DB
  4. Tell time
  5. Tell local weather of configured city
  6. Post tweets on twitter
  7. Look up wikipedia and read a short description for any term, person or place (anything on wikipedia).


To add in future:
  1. Make adding extensions for Ria pluggable at runtime.
  2. Integrate with Amigo chatbot service so Ria can be used for voice ops management.
  3. Use blinky(1) to show the state of the last response


##To setup the environment do the following:
```
virtualenv venv
source venv/bin/activate
pip install -r requirements.txt
cp profile.yaml.default profile.yaml
cp memory.db.default memory.db
```
Some dependencies are not installed using above pip command and require some extra steps.

On Mac OSX:
```
### install portaudio and pyaudio
sudo chown -R $(whoami) /usr/local
brew install portaudio
sudo chown root:wheel /usr/local
pip install --global-option='build_ext' --global-option='-I/usr/local/include' --global-option='-L/usr/local/lib' pyaudio

### install pywapi
wget https://launchpad.net/python-weather-api/trunk/0.3.8/+download/pywapi-0.3.8.tar.gz
tar -xvzf pywapi-0.3.8.tar.gz
cd pywapi-0.3.8
python setup.py build
python setup.py install
```

Edit the profile.yaml for your environment.

Then run the app as:
```
python ./main.py
```

# Setting up on Raspberry Pi

```
ssh pi@<ipaddress of raspberry pi host>
password : (default is raspberry)

$ git clone https://github.com/sjsucohort6/ria.git
$ sudo pip install –r requirements.txt --allow-external pywapi --allow-unverified pywapi


# taken from http://raspberrypihell.blogspot.pt/2013/07/pyaudio-and-how-to-install.html

sudo apt-get install git  
sudo git clone http://people.csail.mit.edu/hubert/git/pyaudio.git  
sudo apt-get install libportaudio0 libportaudio2 libportaudiocpp0 portaudio19-dev  
sudo apt-get install python-dev  
cd pyaudio  
sudo python setup.py install
```
