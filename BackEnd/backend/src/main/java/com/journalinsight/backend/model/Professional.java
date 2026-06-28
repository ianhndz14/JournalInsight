package com.journalinsight.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "professional")
public class Professional {

    @Id
    @Column(name = "phone_number_pk", length = 15)
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "account_id_fk1", nullable = false, unique = true)
    private Account account;

    @Column(name = "specialty", length = 100)
    private String specialty;

    public Professional() {}

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
}
