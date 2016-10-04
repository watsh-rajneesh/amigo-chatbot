import requests,sys
from bs4 import BeautifulSoup
from SenseCells.tts import tts
from GreyMatter.utils import clean_msg

# Google News
fixed_url = 'https://news.google.com/'
news_headlines_list = []
news_details_list = []

r = requests.get(fixed_url)
data = r.text

soup = BeautifulSoup(data, "html.parser")

for news_body in soup.find_all("td", "esc-layout-article-cell"):
        title = news_body.find("span", "titletext")
        detail = news_body.find("div", "esc-lead-snippet-wrapper")
        if title is not None:
            for span in title:
                news_headlines_list.append(span)
        # TBD - add news details as well

#news_headlines_list_small = [element.lower().replace("(", "").replace(")", "").replace("'", "") for element in news_headlines_list]
#news_details_list_small = [element.lower().replace("(", "").replace(")", "").replace("'", "") for element in news_details_list]

#news_dictionary = dict(zip(news_headlines_list_small, news_details_list_small))

def news_reader():
    for value in news_headlines_list:
        tts('Headline, ' + clean_msg(value).encode('utf8'))
        #tts('News, ' + value)
