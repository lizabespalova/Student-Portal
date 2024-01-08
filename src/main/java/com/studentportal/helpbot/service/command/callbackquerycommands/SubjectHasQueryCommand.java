package com.studentportal.helpbot.service.command.callbackquerycommands;

import com.studentportal.helpbot.model.Customer;
import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.RoomsRepository;
import com.studentportal.helpbot.service.consts.Subjects;
import com.studentportal.helpbot.service.dopclasses.Subjectgetters;
import com.studentportal.helpbot.service.mainclasses.Helpbot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class SubjectHasQueryCommand extends QueryCommands {

    private String messagetext;

    public SubjectHasQueryCommand(Helpbot helpbot, CustomerRepository customerRepository, RoomsRepository roomsRepository) {
        super(helpbot, customerRepository,roomsRepository);
    }

    @Override
    public void resolve(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        Subjectgetters subjectgetters = new Subjectgetters();
        String strsubject = subjectgetters.getsubject(messagetext);
        setBranchToTable(update.getCallbackQuery().getMessage(),strsubject);
        if(!customerRepository.findById(update.getCallbackQuery().getMessage().getChatId()).get().isCheck_state()/*.getCheck_state()*/){set_post(String.valueOf(chatId),0,update.getCallbackQuery().getMessage().getMessageId(),update.getCallbackQuery().getMessage()); set_last_buttons(String.valueOf(chatId));}
        else set_text_description(chatId, update.getCallbackQuery().getMessage());
    }

    @Override
    public boolean apply(Update update) {
        messagetext = update.getCallbackQuery().getData();
        return messagetext.equals(Subjects.MATH.toString())||messagetext.equals(Subjects.PROGRAMMING.toString())||
                messagetext.equals(Subjects.PHYLOSOPHY.toString())||messagetext.equals(Subjects.GEOGRAPHY.toString())||
                messagetext.equals(Subjects.LANGUAGES.toString())||messagetext.equals(Subjects.ANOTHER.toString())||
                messagetext.equals(Subjects.CHEMISTRY.toString())|| messagetext.equals(Subjects.MEDICINE.toString());
    }
    public void setBranchToTable(Message message, String strsubject){
        if(!customerRepository.findById(message.getChatId()).isEmpty()) {
            Customer customer = customerRepository.findById(message.getChatId()).get();
            customer.setBranch(strsubject);
            customer.setAgreementsState(true);
            customer.setCheckDescriptionState(1);
            customer.setPriceFlag(0);
            customerRepository.save(customer);
        }
    }
}
