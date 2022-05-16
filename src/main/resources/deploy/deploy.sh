#!/bin/bash

BRANCH=master

while [[ $# -gt 0 ]]
do
  key="$1"

  case $key in
    -b|--branch)
    BRANCH=$2
    shift
    shift
    ;;
  esac
done

git push heroku $BRANCH