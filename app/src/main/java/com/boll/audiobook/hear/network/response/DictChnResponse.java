package com.boll.audiobook.hear.network.response;

import java.util.List;

/**
 * 词典返回数据(中文)
 * created by zoro at 2023/6/6
 */
public class DictChnResponse {

    private Integer code;
    private List<ChineseWordEntitiesBean> chineseWordEntities;
    private String message;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<ChineseWordEntitiesBean> getChineseWordEntities() {
        return chineseWordEntities;
    }

    public void setChineseWordEntities(List<ChineseWordEntitiesBean> chineseWordEntities) {
        this.chineseWordEntities = chineseWordEntities;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class ChineseWordEntitiesBean {
        private Integer id;
        private String word;
        private List<String> copy;
        private String wordAudio;
        private String audioIndex;
        private String dictCode;
        private List<PolyphoneBean> polyphone;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public List<String> getCopy() {
            return copy;
        }

        public void setCopy(List<String> copy) {
            this.copy = copy;
        }

        public String getWordAudio() {
            return wordAudio;
        }

        public void setWordAudio(String wordAudio) {
            this.wordAudio = wordAudio;
        }

        public String getAudioIndex() {
            return audioIndex;
        }

        public void setAudioIndex(String audioIndex) {
            this.audioIndex = audioIndex;
        }

        public String getDictCode() {
            return dictCode;
        }

        public void setDictCode(String dictCode) {
            this.dictCode = dictCode;
        }

        public List<PolyphoneBean> getPolyphone() {
            return polyphone;
        }

        public void setPolyphone(List<PolyphoneBean> polyphone) {
            this.polyphone = polyphone;
        }

        public static class PolyphoneBean {
            private String pinyin;
            private List<ExamplesBean> examples;
            private List<?> antonyms;
            private List<String> synonyms;
            private List<String> explanations;
            private String translations;
            private String audio;

            public String getPinyin() {
                return pinyin;
            }

            public void setPinyin(String pinyin) {
                this.pinyin = pinyin;
            }

            public List<ExamplesBean> getExamples() {
                return examples;
            }

            public void setExamples(List<ExamplesBean> examples) {
                this.examples = examples;
            }

            public List<?> getAntonyms() {
                return antonyms;
            }

            public void setAntonyms(List<?> antonyms) {
                this.antonyms = antonyms;
            }

            public List<String> getSynonyms() {
                return synonyms;
            }

            public void setSynonyms(List<String> synonyms) {
                this.synonyms = synonyms;
            }

            public List<String> getExplanations() {
                return explanations;
            }

            public void setExplanations(List<String> explanations) {
                this.explanations = explanations;
            }

            public String getTranslations() {
                return translations;
            }

            public void setTranslations(String translations) {
                this.translations = translations;
            }

            public String getAudio() {
                return audio;
            }

            public void setAudio(String audio) {
                this.audio = audio;
            }

            public static class ExamplesBean {
                private Object en;
                private String cn;

                public Object getEn() {
                    return en;
                }

                public void setEn(Object en) {
                    this.en = en;
                }

                public String getCn() {
                    return cn;
                }

                public void setCn(String cn) {
                    this.cn = cn;
                }
            }
        }
    }
}
