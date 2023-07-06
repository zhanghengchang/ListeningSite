package com.boll.audiobook.hear.evs.service

import android.annotation.SuppressLint
import android.content.*
import android.os.Binder
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import com.boll.audiobook.hear.R
import com.boll.audiobook.hear.evs.utils.CommonContants
import com.boll.audiobook.hear.evs.utils.EvsSdk.Companion.engineService
import com.boll.audiobook.hear.evs.utils.SpUtils
import com.iflytek.cyber.evs.sdk.EvsService
import com.iflytek.cyber.evs.sdk.RequestCallback
import com.iflytek.cyber.evs.sdk.agent.AudioPlayer
import com.iflytek.cyber.evs.sdk.agent.ExternalPlayer
import com.iflytek.cyber.evs.sdk.agent.Recognizer
import com.iflytek.cyber.evs.sdk.agent.VideoPlayer
import com.iflytek.cyber.evs.sdk.auth.AuthDelegate
import com.iflytek.cyber.evs.sdk.model.DeviceLocation
import com.iflytek.cyber.evs.sdk.model.ImageMetaData

class EngineService : EvsService() {
    private val binder = EngineServiceBinder()

    companion object {
        private const val TAG = "EngineService"

        private const val ACTION_PREFIX = "com.iflytek.cyber.evs.demo.action"
        const val ACTION_EVS_CONNECTED = "$ACTION_PREFIX.EVS_CONNECTED"
        const val ACTION_EVS_DISCONNECTED = "$ACTION_PREFIX.EVS_DISCONNECTED"
        const val ACTION_EVS_ERROR = "$ACTION_PREFIX.EVS_ERROR"
        const val ACTION_EVS_CONNECT_FAILED = "$ACTION_PREFIX.EVS_CONNECT_FAILED"
        const val ACTION_EVS_SEND_FAILED = "$ACTION_PREFIX.EVS_SEND_FAILED"
        const val ACTION_CLEAR_AUTH = "$ACTION_PREFIX.CLEAR_AUTH"
        const val EXTRA_CODE = "code"
        const val EXTRA_MESSAGE = "message"
        private val VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION"
    }

    open inner class EngineServiceBinder : Binder() {
        fun getService(): EngineService {
            return this@EngineService
        }
    }

    private var transmissionListener: TransmissionListener? = null
    private val internalRecordCallback = object : Recognizer.RecognizerCallback {
        override fun onBackgroundRecognizeStateChanged(isBackgroundRecognize: Boolean) {
            recognizerCallback?.onBackgroundRecognizeStateChanged(isBackgroundRecognize)
        }

        override fun onRecognizeStarted(isExpectReply: Boolean) {
            recognizerCallback?.onRecognizeStarted(isExpectReply)
        }

        override fun onRecognizeStopped() {
            recognizerCallback?.onRecognizeStopped()
        }

        override fun onIntermediateText(text: String, isLast: Boolean) {
            Log.d(TAG, "onIntermediateText($text)")
            recognizerCallback?.onIntermediateText(text, isLast)
        }
    }
    private var recognizerCallback: Recognizer.RecognizerCallback? = null
    private val onPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                getString(R.string.key_list_codec) -> {
                    val formatStr =
                        sharedPreferences.getString(key, "RAW")
                    val format = when (formatStr) {
                        "SPEEX_WB_QUALITY_9" -> {
                            Recognizer.AudioCodecFormat.SPEEX_WB_QUALITY_9
                        }
                        "OPUS" -> {
                            Recognizer.AudioCodecFormat.OPUS
                        }
                        else -> {
                            Recognizer.AudioCodecFormat.AUDIO_L16_RATE_16000_CHANNELS_1
                        }
                    }

                    getRecognizer().setCodecFormat(format)
                }
                getString(R.string.key_custom_context_enabled) -> {
                    val enabled = sharedPreferences.getBoolean(key, false)
                    if (enabled) {
                        val context = sharedPreferences.getString(
                            getString(R.string.key_custom_context),
                            null
                        )
                        setCustomIflyosContext(context)
                    } else {
                        setCustomIflyosContext(null)
                    }
                }
                getString(R.string.key_custom_context) -> {
                    val context = sharedPreferences.getString(key, null)
                    setCustomIflyosContext(context)
                }
                getString(R.string.key_recognize_profile) -> {
                    val profile =
                        sharedPreferences.getString(key, Recognizer.Profile.CloseTalk.value)
                    getRecognizer().profile = if (profile == Recognizer.Profile.FarField.value) {
                        Recognizer.Profile.FarField
                    } else {
                        Recognizer.Profile.CloseTalk
                    }
                }
            }
        }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    fun setTransmissionListener(listener: TransmissionListener?) {
        transmissionListener = listener
    }

    fun setRecognizerCallback(recognizerCallback: Recognizer.RecognizerCallback?) {
        this.recognizerCallback = recognizerCallback
    }

    fun setVisualCallback(visualCallback: Recognizer.VisualCallback?) {
        getRecognizer().setVisualCallback(visualCallback)
    }

    override fun onCreate() {
        super.onCreate()

        getRecognizer().setRecognizerCallback(internalRecordCallback)

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        pref.registerOnSharedPreferenceChangeListener(onPreferenceChangeListener)

        if (pref.getBoolean(getString(R.string.key_custom_context_enabled), false)) {
            val customContext = pref.getString(getString(R.string.key_custom_context), null)
            setCustomIflyosContext(customContext)
        }

        val profile =
            pref.getString(
                getString(R.string.key_recognize_profile),
                Recognizer.Profile.CloseTalk.value
            )
        getRecognizer().profile = if (profile == Recognizer.Profile.FarField.value) {
            Recognizer.Profile.FarField
        } else {
            Recognizer.Profile.CloseTalk
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_EVS_CONNECTED)
        intentFilter.addAction(ACTION_EVS_DISCONNECTED)
        intentFilter.addAction(VOLUME_CHANGED_ACTION)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_EVS_DISCONNECTED -> {
                    Log.e(TAG, "EVS断开连接")
                    engineService?.let {
                        val audioPlayer = getAudioPlayer()
                        audioPlayer.pause(AudioPlayer.TYPE_PLAYBACK)
                        audioPlayer.pause(AudioPlayer.TYPE_RING)
                        audioPlayer.pause(AudioPlayer.TYPE_TTS)
                    }

                    val code = intent.getIntExtra(EXTRA_CODE, 0)
                    val message = intent.getStringExtra(EXTRA_MESSAGE)

                    if (code == 1000) {
                        // 1000为正常断开连接
                        Log.d(TAG, "EVS Disconnected normally.")
                    } else {
                        var client_id = SpUtils.getString("client_id", "")
                        if (client_id != null) {
                            connectEvs(client_id)
                        }
                    }
                }
                ACTION_EVS_CONNECTED -> {
                    Log.e(TAG, "EVS连接成功")
//                    initEvsUi()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CLEAR_AUTH -> {
                disconnect()
                getAudioPlayer().apply {
                    stop(AudioPlayer.TYPE_TTS)
                    stop(AudioPlayer.TYPE_RING)
                    stop(AudioPlayer.TYPE_PLAYBACK)
                }

                getAlarm()?.apply {
                    stop()
                }

                AuthDelegate.removeAuthResponseFromPref(this)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        getRecognizer().removeRecognizerCallback()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(onPreferenceChangeListener)
    }

    override fun onEvsConnected() {
        super.onEvsConnected()
        sendBroadcast(Intent(ACTION_EVS_CONNECTED))
    }

    override fun onEvsDisconnected(code: Int, message: String?, fromRemote: Boolean) {
        super.onEvsDisconnected(code, message, fromRemote)
        val intent = Intent(ACTION_EVS_DISCONNECTED)
        intent.putExtra(EXTRA_CODE, code)
        intent.putExtra(EXTRA_MESSAGE, message)
        sendBroadcast(intent)
    }

    fun sendAudioIn(replyKey: String? = null) {
        getRecognizer().sendAudioIn(replyKey)
    }

    fun sendTextIn(query: String, replyKey: String? = null) {
        getRecognizer().sendTextIn(query, true, replyKey)
    }

    fun sendVisualIn(
        profile: String,
        realtime: Boolean,
        metaData: ImageMetaData,
        requestCallback: RequestCallback? = null,
    ) {
        getRecognizer().sendVisualIn(profile, realtime, metaData, requestCallback)
    }

    fun sendImages(data: ByteArray) {
        getRecognizer().sendImages(data)
    }

    fun requestEnd() {
        getRecognizer().sendEndMessage()
    }

    fun sendAudioTranslation() {
        getRecognizer().sendAudioInWithTranslation()
    }

    fun sendTextInTranslation(query: String, withTts: Boolean) {
        getRecognizer().sendTextIn(query, withTts, translation = true)
    }

    fun sendTextInWithProfile(query: String, withTts: Boolean, profile: String) {
        getRecognizer().sendTextIn(query, withTts, profile = profile)
    }

    fun sendTts(
        text: String,
        speed: Float? = null,
        volume: Int? = null,
        vcn: String? = null,
    ) {
        getAudioPlayer().sendTtsText(text, speed, volume, vcn)
    }

    override fun isMiguEnabled(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(getString(R.string.key_app_migu), false)
    }

    @SuppressLint("HardwareIds")
    fun connectEvs(deviceId: String) {
        val serverUrl = CommonContants.EVS_WS_URL
        connect(serverUrl, deviceId)
    }

    override fun onResponsesRaw(json: String) {
        super.onResponsesRaw(json)

        transmissionListener?.onResponsesRaw(json)
    }

    override fun onRequestRaw(obj: Any) {
        super.onRequestRaw(obj)

        transmissionListener?.onRequestRaw(obj)
    }

    override fun onConnectFailed(t: Throwable?) {
        super.onConnectFailed(t)

        val intent = Intent(ACTION_EVS_CONNECT_FAILED)
        intent.putExtra(EXTRA_MESSAGE, t?.message)
        sendBroadcast(intent)
    }

    override fun overrideRecognizer(): Recognizer {
        return EvsRecoginzerImpl(this)
    }

    override fun onSendFailed(code: Int, reason: String?) {
        super.onSendFailed(code, reason)

        val intent = Intent(ACTION_EVS_SEND_FAILED)
        intent.putExtra(EXTRA_CODE, code)
        intent.putExtra(EXTRA_MESSAGE, reason)
        sendBroadcast(intent)
    }

    override fun onError(code: Int, message: String) {
        super.onError(code, message)

        val intent = Intent(ACTION_EVS_ERROR)
        intent.putExtra(EXTRA_CODE, code)
        intent.putExtra(EXTRA_MESSAGE, message)
        sendBroadcast(intent)
    }

    override fun isResponseSoundEnabled(): Boolean {
        return !PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(getString(R.string.key_disable_response_sound), false)
    }

    override fun getLocation(): DeviceLocation? {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        if (pref.getBoolean(getString(R.string.key_custom_location), false)) {
            val latitude = pref.getFloat(getString(R.string.key_latitude), Float.MIN_VALUE)
            val longitude = pref.getFloat(getString(R.string.key_longitude), Float.MIN_VALUE)
            if (latitude != Float.MIN_VALUE && longitude != Float.MIN_VALUE) {
                return DeviceLocation(latitude.toDouble(), longitude.toDouble())
            }
        }
        return super.getLocation()
    }

    interface TransmissionListener {
        fun onResponsesRaw(json: String)
        fun onRequestRaw(obj: Any)
    }

    override fun overrideExternalPlayer(): ExternalPlayer? {
        // 重写外部播放器
        return null
    }

    override fun overrideVideoPlayer(): VideoPlayer? {
        // 好兔视频播放器
        return null
    }
}