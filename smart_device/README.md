# Requirements
Note: this is intended to be on a raspberry pi.

1. Java 8 or higher (with ${JAVA_HOME} set)
1. The pigpio library (for raspberry pi GPIO pins)
1. Internet connection

# Installation/Setup

1. Download repo
1. Fill `setup_then_run.sh` with your aws access keys
1. Run `create_so.sh` to build the shared library for the device
1. build using ./gradlew
1. Run `setup_then_run.sh`
