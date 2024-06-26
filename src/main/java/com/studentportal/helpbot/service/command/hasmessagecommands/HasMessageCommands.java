package com.studentportal.helpbot.service.command.hasmessagecommands;

import com.studentportal.helpbot.model.*;
import com.studentportal.helpbot.repository.CustomerRepository;
import com.studentportal.helpbot.repository.PurchaseRepository;
import com.studentportal.helpbot.repository.RoomsRepository;
import com.studentportal.helpbot.repository.ThiefRepository;
import com.studentportal.helpbot.service.command.Commands;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.dopclasses.CustomerActions;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;

public abstract class HasMessageCommands extends Commands implements BotHasMessageCommand {
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private ThiefRepository thiefRepository;
    HasMessageCommands(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository,roomsRepository);
    }
    public void get_file_description(Update update) throws IOException {
        String file_id = update.getMessage().getDocument().getFileId();
        String urlString = "https://api.telegram.org/bot"+ helpbot.getBotToken()+"/sendDocument?chat_id=@vedmedik_base&document="+file_id;
        String chatId = "@vedmedik_base";
        urlString = String.format(urlString, helpbot.getBotToken(), chatId, file_id);
        Customer customer = customerRepository.findById(update.getMessage().getChatId()).get();
        customer.setAgreementsState(true);
        List fileList = customerRepository.findById(update.getMessage().getChatId()).get().getFileLink();
        fileList.add(urlString);
        customer.setFileLink(fileList);
        customer.setPriceFlag(0);
        customerRepository.save(customer);
    }
    public void get_photo_description(Update update) {
        String photo_id=update.getMessage().getPhoto().get(3).getFileId();
        String urlString = "https://api.telegram.org/bot"+helpbot.getBotToken()+"/sendPhoto?chat_id=@vedmedik_base&photo="+photo_id;
        String chatId = "@vedmedik_base";
        urlString = String.format(urlString, helpbot.getBotToken(), chatId, photo_id);
        Customer customer = customerRepository.findById(update.getMessage().getChatId()).get();
        customer.setAgreementsState(true);
        List photoList = customerRepository.findById(update.getMessage().getChatId()).get().getPhotoLink();
        photoList.add(urlString);
        customer.setPhotoLink(photoList);
        customer.setPriceFlag(0);
        customerRepository.save(customer);
    }

    public void deleteChatMember(Update update) throws IOException {
        long customerID=0;
        long performerID=0;
        long roomID = 0;
        for(int i=0;i<roomsRepository.count();i++){
            if(update.getMessage().getChat().getId().equals(roomsRepository.findById(i+1).get().getRoomID())){
                customerID = roomsRepository.findById(i+1).get().getCustomerID();
                performerID = roomsRepository.findById(i+1).get().getPerformerID();
                roomID = roomsRepository.findById(i+1).get().getRoomID();
                break;
            }
        }
        if(update.getMessage().getLeftChatMember().getId().equals(customerID)){
            deleteMember(roomID,performerID);
        }
        if(update.getMessage().getLeftChatMember().getId().equals(performerID)) {
            deleteMember(roomID, customerID);
        }
        setWarningToCleanRoom(update.getMessage());
    }
    public void setWarningRoguery(Update update){
        for(int i=0; i<roomsRepository.count();i++){
            if(roomsRepository.findById(i+1).get().getRoomID().equals(update.getMessage().getChat().getId())){
                if(roomsRepository.findById(i+1).get().getPerformerID().equals(update.getMessage().getLeftChatMember().getId())){
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(update.getMessage().getChat().getId());
                    sendMessage.setText(Text.warningToCustomer);
                    try {
                        // Send the message
                        helpbot.execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                else if(roomsRepository.findById(i+1).get().getCustomerID().equals(update.getMessage().getLeftChatMember().getId())){
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(update.getMessage().getChat().getId());
                    sendMessage.setText(Text.warningToPerformer);
                    try {
                        // Send the message
                        helpbot.execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void checkThiefFromCustomer(Message message){
        if(!thiefRepository.findById(message.getForwardFrom().getId()).isEmpty()){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(Text.thiefExists);
            sendMessage.setChatId(message.getChatId());
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }else{
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(Text.thiefDoesntExists);
            sendMessage.setChatId(message.getChatId());
            try {
                // Send the message
                helpbot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

}
