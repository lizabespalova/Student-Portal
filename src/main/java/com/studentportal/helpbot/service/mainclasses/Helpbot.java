package com.studentportal.helpbot.service.mainclasses;


import com.studentportal.helpbot.config.HelpbotConfig;
import com.studentportal.helpbot.model.*;
import com.studentportal.helpbot.service.command.Command.CommandFactory;
import com.studentportal.helpbot.service.consts.Buttons;
import com.studentportal.helpbot.service.consts.Quiz;
import com.studentportal.helpbot.service.consts.Text;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Helpbot extends TelegramLongPollingBot {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PerformerRepository performerRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private RoomsRepository roomsRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private ThiefRepository thiefRepository;
    @Autowired
    private CommandFactory commandFactory;

    //    private Chanels chanel;
    private Text const_text;

    final HelpbotConfig config;
    private String text;

    @Override
    public String getBotUsername() {
        return config.getUsername();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    public String getBotTokenPay() {
        return config.getTokenPay();
    }


    public Helpbot(HelpbotConfig helpbotConfig) {
        config = helpbotConfig;
        List<BotCommand> listofCommands = new ArrayList<>();
        text = ("Меню");
        listofCommands.add(new BotCommand("/start", text));
        text = EmojiParser.parseToUnicode(":swan:" + " " + "Оберіть свій статус");
        listofCommands.add(new BotCommand("/status", text));
        text = EmojiParser.parseToUnicode(":dove:" + " " + "Допомога");
        listofCommands.add(new BotCommand("/help", text));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
//        const_text= new Text();
        if (update.hasCallbackQuery()) {
            commandFactory.getCommand(update, (byte) 1).resolve(update);
            return;
        }
        if (update.hasPreCheckoutQuery()) {
            PreCheckoutQuery preCheckoutQuery = update.getPreCheckoutQuery();
            AnswerPreCheckoutQuery answerPreCheckoutQuery = new AnswerPreCheckoutQuery(preCheckoutQuery.getId(), true);
            try {
                execute(answerPreCheckoutQuery);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        if (message != null) {
            String user_sms = message.getText();
            if (user_sms != null) {
                switch (user_sms) {
                    case "/start": {
                        set_main_menu(String.valueOf(message.getChatId()), message);
                        //редагування коду
                        Customer customer = customerRepository.findById(message.getChatId()).get();
                        customer.setAgreementsState(true);
                        customer.setThiefListState(0);
                        customer.setCheck_state(true);
                        customer.setPhotoLink(null);
                        customer.setFileLink(null);
                        customer.setPriceFlag(1);
                        customerRepository.save(customer);
                        break;
                    }
                    case "/help": {
                        help_button(String.valueOf(message.getChatId()));
                        rules_buttons(String.valueOf(message.getChatId()));
                        break;
                    }
                    case "/status": {
                        set_menu_Reply(String.valueOf(message.getChatId()));
                        set_menu_Inline(String.valueOf(message.getChatId()));
                        break;
                    }
                }
                commandFactory.getCommand(update, (byte) 2).resolve(update);
                return;
            }
        }
        if (update.hasMessage()) {
            commandFactory.getCommand(update, (byte) 3).resolve(update);
            return;
        }
    }

    public void set_main_menu(String chatId, Message message) {
        SendMessage main_menu_sms = new SendMessage();
        main_menu_sms.setChatId(chatId);
        main_menu_sms.setText(EmojiParser.parseToUnicode("Оберіть дію:" + ":blush:"));
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> menu = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(Text.agreement_text);
        KeyboardRow row1 = new KeyboardRow();
        row1.add(Text.thiefText);
        KeyboardRow row2 = new KeyboardRow();
        row2.add(EmojiParser.parseToUnicode(Text.performerRegister));
        menu.add(row);
        menu.add(row1);
        menu.add(row2);
        keyboard.setKeyboard(menu);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        keyboard.setSelective(true);
        main_menu_sms.setReplyMarkup(keyboard);
        if (!customerRepository.findById(message.getChatId()).isEmpty()) {
            Customer customer = customerRepository.findById(message.getChatId()).get();
            customer.setAgreementsState(true);
            customer.setState(Quiz.MAINMENU.toString());
            customer.setPriceFlag(0);
            customer.setCheck_state(true);
            customerRepository.save(customer);
        } else {
            Customer customer = new Customer();
            customer.setChatID(message.getChatId());
            customer.setSurname(message.getFrom().getLastName());
            customer.setName(message.getFrom().getFirstName());
            customer.setUser_nick(message.getFrom().getUserName());
            customer.setCheckDescriptionState(0);
            customer.setCheck_state(true);
            customer.setState(Quiz.MAINMENU.toString());
            customerRepository.save(customer);
        }

        try {
            // Send the message
            execute(main_menu_sms);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void set_menu_Reply(String chatId) {
        SendMessage main_menu_sms = new SendMessage();
        main_menu_sms.setChatId(chatId);
        text = EmojiParser.parseToUnicode("Привіт, друже" + ":wave:");
        main_menu_sms.setText(text);
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        text = EmojiParser.parseToUnicode("Правила користування ботом" + ":clipboard:");
        List<KeyboardRow> menu = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(text);
        menu.add(row);
        keyboard.setKeyboard(menu);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        keyboard.setSelective(true);
        main_menu_sms.setReplyMarkup(keyboard);
        try {
            // Send the message
            execute(main_menu_sms);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void set_menu_Inline(String chatId) {
        SendMessage main_menu_sms = new SendMessage();
        main_menu_sms.setChatId(chatId);
        main_menu_sms.setText("Обери дію:");
        InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();
        List<InlineKeyboardButton> row_inline = new ArrayList<>();
        var customer_Button = new InlineKeyboardButton();
        text = EmojiParser.parseToUnicode("Я замовник" + ":woman_office_worker:");
        customer_Button.setText(text);
        customer_Button.setCallbackData(Buttons.CUSTOMER.toString());
        var permormer_Button = new InlineKeyboardButton();
        text = EmojiParser.parseToUnicode("Я виконавець" + ":woman_technologist:");
        permormer_Button.setText(text);
        permormer_Button.setCallbackData(Buttons.PERFOMER.toString());
        row_inline.add(customer_Button);
        row_inline.add(permormer_Button);
        rows_inline.add(row_inline);
        inline_keybord.setKeyboard(rows_inline);
        main_menu_sms.setReplyMarkup(inline_keybord);
        try {
            execute(main_menu_sms);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void help_button(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        text = EmojiParser.parseToUnicode("Якщо виникли питання звертайтесь до адміністратора:" + ":rose:" + "\n\n\n" + "@lizabespalova");
        sendMessage.setText(text);
        try {
            // Send the message
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void rules_buttons(String chatId) {
        SendMessage main_menu_sms = new SendMessage();
        main_menu_sms.setChatId(chatId);
        text = EmojiParser.parseToUnicode("Обери тип інформації, з яким ти хочешь ознайомитись:" + ":book:");
        main_menu_sms.setText(text);
        InlineKeyboardMarkup inline_keybord = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows_inline = new ArrayList<>();
        List<InlineKeyboardButton> row_inline1 = new ArrayList<>();
        List<InlineKeyboardButton> row_inline2 = new ArrayList<>();
        List<InlineKeyboardButton> row_inline3 = new ArrayList<>();
        var bot_guide = new InlineKeyboardButton();
        text = EmojiParser.parseToUnicode("Правила користування ботом" + ":robot_face:");
        bot_guide.setText(text);
        bot_guide.setUrl("https://telegra.ph/Pravila-koristuvannya-botom-Vedmedik-05-01");
        bot_guide.setCallbackData(Buttons.RULEBOT.toString());
        var bot_payment = new InlineKeyboardButton();
        text = EmojiParser.parseToUnicode("Правила оплати" + ":money_with_wings:");
        bot_payment.setText(text);
        bot_payment.setUrl("https://telegra.ph/Pravila-oplati-01-01");
        bot_payment.setCallbackData(Buttons.RULEPAYMENT.toString());
        var bot_relations = new InlineKeyboardButton();
        text = EmojiParser.parseToUnicode("Правила угоди" + ":handshake:");
        bot_relations.setText(text);
        bot_relations.setUrl("https://telegra.ph/Pravila-ugodi-01-01");
        bot_relations.setCallbackData(Buttons.RULERELATE.toString());
        row_inline1.add(bot_guide);
        row_inline2.add(bot_payment);
        row_inline3.add(bot_relations);
        rows_inline.add(row_inline1);
        rows_inline.add(row_inline2);
        rows_inline.add(row_inline3);
        inline_keybord.setKeyboard(rows_inline);
        main_menu_sms.setReplyMarkup(inline_keybord);
        try {
            execute(main_menu_sms);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}






















