package com.studentportal.helpbot.service.dopclasses;

import com.studentportal.helpbot.model.Post;
import com.studentportal.helpbot.model.Purchase;
import org.springframework.data.repository.CrudRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CleanBD <T, K extends CrudRepository>{
  private T table;
  private K repository;

  public CleanBD(T table, K repository){
     this.table = table;
     this.repository = repository;
  }

  public void deleteTableRow(){

      if (table instanceof Post){
           List<Post> list = new ArrayList<Post>();
          repository.findAll().forEach(item -> list.add((Post) item));
          Date today = new Date();
          for (Post post: list){
              SimpleDateFormat format = new SimpleDateFormat();
              format.applyPattern("dd.MM.yyyy");
              String getDate = post.getDate();;
              Date oldDate = null;
              if(getDate!=null) {
                  try {
                      oldDate = format.parse(getDate);
                  } catch (ParseException e) {
                      throw new RuntimeException(e);
                  }
            long delta = (today.getTime()-oldDate.getTime()) / 86400000;
              if(delta > 30){
                  int postID = post.getMessageID();
                  Post postToDelete = (Post) repository.findById(postID).orElseThrow();
                  repository.delete(postToDelete);
              }
          }
      }
   }
      else if(table instanceof Purchase) {
          List<Purchase> list = new ArrayList<Purchase>();
          repository.findAll().forEach(item -> list.add((Purchase) item));
          Date today = new Date();
          for (Purchase purchase : list) {
              SimpleDateFormat format = new SimpleDateFormat();
              format.applyPattern("dd.MM.yyyy");
              String getDate = purchase.getDate();;
              Date oldDate = null;
              if(getDate!=null) {
                  try {
                      oldDate = format.parse(getDate);
                  } catch (ParseException e) {
                      throw new RuntimeException(e);
                  }

              long delta = (today.getTime() - oldDate.getTime()) / 86400000;
              if (delta > 30) {
                  String payloadID = purchase.getPayloadID();
                  Purchase postToDelete = (Purchase) repository.findById(payloadID).orElseThrow();
                  repository.delete(postToDelete);
              }
            }
          }
      }
  }
}
