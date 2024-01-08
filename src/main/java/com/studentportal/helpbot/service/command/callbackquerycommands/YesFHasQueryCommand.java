package com.studentportal.helpbot.service.command.callbackquerycommands;

import com.studentportal.helpbot.model.*;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.dopclasses.CleanBD;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
@Component
public class YesFHasQueryCommand extends QueryCommands {
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private PostRepository postRepository;
    public YesFHasQueryCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        closeBargain(update);
//               deleteOldPost();
        Post post = new Post();
        CleanBD<Post, PostRepository> cleanPost = new CleanBD(post, postRepository);
        cleanPost.deleteTableRow();
        Purchase purchase = new Purchase();
        CleanBD<Purchase, PurchaseRepository> cleanPurchase = new CleanBD(purchase, purchaseRepository);
        cleanPurchase.deleteTableRow();
        try {
            setWarningToCleanRoom(update.getCallbackQuery().getMessage());
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Override
    public boolean apply(Update update) {
        var messagetext = update.getCallbackQuery().getData();
        return messagetext.equals("YESF");
    }
    public void closeBargain(Update update){
        long performerID=0;
        String payload="";
        long postID=0;
        long roomID=0;
        for(int i=0;i<roomsRepository.count();i++){
            if(update.getCallbackQuery().getMessage().getChat().getId().equals(roomsRepository.findById(i+1).get().getRoomID())){
                performerID = roomsRepository.findById(i+1).get().getPerformerID();
                payload = roomsRepository.findById(i+1).get().getPayload();
                postID = roomsRepository.findById(i+1).get().getPostId();
                roomID= roomsRepository.findById(i+1).get().getRoomID();
                break;
            }
        }
        Date currentDate = new Date();
        DateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateToload = sdf.format(currentDate);
        Purchase purchase = purchaseRepository.findById(payload).get();
        purchase.setSuccessfulBargain(true);
        purchase.setDate(dateToload);
        purchaseRepository.save(purchase);
        try {
            deleteMember(roomID, performerID);
        } catch (IOException e) {throw new RuntimeException(e);}
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(Text.byeTxtPerformer);
        sendMessage.setChatId(performerID);
        try {
            // Send the message
            helpbot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(postRepository.findById((int) postID).get().getChanel());
        editMessageText.setMessageId((int) postID);
        editMessageText.setText("Виконано");
        try {
            // Send the message
            helpbot.execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        Post newPost=postRepository.findById((int) postID).get();
        newPost.setActive(false);
        postRepository.save(newPost);
    }
}
