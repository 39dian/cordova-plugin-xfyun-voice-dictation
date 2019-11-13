# cordova-plugin-xfyun-voice-dictation

## Xfyun Voice Dictation

I creted this plugin to provide a bridge to the Voice Dictation engine of Xfyun for chinese Speech Recognition feature on cordova-like platforms (Cordova, Ionic, ...)

This repo has been forked from the original project of @Edc-zhang.

### Supported Platforms

- iOS
- Android

## Installation

### On Cordova

cordova plugin add https://github.com/jackie-d/cordova-plugin-xfyun-voice-dictation

### On Ionic v. 4

ionic cordova plugin add https://github.com/jackie-d/cordova-plugin-xfyun-voice-dictation

## Configuration

First, register to [Xyfun Console](https://console.xfyun.cn/services/iat) and get an App ID key for your app, you'll need the service: 'Speech Recognition > Voice Dictation'.

Then, inside your app `config.xml` file, in general or Android part, specify your key:

```
<preference name="xfyun_app_id_android" value="<YOUR_KEY>">
```

## Basic Usage

### Typescript

On the top of any `.ts` file: 

```
declare let xyfunVoiceDictation: any;
```

To start listen:

```
xyfunVoiceDictation.startListen(
    text => {
        console.log('Listened:', text);
    },
    error => {
        console.log('Error:', error);
    },
    false, // show dialog
    false // report puntuaction
);
```

To stop listen:

```
xyfunVoiceDictation.stopListen();
```

## Credits

Forked from: https://github.com/Edc-zhang/cordova-plugin-IFlyspeech

Original name: cordova-plugin-IFlyspeech