# cordova-plugin-xfyun-voice-dictation

## Xfyun Voice Dictation

I creted this plugin to provide a bridge to the Voice Dictation engine of Xfyun for chinese Speech Recognition feature on cordova-like platforms (Cordova, Ionic, ...)

This is a Speech Recognition (aka Voice Recognition) system that's provided by Xfyun in China.

This repo has been forked from the original project of @Edc-zhang.

## 语音识别 > 语音听写（流式版）

科大讯飞的语音听说读写的cordova插件

我创建了这个插件，为Xfyun的语音听写引擎提供了桥梁，以便在类似于Cordova的平台（Cordova，Ionic等）上实现中文语音识别功能。

这是Xfyun在中国提供的语音识别（又称语音识别）系统。

### Supported Platforms

- Android
- ~~iOS~~

iOs support has been removed, but you can use 

```<plugin name="cordova-plugin-speechrecognition" spec="~1.1.2" />```

that works in all the world on iOs.

## Installation

### On Cordova

cordova plugin add https://github.com/jackie-d/cordova-plugin-xfyun-voice-dictation

### On Ionic v. 4

ionic cordova plugin add https://github.com/jackie-d/cordova-plugin-xfyun-voice-dictation

## Configuration

First, register to [Xyfun Console](https://console.xfyun.cn/services/iat) and get an App ID key for your app, you'll need the service: 'Speech Recognition > Voice Dictation'.

Then, inside your app `config.xml` file, in general or Android part, specify your key:

```
<preference name="xfyun_app_id_android" value="<YOUR_KEY>" />
```

## Permission

### Android

In order to allow the Speech Recognitin to work, you have to request the RECORD_AUDIO permission to Android.

You may use the following ionic plugin: 

```
<plugin name="cordova-plugin-android-permissions" spec="~1.0.0" />
```

and the following code prior to Speech Recognition execution:

```
    constructor(
        private androidPermissions: AndroidPermissions //, ...
    ) {
        this.initPermission();
    }

    initPermission() {
        this.androidPermissions.checkPermission( this.androidPermissions.PERMISSION.RECORD_AUDIO ).then(
            result => {
                if ( result.hasPermission ) {
                    this.init();
                } else {
                    this.requestPermission();
                }
            },
            error => {
                this.requestPermission();
            }
        );
    }

    requestPermission() {
        this.androidPermissions.requestPermission(this.androidPermissions.PERMISSION.RECORD_AUDIO)
            .then(success => {
                this.init();
            }, reqError => {
                // error, permission is required
            });
    }

    init() {
        // init speech recognition
    }
```

in case you're not on Ionic, search google for 'cordova android permission'.

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
    'en_us', // language to recognize
    false // report puntuaction
);
```

To stop listen:

```
xyfunVoiceDictation.stopListen();
```

## Language Support

Current support it's only for the following languages:

`{ "zh_cn", "en_us" }`

## Credits

Forked from: https://github.com/Edc-zhang/cordova-plugin-IFlyspeech

Original name: cordova-plugin-IFlyspeech