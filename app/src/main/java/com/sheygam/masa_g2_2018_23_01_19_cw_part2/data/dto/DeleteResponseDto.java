package com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto;

public class DeleteResponseDto {
    private String status;

    public DeleteResponseDto() {
    }

    public DeleteResponseDto(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
