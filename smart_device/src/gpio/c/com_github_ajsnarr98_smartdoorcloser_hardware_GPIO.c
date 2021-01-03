#include <jni.h>
/* Header for class com_ajsnarr_hardware_GPIO */

#ifndef _Included_com_ajsnarr_hardware_GPIO
#define _Included_com_ajsnarr_hardware_GPIO

#include <stdio.h>
#include <string.h>

#include <pigpio.h>

#ifdef __cplusplus
extern "C" {
#endif

#define VERBOSE 0
#define DEBUG 1
#define INFO 2
#define ERROR 3

int logLevel;

/*
 * Class:     com_github_ajsnarr98_smartdoorcloser_hardware_GPIO
 * Method:    _setLogLevel
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_smartdoorcloser_hardware_GPIO__1setLogLevel
  (JNIEnv *env, jclass clz, jint logLvl) {
    logLevel = logLvl;
    return logLevel;
  }

/*
 * Class:     com_github_ajsnarr98_smartdoorcloser_hardware_GPIO
 * Method:    _initialize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_smartdoorcloser_hardware_GPIO__1initialize
  (JNIEnv *env, jclass clz) {
    if (logLevel <= INFO) printf("Native: Initializing pigpio\n");
    return gpioInitialise();
  }

/*
 * Class:     com_github_ajsnarr98_smartdoorcloser_hardware_GPIO
 * Method:    _terminate
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_smartdoorcloser_hardware_GPIO__1terminate
  (JNIEnv *env, jclass clz) {
    if (logLevel <= INFO) printf("Native: Terminating pigpio\n");
    gpioTerminate();
    return 0;
  }

/*
 * Class:     com_github_ajsnarr98_smartdoorcloser_hardware_GPIO
 * Method:    _setMode
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_smartdoorcloser_hardware_GPIO__1setMode
  (JNIEnv *env, jclass clz, jint gpio, jint mode) {
    if (logLevel <= DEBUG) printf("Native: gpioSetMode\n");
    return gpioSetMode((unsigned) gpio, (unsigned) mode);
  }

/*
 * Class:     com_github_ajsnarr98_smartdoorcloser_hardware_GPIO
 * Method:    _getmode
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_smartdoorcloser_hardware_GPIO__1getmode
  (JNIEnv *env, jclass clz, jint gpio) {
    if (logLevel <= DEBUG) printf("Native: gpioGetMode\n");
    return gpioGetMode((unsigned) gpio);
  }

/*
 * Class:     com_ajsnarr_hauntedgameboard_hardware_GPIO
 * Method:    _read
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_smartdoorcloser_hardware_GPIO__1read
  (JNIEnv *env, jclass clz, jint gpio) {
    if (logLevel <= DEBUG) printf("Native: gpioRead\n");
    return gpioRead((unsigned) gpio);
  }

/*
 * Class:     com_github_ajsnarr98_smartdoorcloser_hardware_GPIO
 * Method:    _write
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_smartdoorcloser_hardware_GPIO__1write
  (JNIEnv *env, jclass clz, jint gpio, jint level) {
    if (logLevel <= DEBUG) printf("Native: gpioWrite\n");
    return gpioWrite((unsigned) gpio, (unsigned) level);
  }

/*
 * Class:     com_github_ajsnarr98_smartdoorcloser_hardware_GPIO
 * Method:    _waveClear
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_smartdoorcloser_hardware_GPIO__1waveClear
  (JNIEnv *env, jclass clz) {
    if (logLevel <= DEBUG) printf("Native: gpioWaveClear\n");
    int result = gpioWaveClear();
    gpioWaveTxStop(); // unsure if gpioWaveClear also aborts current waveform
    return result;
  }

/**
  Generate ramp wave forms.
  nramps: size of two arrays
  rampFreqs:  Array of frequencies for each wave
  rampNSteps: Parallel array of number of steps for each wave
  */
int generate_ramp(jint gpio, jsize nramps, jint *rampFreqs, jint *rampNSteps) {
  
  gpioWaveClear(); // clear existing waves
  int wid[nramps]; 
  
  // Generate a wave per ramp level
  for (int i=0; i<nramps; i++) {
      
      jint frequency = rampFreqs[i];
      int micros = 500000 / frequency;
      
      // configure waveform
      gpioPulse_t wf[2];
      
      // on
      wf[0].gpioOn = (1<<gpio);
      wf[0].gpioOff = 0;
      wf[0].usDelay = micros;
      
      // off
      wf[1].gpioOn = 0;
      wf[1].gpioOff = (1<<gpio);
      wf[1].usDelay = micros;
      
      // create waveform
      gpioWaveAddNew();
      gpioWaveAddGeneric(2, wf);
      wid[i] = gpioWaveCreate();
  }

  // Generate a chain of waves
  int loop_size = 7;
  int chain_size = loop_size * nramps;
  
  char chain[chain_size];
  char *pos = chain;
  
  for (int i=0; i<nramps; i++) {
      jint steps = rampNSteps[i];
      jint x = steps & 255;
      jint y = steps >> 8;
      char loop[] = {255, 0, wid[i], 255, 1, x, y};
      memcpy(pos, loop, loop_size);
      pos += loop_size;
  }
  
  return gpioWaveChain(chain, chain_size); // Transmit chain.
}

/*
 * Class:     com_github_ajsnarr98_smartdoorcloser_hardware_GPIO
 * Method:    _waveRamps
 * Signature: (I[I[I)I
 */
JNIEXPORT jint JNICALL Java_com_github_ajsnarr98_smartdoorcloser_hardware_GPIO__1waveRamps
  (JNIEnv *env, jclass clz, jint gpio, jintArray rampFreq, jintArray rampNSteps) {
    
    if (logLevel <= DEBUG) printf("Native: waveRamps\n");
    
    jsize len = (*env)->GetArrayLength(env, rampFreq);
    jint *freqs = (*env)->GetIntArrayElements(env, rampFreq, 0);
    jint *steps = (*env)->GetIntArrayElements(env, rampNSteps, 0);
    
    int result = generate_ramp(gpio, len, freqs, steps);
    
    // clear allocated arrays (garbage collector freezes them once GetIntArrayElements is called)
    (*env)->ReleaseIntArrayElements(env, rampFreq, freqs, 0);
    (*env)->ReleaseIntArrayElements(env, rampNSteps, steps, 0);
    
    return result;
  }

/*
 * Class:     com_github_ajsnarr98_smartdoorcloser_hardware_GPIO
 * Method:    _waveIsBusy
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_github_ajsnarr98_smartdoorcloser_hardware_GPIO__1waveIsBusy
  (JNIEnv *env, jclass clz) {
    if (logLevel <= VERBOSE) printf("Native: gpioWaveTxBusy\n");
    return gpioWaveTxBusy();
  }

#ifdef __cplusplus
}
#endif
#endif
