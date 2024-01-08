package com.studentportal.helpbot.service.dopclasses;

//import com.studentportal.StudentPortal.Helpbot.config.HelpbotConfig;

import com.studentportal.helpbot.model.Customer;
import com.studentportal.helpbot.model.CustomerRepository;
import com.studentportal.helpbot.model.Post;
import com.studentportal.helpbot.model.PostRepository;
import com.studentportal.helpbot.service.consts.Text;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Chanels {

    private static final double MATH_PI = 3.14;

    private Text const_text;
    private String chanel;

    public Chanels(String chanel) {
        this.chanel = chanel;
    }

    public String set_to_chanal(CustomerRepository customerRepository, String token, Message message, PostRepository postRepository) throws IOException {
        CustomerActions customerActions = new CustomerActions(customerRepository);
        String post = customerActions.post_tostr(0, message);
        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&reply_markup=%s&parse_mode=%s&text=%s";
        String reply = URLEncoder.encode("{\"inline_keyboard\":[[{\"text\":\"" + "Візьму" + "\",\"callback_data\":\"Візьму\"}]]}", "UTF-8");
        String parse = "HTML";
        post = URLEncoder.encode(post, "UTF-8");
        urlString = String.format(urlString, token, chanel, reply, parse, post);
        URL newurl = new URL(urlString);
        URLConnection conn = newurl.openConnection();
        StringBuilder sb = new StringBuilder();
        InputStream is = new BufferedInputStream(conn.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String inputLine = "";
        String digits = "";
        int check = 0;
        while ((inputLine = br.readLine()) != null) {
            sb.append(inputLine);
            for (int i = 0; i < inputLine.length(); i++) {
                char chrs = inputLine.charAt(i);
                if (Character.isDigit(chrs)) {
                    digits = digits + chrs;

                }
                if (chrs == ',') {
                    check++;
                }
                if (check == 2) {
                    break;
                }
            }
            if (check == 2) {
                break;
            }

        }
        Date currentDate = new Date();
        DateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateToload = sdf.format(currentDate);
//        String urlList=("https://t.me/matem_vedmedyk/"+ digits);
        String urlList = ("https://t.me/" + chanel.substring(1) + "/" + digits);
        Customer customer = customerRepository.findById(message.getChatId()).get();
        customer.setPostLink(urlList);
        customerRepository.save(customer);
        Post newPost = new Post();
        newPost.setMessageID(Integer.valueOf(digits));
        newPost.setLink(urlList);
        newPost.setCustomer_id(message.getChatId());
        newPost.setChanel(chanel);
        newPost.setActive(true);
        newPost.setDate(dateToload);
        postRepository.save(newPost);
        return urlList;
    }
}
