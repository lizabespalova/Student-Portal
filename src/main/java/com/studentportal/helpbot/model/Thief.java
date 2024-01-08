package com.studentportal.helpbot.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "thief_data_table")
public class Thief {
    @Id
    private Long thiefID;
    @Column(nullable=true,name = "name")
    private String name;
    @Column(nullable=true,name = "surname")
    private String surname;
    @Column(nullable=true,name = "nick")
    private String nick;

}
