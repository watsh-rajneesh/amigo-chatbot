"""
RIA Bot interactions

"""
import json
import pprint

import requests
from requests.auth import HTTPBasicAuth


"""
Global Constants
"""
HTTPS = 'false'
USER='anonymous'
PASSWORD=''


def replace_value_with_definition(current_dict, key_to_find, definition):
    """
    This method is used to substitute the default values read from .json file
    by a value that the user specified on CLI.

    :param current_dict:
    :param key_to_find:
    :param definition:
    :return:
    """
    for key in current_dict.keys():
        if key == key_to_find:
            current_dict[key] = definition

def get_http_scheme():
    """
    Gets the http/https protocol scheme prefix for url
    :return:
    """
    if HTTPS.lower() == 'true':
        return "https"
    else:
        return "http"


def pretty_print_json(response):
    """
    Pretty print the response.
    """
    print("Response Headers:%s" % (response.headers))
    print("Reason:%s" % (response.reason))
    try:
        res = json.dumps(response.json(), sort_keys=True, indent=4, separators=(',', ':'))
        print("[HTTPCode:%s]JSON:" % (response.status_code))
        print(res)
    except:
        if response is not None:
            print("[HTTPCode:%s]Text:[%s]" % (response.status_code, response.text))
        else:
            print("Noresponse.")


#################### HTTP method wrapper functions ################

def get_request(url, headers=None):
    """
    GETrequest.
    :paramurl:
    :return:
    """
    print('GET ' + str(url))
    if headers is not None:
        print('Request Headers:' + str(headers))
        r = requests.get(url, auth=HTTPBasicAuth(USER, PASSWORD), headers=headers, verify=False)
    else:
        r = requests.get(url, auth=HTTPBasicAuth(USER, PASSWORD), verify=False)
        pretty_print_json(r)
    return r


def put_request(payload, url, headers=None):
    """
    PUT request.
    :param headers:
    :param payload:
    :param url:
    :return:
    """
    print('PUT ' + str(url))
    print('RequestHeaders:' + str(headers))
    pp = pprint.PrettyPrinter(indent=2)
    pp.pprint(payload)
    r = requests.put(url, auth=HTTPBasicAuth(USER, PASSWORD), data=json.dumps(payload), headers=headers, verify=False)
    pretty_print_json(r)
    return r


def post_request(url, payload=None, headers=None, auth=HTTPBasicAuth(USER, PASSWORD)):
    """
    POST request.
    :param url:
    :param payload:
    :param headers:
    :return:
    """
    print('POST ' + str(url))
    if payload is None and headers is None:
        r = requests.post(url, auth=auth, verify=False)
    else:
        print('RequestHeaders:' + str(headers))
    pp = pprint.PrettyPrinter(indent=2)
    pp.pprint(payload)
    r = requests.post(url, auth=auth, data=json.dumps(payload), headers=headers, verify=False)
    pretty_print_json(r)
    return r

def delete_request(url, headers=None):
    """
    DELETE request.
    :param url:
    :return:
    """
    print('DELETE ' + str(url))
    r = requests.delete(url, auth=HTTPBasicAuth(USER, PASSWORD), headers=headers, verify=False)
    pretty_print_json(r)
    return r

######################################################################################################

def send_message(hostPort, speech_txt, riaId):
    """
    Sends message to Amigo chatbot service.
    Waits for the response in a loop until the response is success or failed.
    
    :param speech_txt: actual message spoken by user
    :param riaId: unique id of RIA device that identifies an Amigo user to the backend
    :return: generic message of success or failure of submission of the message to amigo bot
    """
    url = "%s://%s/api/v1.0/ria" % (get_http_scheme(), hostPort)
    data = {}
    data['botType'] = 'RIA'
    data['content'] = speech_txt
    data['riaId'] = riaId
    json_data = json.dumps(data)
    r = post_request(url, payload=json_data, headers={"content-type":"application/json"})


def poll_response(hostPort)