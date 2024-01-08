package com.studentportal.helpbot.service.command.callbackquerycommands;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.PurchaseRepository;
import com.studentportal.helpbot.model.Rooms;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
@Component
public class NoHasQueryCommand extends QueryCommands {
    @Autowired
    private PurchaseRepository purchaseRepository;
    public NoHasQueryCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        String Payload = "";
        int price=0;
        for(int i=0; i<roomsRepository.count();i++){
            if(roomsRepository.findById(i+1).get().getRoomID().equals(update.getCallbackQuery().getMessage().getChat().getId())){
                Payload = roomsRepository.findById(i+1).get().getPayload();
                price = roomsRepository.findById(i+1).get().getPrice();
                break;
            }
        }
        if(Payload==null||purchaseRepository.findById(Payload).get().getPriceToPerformer()==0) {
            try {
                check_performer_return_no(update,price);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            blockActionsWhilePayed(update);
        }
    }

    @Override
    public boolean apply(Update update) {
        var messagetext = update.getCallbackQuery().getData();
        return messagetext.equals("NO");
    }
    public void check_performer_return_no(Update update, int user_price)throws IOException{
        long chatID = 0;
        long performerID=0;
        for (int i=0; i<roomsRepository.count();i++){
            if (update.getCallbackQuery().getMessage().getChat().getId().equals(roomsRepository.findById(i+1).get().getRoomID())){
                chatID=roomsRepository.findById(i+1).get().getRoomID();
                performerID=roomsRepository.findById(i+1).get().getPerformerID();
                break;
            }
        }
        if( performerID != update.getCallbackQuery().getFrom().getId()){
            String post = ("Ми просимо відповісти саме виконавця (людині, яка буде виконувати завдання)");
            String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
            post = URLEncoder.encode(post, "UTF-8");
            urlString = String.format(urlString, helpbot.getBotToken(), chatID, post);
            URL newurl = new URL(urlString);
            URLConnection conn = newurl.openConnection();
            StringBuilder sb = new StringBuilder();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            sms_to_performer(user_price,update.getCallbackQuery().getMessage());
        }
        else{
            String post = ("Виконавець не згідний за ціною, спробуйте домовитися ще");
            String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
            for (int i=0; i<roomsRepository.count();i++){
                if (update.getCallbackQuery().getMessage().getChat().getId().equals(roomsRepository.findById(i+1).get().getRoomID())){
                    chatID=roomsRepository.findById(i+1).get().getRoomID();
                    break;
                }
            }
            post = URLEncoder.encode(post, "UTF-8");
            urlString = String.format(urlString, helpbot.getBotToken(), chatID, post);
            URL newurl = new URL(urlString);
            URLConnection conn = newurl.openConnection();
            StringBuilder sb = new StringBuilder();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }

            for(int i=0;i<roomsRepository.count();i++) {
                if (update.getCallbackQuery().getMessage().getChat().getId().equals(roomsRepository.findById(i + 1).get().getRoomID())) {
                    Rooms rooms = roomsRepository.findById(i + 1).get();
                    rooms.setStateInChat(1);
                    roomsRepository.save(rooms);
                    break;
                }
            }
        }
    }
}
