# Sample Dockerfile for installing MongoDB on an Ubuntu 14.04 server
# https://github.com/atbaker/mongo-example

# Use the Trusty (14.04) Ubuntu base image
FROM ubuntu:14.04

# Optionally set the maintainer
MAINTAINER Watsh Rajneesh <watsh.rajneesh@sjsu.edu>

# Following installation instructions from http://docs.mongodb.org/manual/tutorial/install-mongodb-on-ubuntu/

# Import the MongoDB public GPG key
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10

# Create a list file for MongoDB
RUN echo 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen' | sudo tee /etc/apt/sources.list.d/mongodb.list

# Update the local package database
RUN apt-get update

# Install the latest stable version of MongoDB (note -y flag for non-interactive)
RUN apt-get install -y mongodb-org

# Create MongoDB's default data directory (-p creates parent directories - in this case, /data)
RUN mkdir -p /data/db

# Because this database will run in a Docker container, 
# we must configure it to accept connctions from foreign hosts
RUN echo "bind_ip = 0.0.0.0" >> /etc/mongodb.conf

# Expose the MongoDB port
EXPOSE 27017

# Set the default command for this image
CMD ["mongod"]
