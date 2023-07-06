package com.boll.audiobook.hear.evs.utils

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.boll.audiobook.hear.evs.service.EngineService
import com.iflytek.cyber.evs.sdk.agent.Recognizer
import com.iflytek.cyber.evs.sdk.auth.AuthDelegate
import com.iflytek.cyber.evs.sdk.model.AuthResponse
import com.iflytek.cyber.evs.sdk.model.DeviceCodeResponse
import org.json.JSONObject

class EvsSdk {

    companion object {
        public val TAG = "EvsSdk"
        public var engineService: EngineService? = null

        open fun initAuth(mContext: Context, deviceId: String, clientId: String) {

            val intent = Intent(mContext, EngineService::class.java)
            mContext.startService(intent)
            mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

            val customScopeData = AuthDelegate.SCOPE_DATA_DEFAULT
            AuthDelegate.setAuthUrl(CommonContants.AUTH_BASE_URL)
            AuthDelegate.requestDeviceCode(mContext,
                clientId,
                deviceId,
                CommonContants.THIRD_PARTY_ID,
                AuthDelegate.AuthMode.PRIVACY,
                object : AuthDelegate.ResponseCallback<DeviceCodeResponse> {
                    override fun onResponse(response: DeviceCodeResponse) {
                        Log.d(TAG, "onResponse: $response")
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onError(
                        httpCode: Int?,
                        errorBody: String?,
                        throwable: Throwable?,
                    ) {
                        if (throwable != null) {
                            Log.e(TAG, "onError: " + throwable.message)
                        }
                    }
                },
                object : AuthDelegate.AuthResponseCallback {
                    override fun onAuthSuccess(authResponse: AuthResponse) {
                        val message = authResponse.toString()
                        Log.e(TAG, "onAuthSuccess: 授权成功！")
                        if (engineService?.isEvsConnected == false) {
                            initConnectEvs(mContext, deviceId)
                        }
                    }

                    override fun onAuthFailed(errorBody: String?, throwable: Throwable?) {
                        val message = throwable?.message ?: errorBody
                        Log.e(EvsSdk.TAG, "onAuthFailed: 授权失败！")
                    }

                },
                customScopeData
            )
        }

        open fun initConnectEvs(mContext: Context, deviceId: String) {

            engineService?.getAuthResponse()?.let {
                if (engineService?.getAppAction() != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        engineService?.connectEvs(deviceId)
                    } else {
                        engineService?.connectEvs(deviceId)
                    }
                } else {
                    engineService?.connectEvs(deviceId)
                }
            } ?: run {
                // need auth at first
            }
        }

        private val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                if (service is EngineService.EngineServiceBinder) {
                    engineService = service.getService()
                    engineService?.setTransmissionListener(transmissionListener)
                    engineService?.setRecognizerCallback(recognizerCallback)

                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                engineService?.setTransmissionListener(null)
                engineService?.setRecognizerCallback(null)
                engineService = null
            }
        }

        val transmissionListener = object : EngineService.TransmissionListener {
            override fun onResponsesRaw(json: String) {
                Thread {
                    Log.e(TAG, "onResponsesRaw: " + json)
                    val jsonObject = JSONObject(json)
                    val iflyos_responses = jsonObject.getJSONArray("iflyos_responses")
                    for (index in 0 until iflyos_responses.length()) {
                        val response = iflyos_responses.getJSONObject(index)
                        val header = response.getJSONObject("header")
                        var name = header.getString("name")
                        var standard = "recognizer.evaluate_result"//评测结果
                        if (name.equals(standard, ignoreCase = true)) {
                            voiceEvaluateListener?.onVoiceEvaluate(json)
                        }
                    }
                    val iflyos_meta = jsonObject.getJSONObject("iflyos_meta")
                    //是否是最后一条数据
                    val is_last = iflyos_meta.getBoolean("is_last")
                    if (!is_last) {
                        intermediateListener?.onIntermediateResult(json)
                    } else {
                        audioResultListener?.onAudioResult(json)
                    }
                }.start()
            }

            override fun onRequestRaw(obj: Any) {
                Thread {
                    if (obj is String) {
                        val simpleModel = try {
                            val jsonObject = JSONObject(obj.toString())
                            val iflyosRequest = jsonObject.getJSONObject("iflyos_request")
                            val header = iflyosRequest.getJSONObject("header")
                            val headerName = header.getString("name")
                            var standard = "recognizer.evaluate_result"
                            if (headerName.equals(standard, ignoreCase = true)) {
                                voiceEvaluateListener?.onVoiceEvaluate(obj.toString())
                            } else {

                            }
                        } catch (t: Throwable) {

                        }
                    }
                }.start()
            }
        }

        public val recognizerCallback = object : Recognizer.RecognizerCallback {
            override fun onBackgroundRecognizeStateChanged(isBackgroundRecognize: Boolean) {
                // 背景录音
            }

            override fun onRecognizeStarted(isExpectReply: Boolean) {
                Log.e(EvsSdk.TAG, "onIntermediateText: 识别中")
            }

            override fun onRecognizeStopped() {
                Log.e(EvsSdk.TAG, "onIntermediateText: 识别中")
            }

            override fun onIntermediateText(text: String, isLast: Boolean) {
                Log.e(EvsSdk.TAG, "onIntermediateText: " + text)
            }
        }

        /**
         * 语音识别结果
         */
        interface AudioResultListener {
            fun onAudioResult(json: String);
        }

        /**
         * 语音识别中
         */
        interface IntermediateListener {
            fun onIntermediateResult(json: String);
        }

        /**
         * 语音评测结束
         */
        interface VoiceEvaluateListener {
            fun onVoiceEvaluate(json: String)
        }

        private var voiceEvaluateListener: VoiceEvaluateListener? = null
        fun setVoiceEvaluateListener(listener: VoiceEvaluateListener?) {
            voiceEvaluateListener = listener
        }

        private var audioResultListener: AudioResultListener? = null
        fun setAudioResultListener(listener: AudioResultListener?) {
            audioResultListener = listener
        }

        private var intermediateListener: IntermediateListener? = null
        fun setIntermediateListener(listener: IntermediateListener?) {
            intermediateListener = listener
        }

        fun startEvaluating(language: String, category: String, text: String, enableVad: Boolean) {
//            val language = "en_us"
//            val category = "read_word"
//            var text ="[word]\nbook"
            engineService?.getRecognizer()?.sendEvaluate(language, category, text, enableVad)
        }

        fun stopCapture() {
            engineService?.getRecognizer()?.stopCapture()
        }

        fun sendAudioIn() {
            engineService?.sendAudioIn()
        }

        fun init() {
            engineService?.let {
                it.setTransmissionListener(transmissionListener)
                it.setRecognizerCallback(recognizerCallback)
            }
        }


    }
}