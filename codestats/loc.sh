#!/usr/bin/env bash
git clone --depth 1 "https://github.com/sjsucohort6/amigo-chatbot.git" temp-linecount-repo &&
  printf "('temp-linecount-repo' will be deleted automatically)\n\n\n" &&
  cloc temp-linecount-repo > loc.txt &&
  rm -rf temp-linecount-repo

