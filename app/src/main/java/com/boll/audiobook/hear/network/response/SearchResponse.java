package com.boll.audiobook.hear.network.response;

import java.util.List;

/**
 * created by zoro at 2023/6/15
 */
public class SearchResponse extends BaseResponse{

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private Integer type;
        private Object hasAlbum;
        private Boolean hasAudio;
        private AlbumListBean albumList;
        private AudioListBean audioList;

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Object getHasAlbum() {
            return hasAlbum;
        }

        public void setHasAlbum(Object hasAlbum) {
            this.hasAlbum = hasAlbum;
        }

        public Boolean getHasAudio() {
            return hasAudio;
        }

        public void setHasAudio(Boolean hasAudio) {
            this.hasAudio = hasAudio;
        }

        public AlbumListBean getAlbumList() {
            return albumList;
        }

        public void setAlbumList(AlbumListBean albumList) {
            this.albumList = albumList;
        }

        public AudioListBean getAudioList() {
            return audioList;
        }

        public void setAudioList(AudioListBean audioList) {
            this.audioList = audioList;
        }

        public static class AlbumListBean {
            private Integer total;
            private List<ListBean> list;

            public Integer getTotal() {
                return total;
            }

            public void setTotal(Integer total) {
                this.total = total;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public static class ListBean {
                private Integer id;
                private Integer levelId;
                private String name;
                private String picUrl;
                private Integer resCount;
                private boolean isCollect;
                private Object audioList;

                public Integer getId() {
                    return id;
                }

                public void setId(Integer id) {
                    this.id = id;
                }

                public Integer getLevelId() {
                    return levelId;
                }

                public void setLevelId(Integer levelId) {
                    this.levelId = levelId;
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

                public Integer getResCount() {
                    return resCount;
                }

                public void setResCount(Integer resCount) {
                    this.resCount = resCount;
                }

                public boolean getIsCollect() {
                    return isCollect;
                }

                public void setIsCollect(boolean isCollect) {
                    this.isCollect = isCollect;
                }

                public Object getAudioList() {
                    return audioList;
                }

                public void setAudioList(Object audioList) {
                    this.audioList = audioList;
                }
            }
        }

        public static class AudioListBean {
            private Integer total;
            private List<AudioResponse> list;

            public Integer getTotal() {
                return total;
            }

            public void setTotal(Integer total) {
                this.total = total;
            }

            public List<AudioResponse> getList() {
                return list;
            }

            public void setList(List<AudioResponse> list) {
                this.list = list;
            }

        }
    }
}
