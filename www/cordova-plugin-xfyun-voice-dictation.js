var exec = require('cordova/exec');

var xyfunVoiceDictation = {
	startListen:function (success,error,language,isShowPunc){
		exec(success,error,"XunfeiListenSpeaking","startListen",[isShowDialog,isShowPunc]);
	},
	stopListen:function(){
		exec(null,null,"XunfeiListenSpeaking","stopListen",[]);
	}
};

module.exports = xyfunVoiceDictation;