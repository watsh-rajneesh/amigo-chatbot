"""
Amigo CLI.

Install Python 3 for your OS.
Create virtual environment as: python3 -m venv venv
Activate virtual environment: source venv/bin/activate
Get the following packages:
pip install requests

Now execute the CLI script as:
venv/bin/python ./amigo.py list-users

Do a -h for complete list of commands.

"""
import sys
import argparse
import textwrap
import datetime
import json
import pprint

import requests
from requests.auth import HTTPBasicAuth

"""
Global Constants
"""
HTTPS = 'false'
USER = 'watsh.rajneesh@sjsu.edu'
PASSWORD='pass'


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



def list_users(hostPort):
    """
    List all users.

    :param hostPort:
    :return:
    """
    url = "%s://%s/api/v1.0/users" % (get_http_scheme(), hostPort)
    return get_request(url)


def list_user(hostPort, id):
    """
    Get user by email.

    :param hostPort:
    :param id:
    :return:
    """
    url = "%s://%s/api/v1.0/users/%s" % (get_http_scheme(), hostPort, id)
    return get_request(url)


def create_user(hostPort, payload):
    """
    Create a new user.

    :param hostPort:
    :param payload:
    :return:
    """
    if payload is None:
        print("Please specify the json file with -f option")
        sys.exit(1)
    url = "%s://%s/api/v1.0/users" % (get_http_scheme(), hostPort)
    return post_request(url, payload, auth=None)


def update_user(hostPort, id, payload):
    """
    Update existing user identified by email.

    :param hostPort:    endpoint host and port
    :param id:          user email
    :param payload:     update json payload
    :return:
    """
    if payload is None:
        print("Please specify the json file with -f option")
        sys.exit(1)
    url = "%s://%s/api/v1.0/users/%s" % (get_http_scheme(), hostPort, id)
    return put_request(payload, url)


def delete_user(hostPort, id):
    """
    Delete existing user identified by email.

    :param hostPort:
    :param id:
    :return:
    """
    url = "%s://%s/api/v1.0/users/%s" % (get_http_scheme(), hostPort, id)
    return delete_request(url, headers={"content-type":"application/json"})


def send_msg(hostPort, message):
    pass


def get_status(hostPort, request_id):
    pass


def main():
    """
    Amigo CLI main program.

    :return:
    """
    parser = argparse.ArgumentParser(description=textwrap.dedent('''\
            !!!Amigo CLI!!!
            ----------------------------------
            Note: All commands below require basic authentication (except create-user) so you need to specify
            user and password with -u and -p options.

            Supported commands are:
                1. User Service Commands:
                    1.1 list-users
                    1.2 list-user -i <user-email>
                    1.3 create-user -f create_user.json
                    1.4 delete-user -i <user-email>
                    1.5 update-user -i <user-email> -f update_user.json
                2. Chatbot Service Commands:
                    2.1 send-msg -m <message>
                3. Command Processor Service Commands:
                    3.1 get-status -r <requestId>
    '''), formatter_class=argparse.RawDescriptionHelpFormatter)
    # Required args
    parser.add_argument('command', help="Amigo CLI Commands.See supported commands above.",
                        default='list-users')

    # Optional args
    parser.add_argument('-e', '--endpoint', help='REST endpoint <host:port>, default=localhost:8080',
                        default='localhost:8080')

    parser.add_argument('-v', '--version', action='version', version='%(prog)s 1.0')

    parser.add_argument('-i', '--id', help='User Email')
    parser.add_argument('-m', '--message', help='Message Text')
    parser.add_argument('-f', '--file', help='JSON payload file')
    parser.add_argument('-r', '--reqid', help='Request ID')

    parser.add_argument('-H', '--https', help='Use https, default=false', default='false')

    creds_group = parser.add_argument_group("Credentials Group")
    creds_group.add_argument('-u', '--user', help='user', default='watsh.rajneesh@sjsu.edu')
    creds_group.add_argument('-p', '--password', help='password', default='password')

    args = parser.parse_args()

    command = args.command
    hostPort = args.endpoint
    payload = None
    if args.file is not None:
        with open(args.file, 'r') as myFile:
            payload = eval(myFile.read())

    global HTTPS
    global USER
    global PASSWORD

    HTTPS = args.https
    if args.user is not None:
        USER = args.user
    if args.password is not None:
        PASSWORD = args.password

    print("Executed at:%s" % (datetime.datetime.now()))

    if command == "list-users":
        list_users(hostPort)
    elif command == "list-user":
        list_user(hostPort, id=args.id)
    elif command == "create-user":
        create_user(hostPort, payload)
    elif command == "update-user":
        update_user(hostPort, id=args.id, payload=payload)
    elif command == "delete-user":
        delete_user(hostPort, id=args.id)
    elif command == "send-msg":
        send_msg(hostPort, message=args.message)
    elif command == "get-status":
        get_status(hostPort, request_id=args.reqid)
    else:
        print("Unknown command")

if __name__ == '__main__':
    main()