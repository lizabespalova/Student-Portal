package com.studentportal.helpbot.service.command.callbackquerycommands;

import com.studentportal.helpbot.model.Customer;
import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.consts.Subjects;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
@Component
public class ChangeSubjectHasQueryCommand extends QueryCommands {
    public ChangeSubjectHasQueryCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository,roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        set_change_menu(String.valueOf(chatId));
    }

    @Override
    public boolean apply(Update update) {
        var messagetext = update.getCallbackQuery().getData();
        return messagetext.equals(Subjects.CHANGE.toString());
    }
    public void set_change_menu(String chatId) {
        SendMessage main_menu_sms = new SendMessage();
        main_menu_sms.setChatId(chatId);
        main_menu_sms.setText(EmojiParser.parseToUnicode("Оберіть на який крок треба повернутися: " + ":dancer:"));
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> menu = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(Text.change_subject);
        row.add(Text.change_description);
        KeyboardRow row1 = new KeyboardRow();
        row1.add(Text.change_photo_or_file);
        row1.add(Text.change_price);
        menu.add(row);
        menu.add(row1);
        keyboard.setKeyboard(menu);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        keyboard.setSelective(true);
        main_menu_sms.setReplyMarkup(keyboard);
        Customer customer = customerRepository.findById(Long.valueOf(chatId)).get();
        customer.setCheck_state(false);
        customerRepository.save(customer);
        try {
            // Send the message
            helpbot.execute(main_menu_sms);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
