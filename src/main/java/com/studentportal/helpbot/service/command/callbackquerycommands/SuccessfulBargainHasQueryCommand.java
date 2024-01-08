package com.studentportal.helpbot.service.command.callbackquerycommands;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.PostRepository;
import com.studentportal.helpbot.model.Rooms;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.consts.Subjects;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.dopclasses.CustomerActions;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//згода на угоду
@Component
public class SuccessfulBargainHasQueryCommand extends QueryCommands {
    @Autowired
    private PostRepository postRepository;
    public SuccessfulBargainHasQueryCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository, roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var messagetext = update.getCallbackQuery().getData();
        boolean flag = true;
        String performerID="";
        String postID="";
        for (int i = 0; i < messagetext.length(); i++) {
            char chrs = messagetext.charAt(i);
            if (Character.isDigit(chrs) && flag) {
                postID = postID + chrs;
            }
            if (Character.isDigit(chrs) && !flag) {
                performerID = performerID + chrs;
            }
            if(chrs == ',') {
                flag=false;
            }

        }

        int roomId=return_chat_link_and_show_sms_in_group(chatId, update);
        return_chat_link_and_show_sms_for_performer_in_group( performerID, postID, roomId);
        try {
            set_sms_in_chat(roomId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public int  return_chat_link_and_show_sms_in_group(long chatID, Update update){
        int room_num = 0;
        String postUrl = null;
        for(int i=0;i<roomsRepository.count();i++) {
            if(roomsRepository.findById(i+1).get().isIsFree()) {
                postUrl = roomsRepository.findById(i + 1).get().getChatLink();
                room_num = i+1;
                Date date = new Date();
                String currentDate = String.valueOf(date);
                Rooms rooms = roomsRepository.findById(i+1).get();
                rooms.setIsFree(false);
                rooms.setDate(currentDate);
                rooms.setFollowing(0);
                rooms.setCustomerID(update.getCallbackQuery().getMessage().getChatId());
                roomsRepository.save(rooms);
                break;
            }
        }
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatID);
        editMessageText.setMessageId((int) update.getCallbackQuery().getMessage().getMessageId());
        editMessageText.setText("Посилання на чат з виконавцем");
        InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();
        List<InlineKeyboardButton> row_inline=new ArrayList<>();
        var link_Button = new InlineKeyboardButton();
        link_Button.setText(Text.go_chat);
        link_Button.setUrl(postUrl);
        link_Button.setCallbackData(Subjects.LINK.toString()+","+room_num);
        row_inline.add(link_Button);
        rows_inline.add(row_inline);
        inline_keybord.setKeyboard(rows_inline);
        editMessageText.setReplyMarkup(inline_keybord);
        try {
            // Send the message
            helpbot.execute(editMessageText);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return room_num;
    }
    public void return_chat_link_and_show_sms_for_performer_in_group(String performerID, String postID, int roomID){
        Rooms rooms = roomsRepository.findById(roomID).get();
        rooms.setPerformerID(Long.valueOf(performerID));
        rooms.setPostId(Long.valueOf(postID));
        roomsRepository.save(rooms);
        String url = roomsRepository.findById(roomID).get().getChatLink();
        CustomerActions customerActions = new CustomerActions(customerRepository);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(performerID);
        sendMessage.setParseMode("HTML");
        sendMessage.setText("Посилання на чат з користувачем з посту:\n"+ postRepository.findById(Integer.valueOf(postID)).get().getLink() /*customerActions.get_customer_post_link_tostr(update,postRepository)*/);
        InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();
        List<InlineKeyboardButton> row_inline=new ArrayList<>();
        var link_Button = new InlineKeyboardButton();
        link_Button.setText(Text.go_chat);
        link_Button.setUrl(url);
        link_Button.setCallbackData(Subjects.LINK.toString()+","+roomID);
        row_inline.add(link_Button);
        rows_inline.add(row_inline);
        inline_keybord.setKeyboard(rows_inline);
        sendMessage.setReplyMarkup(inline_keybord);

        try {
            // Send the message
            helpbot.execute(sendMessage);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
    public void set_sms_in_chat(int roomID) throws IOException {
        CustomerActions customerActions=new CustomerActions(customerRepository);
        String post = customerActions.set_in_group_info_tostr();
        long chatId =roomsRepository.findById(roomID).get().getRoomID();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("HTML");
        sendMessage.setText(post);

        InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();


        var endAgreementButton = new InlineKeyboardButton();
        endAgreementButton.setText(Text.endBargain);
        endAgreementButton.setCallbackData("CLOSE");
        List<InlineKeyboardButton> row_inline=new ArrayList<>();
        var price_Button = new InlineKeyboardButton();
        price_Button.setText(Text.priceChat);
        price_Button.setCallbackData("PRICE");

        row_inline.add(endAgreementButton);
        row_inline.add(price_Button);
        rows_inline.add(row_inline);
        inline_keybord.setKeyboard(rows_inline);
        sendMessage.setReplyMarkup(inline_keybord);
        try{
            Message messageId= helpbot.execute(sendMessage);
//           sendApiMethodAsync(PinChatMessage.builder()
//                    .chatId(messageID.getChatId().toString())
//                    .messageId(messageID.getMessageId())
//                    .build());
            PinChatMessage pinChatMessage = new PinChatMessage();
            pinChatMessage.setChatId(chatId);
            pinChatMessage.setMessageId(messageId.getMessageId());
                helpbot.execute(pinChatMessage);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean apply(Update update) {
        var messagetext = update.getCallbackQuery().getData();
        return Character.isDigit(messagetext.charAt(0));
    }
}
