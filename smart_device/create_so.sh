#!/bin/bash

# this script should be run from the project home directory

# make shared lib
cd src/gpio/c
make

# move to location
cd ../../..
mv src/gpio/c/gpio.so src/lib/gpio.so
