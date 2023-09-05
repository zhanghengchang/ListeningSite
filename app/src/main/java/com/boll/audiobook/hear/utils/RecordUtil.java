package com.boll.audiobook.hear.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 录音工具类
 * created by zoro at 2023/6/17
 */
public class RecordUtil {

    private static final String TAG = "AudioUtil";

    private static AudioRecord audioRecord;

    // 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
    public static final int SAMPLE_RATE_INHZ = 44100;

    // 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;

    // 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * 录音的工作线程
     */
    private static Thread recordingAudioThread;
    private static boolean isRecording = false;//mark if is recording

    /**
     * 开始录音
     */
    public static void startRecordAudio() {
        try {
            // 获取最小录音缓存大小，
            int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);

            // 开始录音
            isRecording = true;
            audioRecord.startRecording();

            // 创建数据流，将缓存导入数据流
            recordingAudioThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    File folderFile = new File(Const.RECORDPATH);
                    if (!folderFile.exists()) {
                        folderFile.mkdirs();
                    }

                    File file = new File(Const.RECORDPATH + "record.pcm");
                    Log.d(TAG, "audio cache pcm file path:" + Const.RECORDPATH + "record.pcm");

                    if (file.exists()) {
                        file.delete();
                    }

                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e(TAG, "临时缓存文件未找到");
                    }
                    if (fos == null) {
                        return;
                    }

                    byte[] data = new byte[minBufferSize];
                    int read;
                    if (fos != null) {
                        while (isRecording && !recordingAudioThread.isInterrupted()) {
                            read = audioRecord.read(data, 0, minBufferSize);
                            if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                                try {
                                    fos.write(data);
                                    Log.i("audioRecordTest", "写录音数据->" + read);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    try {
                        // 关闭数据流
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            recordingAudioThread.start();
        } catch (IllegalStateException e) {
            Log.e(TAG, "需要获取录音权限！");
        } catch (SecurityException e) {
            Log.e(TAG, "需要获取录音权限！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录音
     *
     * @param position 第几句
     */
    public static void stopRecordAudio(int position) {
        try {
            isRecording = false;
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
                recordingAudioThread.interrupt();
                recordingAudioThread = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        PcmToMp3Util.pcmToMp3(position, false);
        PcmToWavUtil.getInstance().pcmToWav(position, false);
    }

    /**
     * 播放音频文件
     */
    public static void playWav(String filePath) {
        File wavFile = new File(filePath);
        try {
            FileInputStream fis = new FileInputStream(wavFile);
            byte[] buffer = new byte[1024 * 1024 * 2];//2M
            int len = fis.read(buffer);
            Log.i(TAG, "fis len=" + len);
            Log.i(TAG, "0:" + (char) buffer[0]);
            int pcmlen = 0;
            pcmlen += buffer[0x2b];
            pcmlen = pcmlen * 256 + buffer[0x2a];
            pcmlen = pcmlen * 256 + buffer[0x29];
            pcmlen = pcmlen * 256 + buffer[0x28];

            int channel = buffer[0x17];
            channel = channel * 256 + buffer[0x16];

            int bits = buffer[0x23];
            bits = bits * 256 + buffer[0x22];
            Log.i(TAG, "pcmlen=" + pcmlen + ",channel=" + channel + ",bits=" + bits);
            AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE_INHZ * 2,
                    channel,
                    AudioFormat.ENCODING_PCM_16BIT,
                    pcmlen,
                    AudioTrack.MODE_STATIC);
            at.write(buffer, 0x2C, pcmlen);
            at.play();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            wavFile = null;
        }
    }

    /**
     * 使用MediaPlayer播放文件，并且指定一个当播放完成后会触发的监听器
     *
     * @param filePath
     * @param onCompletionListener
     */
    public static void playWavWithMediaPlayer(String filePath, MediaPlayer.OnCompletionListener onCompletionListener) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用MediaPlayer播放文件
     *
     * @param filePath
     */
    public static void playWavWithMediaPlayer(String filePath) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
