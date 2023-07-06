package com.boll.audiobook.hear.utils;

import java.net.InetAddress;

public class UUIDHexGenerator {
    private static final int IP;

    private static short counter = (short) 0;

    private static final int JVM = (int) (System.currentTimeMillis() >>> 8);

    private static UUIDHexGenerator uuidgen = new UUIDHexGenerator();

    static {
        int ipadd;
        try {
            ipadd = toInt(InetAddress.getLocalHost().getAddress());
        } catch (Exception e) {
            ipadd = 0;
        }
        IP = ipadd;
    }

    public static UUIDHexGenerator getInstance() {
        return uuidgen;
    }

    public static int toInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];
        }
        return result;
    }

    protected String format(int intval) {
        String formatted = Integer.toHexString(intval);
        StringBuffer buf = new StringBuffer("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }


    protected String format(short shortval) {
        String formatted = Integer.toHexString(shortval);
        StringBuffer buf = new StringBuffer("0000");
        buf.replace(4 - formatted.length(), 4, formatted);
        return buf.toString();
    }

    protected String formatToken(int intval) {
        String formatted = Integer.toHexString(intval);
        StringBuffer buf = new StringBuffer("000000000000");
        buf.replace(12 - formatted.length(), 12, formatted);
        return buf.toString();
    }

    protected String formatToken(short shortval) {
        String formatted = Integer.toHexString(shortval);
        StringBuffer buf = new StringBuffer("0000");
        buf.replace(4 - formatted.length(), 4, formatted);
        return buf.toString();
    }

    protected int getJVM() {
        return JVM;
    }

    protected synchronized short getCount() {
        if (counter < 0) {
            counter = 0;
        }
        return counter++;
    }

    protected int getIP() {
        return IP;
    }

    protected short getHiTime() {
        return (short) (System.currentTimeMillis() >>> 32);
    }

    protected int getLoTime() {
        return (int) System.currentTimeMillis();
    }

    public String generate() {
        return new StringBuffer(36).append(format(getIP())).append(
                format(getHiTime())).append(format(getLoTime())).append(
                format(getCount())).toString();
    }

    public String generateToken() {
        return new StringBuffer(32).append(formatToken(getIP())).append(
                formatToken(getHiTime())).append(formatToken(getLoTime())).append(
                formatToken(getCount())).toString();
    }

}
