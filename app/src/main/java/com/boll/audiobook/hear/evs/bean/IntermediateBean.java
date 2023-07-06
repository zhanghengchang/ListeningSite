package com.boll.audiobook.hear.evs.bean;

import java.util.List;

/**
 * 语音识别中实体类
 * created by zoro at 2023/6/14
 */
public class IntermediateBean {

    private IflyosMetaBean iflyos_meta;
    private List<IflyosResponsesBean> iflyos_responses;

    public IflyosMetaBean getIflyos_meta() {
        return iflyos_meta;
    }

    public void setIflyos_meta(IflyosMetaBean iflyos_meta) {
        this.iflyos_meta = iflyos_meta;
    }

    public List<IflyosResponsesBean> getIflyos_responses() {
        return iflyos_responses;
    }

    public void setIflyos_responses(List<IflyosResponsesBean> iflyos_responses) {
        this.iflyos_responses = iflyos_responses;
    }

    public static class IflyosMetaBean {
        private Boolean is_last;
        private String trace_id;
        private String request_id;

        public Boolean getIs_last() {
            return is_last;
        }

        public void setIs_last(Boolean is_last) {
            this.is_last = is_last;
        }

        public String getTrace_id() {
            return trace_id;
        }

        public void setTrace_id(String trace_id) {
            this.trace_id = trace_id;
        }

        public String getRequest_id() {
            return request_id;
        }

        public void setRequest_id(String request_id) {
            this.request_id = request_id;
        }
    }

    public static class IflyosResponsesBean {
        private HeaderBean header;
        private PayloadBean payload;

        public HeaderBean getHeader() {
            return header;
        }

        public void setHeader(HeaderBean header) {
            this.header = header;
        }

        public PayloadBean getPayload() {
            return payload;
        }

        public void setPayload(PayloadBean payload) {
            this.payload = payload;
        }

        public static class HeaderBean {
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class PayloadBean {
            private String text;
            private Boolean is_last;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public Boolean getIs_last() {
                return is_last;
            }

            public void setIs_last(Boolean is_last) {
                this.is_last = is_last;
            }
        }
    }
}
