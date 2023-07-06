package com.boll.audiobook.hear.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PcmToMp3Util {

    private static final String TAG = "PcmToMp3Util";

    public static boolean createFile(String fileName) {
        boolean isSuccess = false;
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            isSuccess = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("", "创建储存音频文件出错");
        }
        Log.e("", "createFile: 文件创建是否成功：" + isSuccess);
        return isSuccess;
    }

    /**
     * pcm转mp3
     *
     * @param position  第几句
     * @param deleteOrg 是否删除源文件
     * @return
     */
    public static boolean pcmToMp3(int position, boolean deleteOrg) {
        String src = Const.RECORDPATH + "record.pcm";
        String target = Const.RECORDPATH + "record" + position + ".mp3";
        try {
            FileInputStream fis = new FileInputStream(src);
            FileOutputStream fos = new FileOutputStream(target);

            //计算长度
            byte[] buf = new byte[1024 * 4];
            int size = fis.read(buf);
            int PCMSize = 0;
            while (size != -1) {
                PCMSize += size;
                size = fis.read(buf);
            }
            fis.close();

            //填入参数，比特率等等。这里用的是16位单声道 8000 hz
            WaveHeader header = new WaveHeader();
            //长度字段 = 内容的大小（PCMSize) + 头部字段的大小(不包括前面4字节的标识符RIFF以及fileLength本身的4字节)
            header.fileLength = PCMSize + (44 - 8);
            header.FmtHdrLeth = 16;
            header.BitsPerSample = 16;
            header.Channels = 1;
            header.FormatTag = 0x0001;
            header.SamplesPerSec = 16000;//正常速度是8000，这里写成了16000，速度加快一倍
            header.BlockAlign = (short) (header.Channels * header.BitsPerSample / 8);
            header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec;
            header.DataHdrLeth = PCMSize;

            byte[] h = header.getHeader();

            assert h.length == 44; //WAV标准，头部应该是44字节
            //write header
            fos.write(h, 0, h.length);
            //write data stream
            fis = new FileInputStream(src);
            size = fis.read(buf);
            while (size != -1) {
                fos.write(buf, 0, size);
                size = fis.read(buf);
            }
            fis.close();
            fos.close();
            if (deleteOrg) {
                new File(src).delete();
            }
            Log.d(TAG, "PCM Convert to MP3 OK!");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
