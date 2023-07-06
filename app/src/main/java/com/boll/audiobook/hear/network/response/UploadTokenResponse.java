package com.boll.audiobook.hear.network.response;

/**
 * created by zoro at 2023/6/19
 */
public class UploadTokenResponse extends BaseResponse {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String filePath;
        private String uploadToken;

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getUploadToken() {
            return uploadToken;
        }

        public void setUploadToken(String uploadToken) {
            this.uploadToken = uploadToken;
        }
    }
}
