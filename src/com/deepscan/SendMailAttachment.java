package com.deepscan;




import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMailAttachment extends AsyncTask {

    //  private ProgressDialog statusDialog;
    private Context sendMailActivity;

    final String emailPort = "587";// gmail's smtp port
    final String smtpAuth = "true";
    final String starttls = "true";
    final String emailHost = "smtp.gmail.com";

    public SendMailAttachment(Context activity) {
        sendMailActivity = activity;

    }

    protected void onPreExecute() {
//        statusDialog = new ProgressDialog(sendMailActivity);
//        statusDialog.setMessage("Getting ready...");
//        statusDialog.setIndeterminate(false);
//        statusDialog.setCancelable(false);
//        statusDialog.show();
    }

    @Override
    protected Object doInBackground(Object... args) {


        String to = "manishsinghal1991@gmail.com";//change accordingly
        final String user = "syon.manish@gmail.com";//change accordingly
        final String password = "syon@123";//change accordingly

        //1) get the session object
        Properties emailProperties ;


        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.starttls.enable", starttls);

        Session session = Session.getDefaultInstance(emailProperties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });

        //2) compose message
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Message Aleart");

            //3) create MimeBodyPart object and set your message text
            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText("This is message body");

            //4) create new MimeBodyPart object and set DataHandler object to this object
            MimeBodyPart messageBodyPart2 = new MimeBodyPart();

            String filename = args[0].toString();//change accordingly
            DataSource source = new FileDataSource(filename);
            messageBodyPart2.setDataHandler(new DataHandler(source));
            messageBodyPart2.setFileName(filename);


            //5) create Multipart object and add MimeBodyPart objects to this object
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart1);
            multipart.addBodyPart(messageBodyPart2);

            //6) set the multiplart object to the message object
            message.setContent(multipart);

            //7) send message
            Transport.send(message);

            System.out.println("message sent....");
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }


        return null;

    }
@Override
public void onProgressUpdate(Object...values){
        // statusDialog.setMessage(values[0].toString());

        }

@Override
public void onPostExecute(Object result){
        //statusDialog.dismiss();

        }

        }