package com.studentportal.helpbot.service.dopclasses;

import com.studentportal.helpbot.service.consts.Subjects;
import com.studentportal.helpbot.service.consts.Text;
import org.springframework.stereotype.Component;

@Component
public class Subjectgetters {

    private Subjects subjects;
   public String getsubject(String messagetext){

       if(messagetext.equals(subjects.MATH.toString()))return Text.matem_text;
       else if (messagetext.equals(subjects.LANGUAGES.toString()))return Text.languages_text;
       else if (messagetext.equals(subjects.MEDICINE.toString()))return Text.medicine_text;
       else if (messagetext.equals(subjects.PHYLOSOPHY.toString()))return Text.phylosophy_text;
       else if (messagetext.equals(subjects.PROGRAMMING.toString()))return Text.programming_text;
       else if (messagetext.equals(subjects.GEOGRAPHY.toString()))return Text.geographe_text;
       else if (messagetext.equals(subjects.CHEMISTRY.toString()))return Text.chemistry_text;
       else return Text.another_text;
   }
}
