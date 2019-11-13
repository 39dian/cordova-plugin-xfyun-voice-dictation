package com.thomas.xunfeilistenspeaking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.iflytek.cloud.*;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Thomas.Wang on 17/2/9.
 */
public class XunfeiListenSpeaking extends CordovaPlugin{

    private static String TAG = XunfeiListenSpeaking.class.getSimpleName();
    private Context context;
    private CallbackContext callbackContext;
    private Toast mToast;
    private Handler mHandler = new Handler();

    private SpeechRecognizer mIat;
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    private String xfyunAppId;

    int ret = 0;

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        context = cordova.getActivity();
        
        this.xfyunAppId = cordova.getActivity().getIntent().getStringExtra("xfyun_app_id_android");
        SpeechUtility.createUtility(context, SpeechConstant.APPID +"="+this.xfyunAppId );
    }

    private int getId(String idName,String type){
        return context.getResources().getIdentifier(idName, type,context.getPackageName());
    }
    private static final int DIALOG_ACTIVIT_CODE = 0;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        this.callbackContext = callbackContext;

        if (action.equals("startListen")){

            String language;
            try{
                language = args.getString(0);
                if ( language == null || language.isEmpty() ) {
                    language = 'en_us';
                }
            }catch (Exception e){
                language = 'en_us';
            }

            String punc;
            try{
                punc = args.getBoolean(1)?"1":"0";
            }catch (Exception e){
                punc = "1";
            }

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    startListen(language, punc);   
                }
            });

            return true;
        }

        if (action.equals("stopListen")) {
            stopListen();
            return true;
        }


        return false;
    }


    private void stopListen(){
        if (mIat!=null&&mIat.isListening()) {
            mIat.stopListening();
        }
    }

    private void startListen(String language, String punc){
        mIat = SpeechRecognizer.createRecognizer(context, mInitListener);

        if (mIat.isListening()) {
            mIat.stopListening();
        }

        mIatResults.clear();

        setParam(language, punc);

        this.ret = mIat.startListening(mRecognizerListener);
        if (this.ret != ErrorCode.SUCCESS) {
            callbackContext.error("听写失败,错误码：" + ret);
        }
    }

    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };

    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            //showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            //showTip(error.getPlainDescription(true));
            //finishThisActivity(RESULT_CANCELED,error.getPlainDescription(true));
            callbackContext.error(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            //showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            printResult(results,isLast);
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            //showTip("当前正在说话，音量大小：" + volume);
            //showTip("当前正在说话...");
            //Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private void printResult(RecognizerResult results, boolean isLast) {
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIatResults.put(sn, text);
        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        Log.d(TAG, "音频中文：" + resultBuffer.toString());
        //mResultText.setText(resultBuffer.toString());
        //mResultText.setSelection(mResultText.length());
        //Intent resultIntent = new Intent();
        //Bundle bundle = new Bundle();
        //bundle.putString("result", resultBuffer.toString());
        //resultIntent.putExtras(bundle);
        //this.setResult(RESULT_OK, resultIntent);
        //finish();
        
        //if (isLast)
        callbackContext.success(resultBuffer.toString());
    }

    /**
     * 参数设置
     *
     * @param
     * @return
     */
    public void setParam(String language, String punc) {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        mIat.setParameter(SpeechConstant.LANGUAGE, language);
        if ( language.equals("zh_cn") ) {
            String chineseAccent = "mandarin";
            mIat.setParameter(SpeechConstant.LANGUAGE, language);
            mIat.setParameter(SpeechConstant.ACCENT, chineseAccent);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, punc);

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, android.content.Context.getCacheDir() + "/msc/iat.wav");
    }

    private void showTip(final String str) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

}
