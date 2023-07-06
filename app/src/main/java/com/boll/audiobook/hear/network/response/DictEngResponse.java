package com.boll.audiobook.hear.network.response;

import java.io.Serializable;
import java.util.List;

/**
 * 词典返回数据(英文)
 * created by zoro at 2023/6/6
 */
public class DictEngResponse implements Serializable {

    private static final long serialVersionUID = 7989865558224579113L;
    private Integer code;
    private String message;
    private List<EnglishDictEntitiesBean> englishDictEntities;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<EnglishDictEntitiesBean> getEnglishDictEntities() {
        return englishDictEntities;
    }

    public void setEnglishDictEntities(List<EnglishDictEntitiesBean> englishDictEntities) {
        this.englishDictEntities = englishDictEntities;
    }

    public static class EnglishDictEntitiesBean implements Serializable{
        private static final long serialVersionUID = -1001160461703674586L;
        private Integer id;
        private Object adjective;
        private Object comparativeDegree;
        private String nounPlurals;
        private String pastParticiple;
        private String phoneticEn;
        private String phoneticUs;
        private List<String> phrases;
        private String presentParticiple;
        private String preterit;
        private Object superlativeDegree;
        private Object sentencePattern;
        private String thirdSingular;
        private Object verb;
        private Object referenceArticle;
        private String phoneticEnAudio;
        private Object wordAudio;
        private List<String> synonyms;
        private Object vocabularyGroup;
        private List<ExamplesBean> examples;
        private List<String> antonyms;
        private List<ExplanationsBean> explanations;
        private String word;
        private String phoneticUsAudio;
        private Object explanationEn;
        private String dictCode;
        private List<String> deriveList;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Object getAdjective() {
            return adjective;
        }

        public void setAdjective(Object adjective) {
            this.adjective = adjective;
        }

        public Object getComparativeDegree() {
            return comparativeDegree;
        }

        public void setComparativeDegree(Object comparativeDegree) {
            this.comparativeDegree = comparativeDegree;
        }

        public String getNounPlurals() {
            return nounPlurals;
        }

        public void setNounPlurals(String nounPlurals) {
            this.nounPlurals = nounPlurals;
        }

        public String getPastParticiple() {
            return pastParticiple;
        }

        public void setPastParticiple(String pastParticiple) {
            this.pastParticiple = pastParticiple;
        }

        public String getPhoneticEn() {
            return phoneticEn;
        }

        public void setPhoneticEn(String phoneticEn) {
            this.phoneticEn = phoneticEn;
        }

        public String getPhoneticUs() {
            return phoneticUs;
        }

        public void setPhoneticUs(String phoneticUs) {
            this.phoneticUs = phoneticUs;
        }

        public List<String> getPhrases() {
            return phrases;
        }

        public void setPhrases(List<String> phrases) {
            this.phrases = phrases;
        }

        public String getPresentParticiple() {
            return presentParticiple;
        }

        public void setPresentParticiple(String presentParticiple) {
            this.presentParticiple = presentParticiple;
        }

        public String getPreterit() {
            return preterit;
        }

        public void setPreterit(String preterit) {
            this.preterit = preterit;
        }

        public Object getSuperlativeDegree() {
            return superlativeDegree;
        }

        public void setSuperlativeDegree(Object superlativeDegree) {
            this.superlativeDegree = superlativeDegree;
        }

        public Object getSentencePattern() {
            return sentencePattern;
        }

        public void setSentencePattern(Object sentencePattern) {
            this.sentencePattern = sentencePattern;
        }

        public String getThirdSingular() {
            return thirdSingular;
        }

        public void setThirdSingular(String thirdSingular) {
            this.thirdSingular = thirdSingular;
        }

        public Object getVerb() {
            return verb;
        }

        public void setVerb(Object verb) {
            this.verb = verb;
        }

        public Object getReferenceArticle() {
            return referenceArticle;
        }

        public void setReferenceArticle(Object referenceArticle) {
            this.referenceArticle = referenceArticle;
        }

        public String getPhoneticEnAudio() {
            return phoneticEnAudio;
        }

        public void setPhoneticEnAudio(String phoneticEnAudio) {
            this.phoneticEnAudio = phoneticEnAudio;
        }

        public Object getWordAudio() {
            return wordAudio;
        }

        public void setWordAudio(Object wordAudio) {
            this.wordAudio = wordAudio;
        }

        public List<String> getSynonyms() {
            return synonyms;
        }

        public void setSynonyms(List<String> synonyms) {
            this.synonyms = synonyms;
        }

        public Object getVocabularyGroup() {
            return vocabularyGroup;
        }

        public void setVocabularyGroup(Object vocabularyGroup) {
            this.vocabularyGroup = vocabularyGroup;
        }

        public List<ExamplesBean> getExamples() {
            return examples;
        }

        public void setExamples(List<ExamplesBean> examples) {
            this.examples = examples;
        }

        public List<String> getAntonyms() {
            return antonyms;
        }

        public void setAntonyms(List<String> antonyms) {
            this.antonyms = antonyms;
        }

        public List<ExplanationsBean> getExplanations() {
            return explanations;
        }

        public void setExplanations(List<ExplanationsBean> explanations) {
            this.explanations = explanations;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getPhoneticUsAudio() {
            return phoneticUsAudio;
        }

        public void setPhoneticUsAudio(String phoneticUsAudio) {
            this.phoneticUsAudio = phoneticUsAudio;
        }

        public Object getExplanationEn() {
            return explanationEn;
        }

        public void setExplanationEn(Object explanationEn) {
            this.explanationEn = explanationEn;
        }

        public String getDictCode() {
            return dictCode;
        }

        public void setDictCode(String dictCode) {
            this.dictCode = dictCode;
        }

        public List<String> getDeriveList() {
            return deriveList;
        }

        public void setDeriveList(List<String> deriveList) {
            this.deriveList = deriveList;
        }

        public static class ExamplesBean implements Serializable{
            private static final long serialVersionUID = 3524529076800770850L;
            private String en;
            private String cn;
            private Object cnAudio;
            private Object enAudio;

            public String getEn() {
                return en;
            }

            public void setEn(String en) {
                this.en = en;
            }

            public String getCn() {
                return cn;
            }

            public void setCn(String cn) {
                this.cn = cn;
            }

            public Object getCnAudio() {
                return cnAudio;
            }

            public void setCnAudio(Object cnAudio) {
                this.cnAudio = cnAudio;
            }

            public Object getEnAudio() {
                return enAudio;
            }

            public void setEnAudio(Object enAudio) {
                this.enAudio = enAudio;
            }
        }

        public static class ExplanationsBean implements Serializable{
            private static final long serialVersionUID = 3210651544207678982L;
            private String pos;
            private String meaning;

            public String getPos() {
                return pos;
            }

            public void setPos(String pos) {
                this.pos = pos;
            }

            public String getMeaning() {
                return meaning;
            }

            public void setMeaning(String meaning) {
                this.meaning = meaning;
            }
        }
    }
}
