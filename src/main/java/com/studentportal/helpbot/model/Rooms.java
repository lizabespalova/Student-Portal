package com.studentportal.helpbot.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "rooms_data_table")
public class Rooms {
    @Id
    private int roomNumber;
    @Column(nullable=true,name = "roomID")
    private Long roomID;
    @Column(nullable=true,name = "IsFree")
    private boolean IsFree;
    @Column(nullable=true,name = "postId")
    private Long postId;
    @Column(nullable=true,name = "customerID")
    private Long customerID;
    @Column(nullable=true,name = "performerID")
    private Long performerID;
    @Column(nullable=true,name = "chatLink")
    private String chatLink;
    @Column(nullable=true,name = "price")
    private Integer price;
    @Column(nullable=true,name = "stateInChat")
    private Integer stateInChat;
    @Column(nullable=true,name = "date")
    private String date;
    @Column(nullable=true,name = "payload")
    private String payload;
    @Column(nullable=true,name = "following")//послідовність дій при закінченні угоди. Якщо 1, то перевіряти смс користувача щодо оцінки виконавця.Якщо 2,то видаляємо користуавача та просимо картку виконавця.Якщо 3 , то встановлюємо успішну угоду, видаляємо виконаця та звільняємо кімнату
    private Integer following;
}
