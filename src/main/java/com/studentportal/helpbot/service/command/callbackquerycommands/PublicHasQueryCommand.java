package com.studentportal.helpbot.service.command.callbackquerycommands;

import com.studentportal.helpbot.model.Customer;
import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.PostRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.consts.Subjects;
import com.studentportal.helpbot.service.consts.Text;
import com.studentportal.helpbot.service.dopclasses.Chanels;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
@Component
public class PublicHasQueryCommand extends QueryCommands {
    @Autowired
    private PostRepository postRepository;
    private Chanels chanel;
    public PublicHasQueryCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository,roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
          if (customerRepository.findById(update.getCallbackQuery().getMessage().getChatId()).get().getBranch()==null){
                set_warning(String.valueOf(chatId));
            }
            else if(customerRepository.findById(update.getCallbackQuery().getMessage().getChatId()).get().getBranch().equals(Text.matem_text)){
                try {
                    chanel = new Chanels("@matem_vedmedyk");
                    String post_url = chanel.set_to_chanal(customerRepository, helpbot.getBotToken(), update.getCallbackQuery().getMessage(), postRepository);
                    setPostLinkToTable(update,post_url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if(customerRepository.findById(update.getCallbackQuery().getMessage().getChatId()).get().getBranch().equals(Text.programming_text)){
                try {
                    chanel = new Chanels("@program_vedmedyk");
                    String post_url =  chanel.set_to_chanal(customerRepository, helpbot.getBotToken(), update.getCallbackQuery().getMessage(), postRepository);

                    setPostLinkToTable(update,post_url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if(customerRepository.findById(update.getCallbackQuery().getMessage().getChatId()).get().getBranch().equals(Text.chemistry_text)){
                try {
                    chanel = new Chanels("@chemistry_vedmedyk");
                    String post_url = chanel.set_to_chanal(customerRepository, helpbot.getBotToken(), update.getCallbackQuery().getMessage(), postRepository);

                    setPostLinkToTable(update,post_url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if(customerRepository.findById(update.getCallbackQuery().getMessage().getChatId()).get().getBranch().equals(Text.medicine_text)){
                try {
                    chanel = new Chanels("@medicine_vedmedyk");
                    String post_url = chanel.set_to_chanal(customerRepository, helpbot.getBotToken(),update.getCallbackQuery().getMessage(), postRepository);

                    setPostLinkToTable(update,post_url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if(customerRepository.findById(update.getCallbackQuery().getMessage().getChatId()).get().getBranch().equals(Text.geographe_text)){
                try {
                    chanel = new Chanels("@geogtaphy_vedmedyk");
                    String post_url=chanel.set_to_chanal(customerRepository, helpbot.getBotToken(), update.getCallbackQuery().getMessage(), postRepository);
                    setPostLinkToTable(update,post_url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if(customerRepository.findById(update.getCallbackQuery().getMessage().getChatId()).get().getBranch().equals(Text.phylosophy_text)){
                try {
                    chanel = new Chanels("@phylosophy_vedmedyk");
                    String post_url= chanel.set_to_chanal(customerRepository, helpbot.getBotToken(), update.getCallbackQuery().getMessage(), postRepository);
                    setPostLinkToTable(update,post_url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if(customerRepository.findById(update.getCallbackQuery().getMessage().getChatId()).get().getBranch().equals(Text.languages_text)){
                try {
                    chanel = new Chanels("@languages_vedmedyk");
                    String post_url= chanel.set_to_chanal(customerRepository, helpbot.getBotToken(), update.getCallbackQuery().getMessage(), postRepository);
                    setPostLinkToTable(update,post_url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if(customerRepository.findById(update.getCallbackQuery().getMessage().getChatId()).get().getBranch().equals(Text.another_text)){
                try {
                    chanel = new Chanels("@main_vedmedyk");
                    String post_url= chanel.set_to_chanal(customerRepository, helpbot.getBotToken(), update.getCallbackQuery().getMessage(), postRepository);
                    /* customerData.setPost_url(post_url);*/
                    setPostLinkToTable(update,post_url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            set_post(String.valueOf(chatId),1,update.getCallbackQuery().getMessage().getMessageId(), update.getCallbackQuery().getMessage());
            helpbot.set_main_menu(String.valueOf(chatId), update.getCallbackQuery().getMessage());

    }
    @Override
    public boolean apply(Update update) {
        var messagetext = update.getCallbackQuery().getData();
        return messagetext.equals(Subjects.PUBLIC.toString());
    }
    public void set_warning(String chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(Text.warning);
        try {
            // Send the message
            helpbot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void setPostLinkToTable(Update update, String postUrl){
        if(!customerRepository.findById(update.getCallbackQuery().getMessage().getChatId()).isEmpty()) {
            Customer customer = customerRepository.findById(update.getCallbackQuery().getMessage().getChatId()).get();
            customer.setAgreementsState(true);
            customer.setPostLink(postUrl);
            customerRepository.save(customer);
        }
    }
}
