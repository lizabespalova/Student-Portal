package com.studentportal.helpbot.service.command.callbackquerycommands;

import com.studentportal.helpbot.model.Customer;
import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.command.Commands;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.dopclasses.CustomerActions;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public abstract class QueryCommands extends Commands implements BotHasQueryCommand {

     QueryCommands(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
         super(helpbot, customerRepository,roomsRepository);
     }

    public void blockActionsWhilePayed(Update update){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setText(Text.blockPay);
        try {
            // Send the message
           helpbot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public String thiefRow(String list, int state){
        String resultString = "";
        long thiefID=0;
        String name="";
        String Surname="";
        String nick="";
        short count = 0;
        int k=state*10-9;
        char[]symbols=list.toCharArray();
        for(int i=0; i<list.length();i++){
            if(symbols[i]=='=') {
                i++;
                while(symbols[i]!=',') {
                    if (count == 0) {
                        thiefID += symbols[i];
                    } else if (count == 1) {
                        name+=symbols[i];
                    } else if (count == 2) {
                        Surname+=symbols[i];
                    } else if (count == 3) {
                        nick +=symbols[i];
                    }
                    i++;
                }
                count+=1;
                if(count>3){
                    resultString+="\n"+k+")"+name + " "+ Surname+" "+nick;
                    thiefID = 0;
                    name = "";
                    Surname="";
                    nick="";
                    count=0;
                    k++;
                }
            }
        }
        return resultString;
    }
    public void turnList(boolean flag, Message message) {
        int state = customerRepository.findById(message.getChatId()).get().getThiefListState();
        String list = "";
        String newStr = "";
        String resultStr = "";
        if (flag) {
            state -= 1;
            if (state == 0) {

            } else {
                try {
                    String projectPath = System.getProperty("user.dir");
                    String relativePath = "Student-Portal/src/main/java/com/studentportal/StudentPortal/Helpbot/service/command/files/ThiefDataTable";
                    String absolutePath = projectPath + File.separator + relativePath;
                    FileReader fileReader = new FileReader(absolutePath, StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(fileReader);
                    String mess="";
                    for (int i = 0; i < state * 10; i++) {
                        if(i>= (state-1)* 10)
                            list += br.readLine();
                        else mess+=br.readLine();
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                CustomerActions customerActions = new CustomerActions(customerRepository);
                newStr = thiefRow(list,state);
                resultStr = customerActions.getThiefList(newStr);
                Customer customer = customerRepository.findById(message.getChatId()).get();
                customer.setThiefListState(state);
                customerRepository.save(customer);
                InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();
                List<InlineKeyboardButton> row_inline = new ArrayList<>();
                var leftButton = new InlineKeyboardButton();
                leftButton.setText(Text.leftSide);
                leftButton.setCallbackData("LEFT");
                var rightButton = new InlineKeyboardButton();
                rightButton.setText(Text.rightSide);
                rightButton.setCallbackData("RIGHT");
                row_inline.add(leftButton);
                row_inline.add(rightButton);
                rows_inline.add(row_inline);
                inline_keybord.setKeyboard(rows_inline);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setText(resultStr);
                editMessageText.setMessageId(customerRepository.findById(message.getChatId()).get().getMessageThiefID());
                editMessageText.setChatId(message.getChatId());
                editMessageText.setReplyMarkup(inline_keybord);
                try {
                    // Send the message
                    helpbot.execute(editMessageText);
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
            }

        } else {
            state += 1;
            try {
                String projectPath = System.getProperty("user.dir");
                String relativePath = "Student-Portal/src/main/java/com/studentportal/StudentPortal/Helpbot/service/command/files/ThiefDataTable";
                String absolutePath = projectPath + File.separator + relativePath;
                FileReader fileReader = new FileReader(absolutePath, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(fileReader);
                String mess="";
                for (int i = 0; i < state * 10; i++) {
                    if(i>= (state-1)* 10)
                        list += br.readLine();
                    else mess+=br.readLine();
                }
                String checklist = list.replace("null", "");

                if(checklist.isEmpty()/*list.equals("nullnullnullnullnullnullnullnullnullnullnullnullnullnullnullnullnullnullnullnull")||list.equals("")*/){

                }else {
                    CustomerActions customerActions = new CustomerActions(customerRepository);
                    newStr = thiefRow(list,state);
                    resultStr = customerActions.getThiefList(newStr);
                    InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();
                    List<InlineKeyboardButton> row_inline = new ArrayList<>();
                    var leftButton = new InlineKeyboardButton();
                    leftButton.setText(Text.leftSide);
                    leftButton.setCallbackData("LEFT");
                    var rightButton = new InlineKeyboardButton();
                    rightButton.setText(Text.rightSide);
                    rightButton.setCallbackData("RIGHT");
                    row_inline.add(leftButton);
                    row_inline.add(rightButton);
                    rows_inline.add(row_inline);
                    inline_keybord.setKeyboard(rows_inline);
                    Customer customer = customerRepository.findById(message.getChatId()).get();
                    customer.setThiefListState(state);
                    customerRepository.save(customer);

                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setText(resultStr);
                    editMessageText.setMessageId(customerRepository.findById(message.getChatId()).get().getMessageThiefID());
                    editMessageText.setChatId(message.getChatId());
                    editMessageText.setReplyMarkup(inline_keybord);
                    try {
                        // Send the message
                        helpbot.execute(editMessageText);
                    } catch (TelegramApiException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
