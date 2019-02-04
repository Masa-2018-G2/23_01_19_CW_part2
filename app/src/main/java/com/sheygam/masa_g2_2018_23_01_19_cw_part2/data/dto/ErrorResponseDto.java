package com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto;

public class ErrorResponseDto {
    private int code;
    private String message;

    public ErrorResponseDto() {
    }

    public ErrorResponseDto(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
