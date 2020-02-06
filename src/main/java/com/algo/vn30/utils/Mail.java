package com.algo.vn30.utils;
import com.sun.mail.smtp.SMTPTransport;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Mail {

    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final String USERNAME = "algo.greenstock@gmail.com";
    private static final String PASSWORD = "46nguynhukontum";

    private static final String EMAIL_FROM = "algo.greenstock@gmail.comm";
//    private static final String EMAIL_TO = "tuanlv.k57@gmail.com,ntmanh2904@gmail.com,manhluan.vn@gmail.com,nguyenhuyduong228@gmail.com,dqhungdl@gmail.com";
//    private static final String EMAIL_TO = "tuanlv.k57@gmail.com,ntmanh2904@gmail.com,manhluan.vn@gmail.com,dqhungdl@gmail.com";
    private static final String EMAIL_TO = "tuanlv.k57@gmail.com";
    private static final String EMAIL_TO_CC = "ntmanh2904@gmail.com";

    private static final String EMAIL_SUBJECT = "Báo cáo kết quả review VN30 ngày: ";
    private static final String EMAIL_TEXT = "(Chi tiết vui lòng xem file đính kèm)";

    public static void sendGmail(String path){
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        Session session = Session.getInstance(props, null);
        MimeMessage msg = new MimeMessage(session);

        try {
            String encodingOptions = "text/html; charset=UTF-8";
            msg.setFrom(new InternetAddress(EMAIL_FROM));
            String email_to = FileUtil.readEmails();
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email_to, false));

            msg.setHeader("Content-Type", encodingOptions);
            msg.setSubject(EMAIL_SUBJECT + format.format(date), "UTF-8");

            // text
            MimeBodyPart p1 = new MimeBodyPart();
            p1.setText(EMAIL_TEXT, "utf-8");
            // file
            MimeBodyPart p2 = new MimeBodyPart();
            FileDataSource fds = new FileDataSource(path);
            p2.setDataHandler(new DataHandler(fds));
            p2.setFileName(fds.getName());

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(p1);
            mp.addBodyPart(p2);

            msg.setContent(mp);


            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");

            // connect
            t.connect(SMTP_SERVER, USERNAME, PASSWORD);

            // send
            t.sendMessage(msg, msg.getAllRecipients());

            t.close();

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
