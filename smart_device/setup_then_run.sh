#!/bin/bash

# Put secret keys here:
export AWS_ACCESS_KEY_ID=#aws access key
export AWS_SECRET_ACCESS_KEY=#aws secret access key
export AWS_DEFAULT_REGION=us-east-1

# ---------------------------------------
java -jar build/libs/smart_device-1.0.jar
