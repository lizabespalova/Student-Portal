package com.studentportal.helpbot.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "post_data_table")
public class Post {
    @Id
    private Integer messageID;
    @Column(name = "link")
    private String link;
    @Column(nullable=true,name = "customer_id")
    private Long customer_id;
    @Column(name = "chanel")
    private String chanel;
    @Column(nullable=true,name = "isActive")
    private boolean isActive;
    @Column(nullable=true,name = "date")
    private String date;
//    @Column(nullable=true,name = "performer_id")
//    private Long performer_id;
}
