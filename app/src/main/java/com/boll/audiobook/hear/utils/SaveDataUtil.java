package com.boll.audiobook.hear.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 简单数据工具类
 *
 * @author Created by zhc on 2020/10/8
 */
public class SaveDataUtil {

    private Context mContext;
    public static volatile SaveDataUtil mInstance;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public static SaveDataUtil getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SaveDataUtil.class) {
                if (mInstance == null) {
                    mInstance = new SaveDataUtil(context);
                }
            }
        }
        return mInstance;
    }

    private SaveDataUtil(Context context) {
        mContext = context;
        mPreferences = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    /**
     * 存入字符串
     *
     * @param key   字符串的键
     * @param value 字符串的值
     */
    public void putString(String key, String value) {
        //存入数据
        mEditor.putString(key, value).apply();
    }

    /**
     * 获取字符串
     *
     * @param key 字符串的键
     * @return 得到的字符串
     */
    public String getString(String key) {
        return getString(key, "");
    }

    /**
     * 获取字符串
     *
     * @param key      字符串的键
     * @param defValue 字符串的默认值
     * @return 得到的字符串
     */
    public String getString(String key, String defValue) {
        return mPreferences.getString(key, defValue);
    }

    /**
     * 保存布尔值
     *
     * @param key   键
     * @param value 值
     */
    public void putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value).apply();
    }

    /**
     * 获取布尔值
     *
     * @param key      键
     * @param defValue 默认值
     * @return 返回保存的值
     */
    public boolean getBoolean(String key, boolean defValue) {
        return mPreferences.getBoolean(key, defValue);
    }

    /**
     * 保存long值
     *
     * @param key   键
     * @param value 值
     */
    public void putLong(String key, long value) {
        mEditor.putLong(key, value).apply();
    }

    /**
     * 获取long值
     *
     * @param key      键
     * @param defValue 默认值
     * @return 保存的值
     */
    public long getLong(String key, long defValue) {
        return mPreferences.getLong(key, defValue);
    }

    /**
     * 保存int值
     *
     * @param key   键
     * @param value 值
     */
    public void putInt(String key, int value) {
        mEditor.putInt(key, value).apply();
    }

    /**
     * 获取int值
     *
     * @param key      键
     * @param defValue 默认值
     * @return 保存的值
     */
    public int getInt(String key, int defValue) {
        return mPreferences.getInt(key, defValue);
    }

    /**
     * 保存float值
     *
     * @param key   键
     * @param value 值
     */
    public void putFloat(String key, float value) {
        mEditor.putFloat(key, value).apply();
    }

    /**
     * 获取float值
     *
     * @param key      键
     * @param defValue 默认值
     * @return 保存的值
     */
    public float getFloat(String key, float defValue) {
        return mPreferences.getFloat(key, defValue);
    }

    /**
     * 保存对象
     *
     * @param key 键
     * @param obj 要保存的对象（Serializable的子类）
     * @param <T> 泛型定义
     */
    public <T extends Serializable> void putObject(String key, T obj) {
        if (obj == null) {
            putString(key, "");
            return;
        }
        putString(key, obj2Base64(obj));
    }

    /**
     * 获取对象
     *
     * @param key 键
     * @param <T> 指定泛型
     * @return 泛型对象
     */
    public <T extends Serializable> T getObject(String key) {
        return base64ToObj(getString(key));
    }

    /**
     * 存储List集合
     *
     * @param key  存储的键
     * @param list 存储的集合
     */
    public void putList(String key, List<? extends Serializable> list) {
        putString(key, obj2Base64(list));
    }

    /**
     * 获取List集合
     *
     * @param key 键
     * @param <E> 指定泛型
     * @return List集合
     */
    public <E extends Serializable> List<E> getList(String key) {
        return (List<E>) base64ToObj(getString(key));
    }

    /**
     * 存储Map集合
     *
     * @param key 键
     * @param map 存储的集合
     * @param <K> 指定Map的键
     * @param <V> 指定Map的值
     */
    public <K extends Serializable, V> void putMap(String key, Map<K, V> map) {
        putString(key, obj2Base64(map));
    }

    /**
     * 获取map集合
     *
     * @param key 键
     * @param <K> 指定Map的键
     * @param <V> 指定Map的值
     * @return 存储的集合
     */
    public <K extends Serializable, V> Map<K, V> getMap(String key) {
        return (Map<K, V>) base64ToObj(getString(key));
    }

    /**
     * 对象转字符串
     *
     * @param obj 任意对象
     * @return base64字符串
     */
    private String obj2Base64(Object obj) {
        //判断对象是否为空
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        String objectStr = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            // 将对象放到OutputStream中
            // 将对象转换成byte数组，并将其进行base64编码
            objectStr = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return objectStr;
    }

    /**
     * base64转对象
     *
     * @param base64 字符串
     * @param <T>    指定转成的类型
     * @return 指定类型对象 失败返回null
     */
    private <T> T base64ToObj(String base64) {
        // 将base64格式字符串还原成byte数组
        if (TextUtils.isEmpty(base64)) {
            return null;
        }
        byte[] objBytes = Base64.decode(base64.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        T t = null;
        try {
            bais = new ByteArrayInputStream(objBytes);
            ois = new ObjectInputStream(bais);
            t = (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return t;
    }

    /**
     * 移除字符串
     *
     * @param key 字符串的键
     */
    public void removeByKey(String key) {
        mEditor.remove(key).apply();
    }

}
