package com.studentportal.helpbot.service.command.callbackquerycommands;

import com.studentportal.helpbot.model.Customer;
import com.studentportal.helpbot.model.Thief;
import com.studentportal.helpbot.repository.CustomerRepository;
import com.studentportal.helpbot.repository.RoomsRepository;
import com.studentportal.helpbot.repository.ThiefRepository;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.dopclasses.CustomerActions;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
@Component
public class ThiefListHasQueryCommand extends QueryCommands {
    public ThiefListHasQueryCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }
    @Autowired
    private ThiefRepository thiefRepository;
    @Override
    public void resolve(Update update) {
        showThiefList(update);
    }

    @Override
    public boolean apply(Update update) {
        if(update.hasCallbackQuery()) {
            var messagetext = update.getCallbackQuery().getData();
            return messagetext.equals("THIEFLIST");
        }
        return false;
    }
    public void showThiefList(Update update){
//        String list = "";
//        String resultString="";
//        try {
//            String projectPath = System.getProperty("user.dir");
//            String relativePath = "Student-Portal/src/main/java/com/studentportal/StudentPortal/Helpbot/service/command/files/ThiefDataTable";
//            String absolutePath = projectPath + File.separator + relativePath;
//            FileReader fileReader = new FileReader(absolutePath, StandardCharsets.UTF_8);
//            BufferedReader br = new BufferedReader(fileReader);
//            for(int i=0; i<10;i++){
//                list += br.readLine();
//            }
//            fileReader.close();
//            br.close();
//            list=list.replace("null", "");
//            resultString = thiefRow(list,1);
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        List<Thief> thieves = (List<Thief>) thiefRepository.findAll();
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        for (Thief thief : thieves) {
            count++;
            stringBuilder
                    .append(count+") "+ thief.getName()).append(" ")
                    .append(thief.getSurname()).append(" ")
                    .append(thief.getNick()).append("\n");
            if(count==10) break;
        }


        String resultString = stringBuilder.toString();
        CustomerActions customerActions = new CustomerActions(customerRepository);
        String list = customerActions.getThiefList(resultString);
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getFrom().getId());
        sendMessage.setText(list.replaceAll("null", ""));

        InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();

        List<InlineKeyboardButton> row_inline=new ArrayList<>();
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
        sendMessage.setReplyMarkup(inline_keybord);
        try {
            // Send the message
            Message message = helpbot.execute(sendMessage);
            Customer customer = customerRepository.findById(update.getCallbackQuery().getFrom().getId()).get();
            customer.setThiefListState(1);
            customer.setMessageThiefID(message.getMessageId());
            customerRepository.save(customer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
