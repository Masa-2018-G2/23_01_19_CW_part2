package com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto;

import java.util.List;

public class ContactListDto {
    private List<ContactDto> contacts;

    public ContactListDto() {
    }

    public ContactListDto(List<ContactDto> contacts) {
        this.contacts = contacts;
    }

    public List<ContactDto> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactDto> contacts) {
        this.contacts = contacts;
    }
}
