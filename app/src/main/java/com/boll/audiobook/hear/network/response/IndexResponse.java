package com.boll.audiobook.hear.network.response;

import java.util.List;

/**
 * 主页返回数据
 * created by zoro at 2023/5/10
 */
public class IndexResponse extends BaseResponse{

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<LevelResponse> levelList;

        public List<LevelResponse> getLevelList() {
            return levelList;
        }

        public void setLevelList(List<LevelResponse> levelList) {
            this.levelList = levelList;
        }
    }
}
