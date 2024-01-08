package com.studentportal.helpbot.service.dopclasses;

import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.PostRepository;
import com.studentportal.helpbot.service.consts.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component

public class CustomerActions {
    @Autowired
    private CustomerRepository customerRepository;
    private Text constText = new Text();
    public CustomerActions(CustomerRepository customerRepository){
     this.customerRepository =customerRepository;
 }
    public int finish_price_for_customer(int price){
        if(price<=200){
            return price+10;
        }
        else{
            double procent = (double) price/(double) 100;
            return (int) (procent*5+price);
        }
    }
    public int finish_price_for_performer(int price){
        if(price<=200){
            return price-10;
        }
        else{
            double procent = (double) price/(double) 100;
            return (int) (price-procent*5);
        }
    }
    public StringBuilder set_customer_post(Message message) {
        StringBuilder sb = new StringBuilder(/*"<b>"*/);
        sb.append(Text.active_state);
        sb.append("\n\n\n");
        String branch = customerRepository.findById(message.getChatId()).get().getBranch();
        sb.append("<b>Галузь: </b>"+  branch);
        sb.append("\n\n\n");
        sb.append("<b>Опис: </b>");
        String description = customerRepository.findById(message.getChatId()).get().getDescription();
        sb.append(description);
        sb.append("\n\n\n");
        sb.append("<b>"+Text.price_text+"</b>"+": "+ customerRepository.findById(message.getChatId()).get().getPrice());
        sb.append("\n");

        if(customerRepository.findById(message.getChatId()).get().getFileLink()!=null) {
            for (int i=0; i<customerRepository.findById(message.getChatId()).get().getFileLink().size();i++) {
                sb.append("<a href= \"");
                sb.append(customerRepository.findById(message.getChatId()).get().getFileLink().get(i));
                sb.append("\">");
                sb.append("Прикріплений файл\n");
                sb.append("</a>");
            }
        }
        if(customerRepository.findById(message.getChatId()).get().getPhotoLink()!=null) {
            for (int i=0; i<customerRepository.findById(message.getChatId()).get().getPhotoLink().size();i++) {
                sb.append("<a href= \"");
                sb.append(customerRepository.findById(message.getChatId()).get().getPhotoLink().get(i));
                sb.append("\">");
                sb.append("Прикріплене фото\n");
                sb.append("</a>");
            }
        }

        return sb;
    }
    public String post_tostr(int check, Message message){
       if(check==0) {
           return set_customer_post(message).toString();
       }else {
           return get_customer_post(message).toString();
       }
    }
    public StringBuilder get_customer_post(Message message){
        StringBuilder sb = new StringBuilder(/*"<b>"*/);
        sb.append("Ось ваш пост у каналі:");
        sb.append("\n");
            sb.append("<a href= \"");
            sb.append(customerRepository.findById(message.getChatId()).get().getPostLink());
            sb.append("\">");
            sb.append("Прикріплений пост\n");
            sb.append("</a>");
        return sb;
    }
    public StringBuilder get_customer_post_link(Update update, PostRepository postRepository){
        StringBuilder sb = new StringBuilder();
        sb.append(postRepository.findById(update.getCallbackQuery().getMessage().getMessageId()).get().getLink());/*customerRepository.findById(update.getCallbackQuery().getMessage().getChatId()).get().getPostLink()*/
        return sb;
    }
    public String get_customer_post_link_tostr(Update update, PostRepository postRepository){
            return get_customer_post_link(update, postRepository).toString();
    }
    public StringBuilder set_in_group_info(){
        StringBuilder sb = new StringBuilder();
        sb.append(Text.chat_text);
        sb.append("\n\n\n");
        sb.append("Адміністрація:");
        sb.append("\n");
                sb.append("<a href= \"");
                sb.append("@lizabespalova");
                sb.append("\">");
                sb.append("@lizabespalova\n");
                sb.append("</a>");
        return sb;
    }
    public String set_in_group_info_tostr(){
        return set_in_group_info().toString();
    }
    public StringBuilder setThiefList(String list){
        StringBuilder sb = new StringBuilder();
        sb.append(Text.thiefListToAppand);
        sb.append("\n");
        sb.append(list);
        return sb;
    }
    public String getThiefList(String list){
        return setThiefList(list).toString();
    }
    public StringBuilder setPerformersInformation(String userName, String userSurname, String post, String rating, double bargainsAmount){
      String stars = "";
      String halbestars = "";
        double fractionalPart=0;
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(rating);
        Pattern pattern1 = Pattern.compile("\\d+\\.\\d+");
        Matcher matcher1 = pattern1.matcher(rating);
        int extractedNumber=0;
        double extractedNumber1 =0;
        // Находим первое вхождение числа в строке
        if (matcher.find()) {
             extractedNumber = Integer.parseInt(matcher.group());
        } if (matcher1.find()) {
            extractedNumber1 = Double.parseDouble(matcher.group());
            fractionalPart = extractedNumber % 1; // Получаем дробную часть числа
        }
        if(extractedNumber!=0){for(int i=0; i< extractedNumber; i++){
            stars+=Text.star;
        }}
        if(extractedNumber1!=0){
            if(fractionalPart<5){
                halbestars+=Text.dunklestar;
            }
            else if(fractionalPart>=5){
                halbestars+=Text.hellestar;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Користувач: "+ userName+" "+userSurname);
        sb.append("\n");
        sb.append("Рейтинг:"+rating+stars+halbestars);
        sb.append("\n");
        sb.append("Кількість угод:"+bargainsAmount);
        sb.append("\n");
        sb.append("Завдання, яке вона/він готова/ий виконати:"+ "\n"+post);
        sb.append("\n");
        return sb;
    }
    public String getPerformersInformation(String userName, String userSurname, String post, String rating, double bargainsAmount){
        return setPerformersInformation( userName,  userSurname,  post,  rating,  bargainsAmount).toString();
    }
}
