package com.studentportal.helpbot.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "purchase_data_table")
public class Purchase {
    @Id
    private String payloadID;
    @Column(nullable=true,name = "roomID")
    private Long roomID;
    @Column(nullable=true,name = "messageID")
    private Integer messageID;
    @Column(nullable=true,name = "customerID")
    private Long customerID;
    @Column(nullable=true,name = "performerID")
    private Long performerID;
    @Column(nullable=true,name = "chargeID")
    private String chargeID;
    @Column(nullable=true,name = "priceToPerformer", columnDefinition = "int default 0")
    private Integer priceToPerformer;
//    @Column(nullable=true,name = "customerCard")
//    private Long customerCard;
    @Column(nullable=true,name = "performerCard")
    private Long performerCard;
    @Column(nullable=false,name = "flag")
    private boolean flag;
    @Column(nullable=false,name = "successfulBargain")
    private boolean successfulBargain;
    @Column(nullable=true,name = "date")
    private String date;
}
