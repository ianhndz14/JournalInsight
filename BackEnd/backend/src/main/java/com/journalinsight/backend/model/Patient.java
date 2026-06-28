package com.journalinsight.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @Column(name = "phone_number_pk", length = 15)
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "account_id_fk1", nullable = false, unique = true)
    private Account account;

    public Patient() {}

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
}
