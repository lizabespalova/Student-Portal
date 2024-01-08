package com.studentportal.helpbot.model;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Table(name = "customer_data_table")
public class Customer {
    @Id
    private Long chatID;

    @Column(name = "name")
    private String name;
    @Column(name = "surname")
    private String surname;

    @Column(name = "user_nick")
    private String user_nick;

    @Column(name = "agreement_state")
    private boolean agreementsState; //(true - завдання виконано, false - не виконано)

    @Column(name = "branch")
    private String branch; //(Галузь)

    @Column(name = "description")
    private String description; //(Опис)

    @Column(name = "price")
    private String price; //(Ціна)


    @ElementCollection(targetClass = String.class)
    @LazyCollection(LazyCollectionOption.FALSE)
   /* @CollectionTable(name = "customersPhotoLink", joinColumns = @JoinColumn(name = "id"))*/
    @Column(name = "photoLink")
    private List<String> photoLink= new ArrayList<>(); //(посилання на фото)

    @ElementCollection(targetClass = String.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    /*@CollectionTable(name = "customersFileLink", joinColumns = @JoinColumn(name = "id"))*/
    @Column(name = "fileLink")
    private List<String> fileLink = new ArrayList<>(); //(посилання на файли)

   /* @ElementCollection(targetClass = String.class)
    @LazyCollection(LazyCollectionOption.FALSE)*/
    @Column(name = "postLink")
    private String postLink; //(посилання на пост)

    @Column(name = "state")
    private String state; //(на якій дії зараз людина)
    @Column(nullable=true,name = "priceFlag")
    private Integer priceFlag; //(на якій дії зараз людина)

    @Column(nullable=true,name = "checkDescriptionState", columnDefinition = "int default 0")
    private Integer checkDescriptionState; //(на якій дії зараз людина)

    @Column(nullable=true,name = "check_state")
    private boolean check_state;
    @Column(nullable=true,name = "thiefListState", columnDefinition = "int default 0")
    private Integer thiefListState;
    @Column(nullable=true,name = "message_thief_id", columnDefinition = "int default 0")
    private Integer messageThiefID;
}

