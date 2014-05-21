package com.hp.it.innovation.collaboration.dto;

public class JsonResponseDTO {
    public final static String SUCCESS_RESPONSE = "SUCCESS";
    public final static String FAILURE_RESPONSE = "FAILURE";

    private String status;
    private Object result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
