FROM python:3
MAINTAINER Watsh Rajneesh <watsh.rajneesh@sjsu.edu>

WORKDIR /pycli

ADD ./amigo.py /pycli
ADD ./requirements.txt /pycli

RUN pip install -r requirements.txt
CMD ["python", "./amigo.py"]