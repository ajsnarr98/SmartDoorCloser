# Requirements
Note: this is intended to be on a raspberry pi.

1. Java 8 or higher (with ${JAVA_HOME} set)
1. cmake
1. The pigpio library (for raspberry pi GPIO pins)
1. libssl
1. AWS IoT is set up (including downloaded certs and private key) (https://docs.aws.amazon.com/iot/latest/developerguide/create-iot-resources.html)
1. Internet connection

# Installation/Setup

1. Download repo
1. Make a file called `src/main/resources/config.json` based on `src/main/resources/config_example.json` and fill it with your relevant secrets
1. Run `create_so.sh` to build the shared library for the device
1. Build using `./gradlew build`
1. Run `run.sh`
