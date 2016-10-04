import re


def clean_msg(wiki_data):
    regEx = re.compile(r'([^\(]*)\([^\)]*\) *(.*)')
    m = regEx.match(wiki_data)
    while m:
        wiki_data = m.group(1) + m.group(2)
        m = regEx.match(wiki_data)
    wiki_data = wiki_data.replace("'", "")
    return wiki_data