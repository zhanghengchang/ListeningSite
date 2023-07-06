package com.boll.audiobook.hear.network.response;

import java.io.Serializable;

public class MyWorkListBean implements Serializable {

   public String audioUrl;
   public String name;
   public String picUrl;
   public String recordTime;
   public int resId;
   public int type;

   public String getAudioUrl() {
      return audioUrl;
   }

   public void setAudioUrl(String audioUrl) {
      this.audioUrl = audioUrl;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getPicUrl() {
      return picUrl;
   }

   public void setPicUrl(String picUrl) {
      this.picUrl = picUrl;
   }

   public String getRecordTime() {
      return recordTime;
   }

   public void setRecordTime(String recordTime) {
      this.recordTime = recordTime;
   }

   public int getResId() {
      return resId;
   }

   public void setResId(int resId) {
      this.resId = resId;
   }

   public int getType() {
      return type;
   }

   public void setType(int type) {
      this.type = type;
   }
}
