
# MAKEFILE for creating JNI shared library

CC = gcc  # C compiler
CFLAGS = -Wall -O3  # C flags
LDFLAGS = -shared -lpigpio -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux  # linking flags
RM = rm   # rm command
TARGET_LIB = gpio.so  # target lib

SRCS = com_github_ajsnarr98_smartdoorcloser_hardware_GPIO.c # source files

.PHONY: all
all: ${TARGET_LIB}

$(TARGET_LIB): $(SRCS)
	$(CC) ${LDFLAGS} ${CFLAGS} ${SRCS} -o ${TARGET_LIB}

clean:
	-${RM} ${TARGET_LIB} $(SRCS:.c=.d)
