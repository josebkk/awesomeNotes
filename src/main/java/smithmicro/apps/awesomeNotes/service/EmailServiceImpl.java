package smithmicro.apps.awesomeNotes.service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service("emailService")
public class EmailServiceImpl implements EmailService{


	@Autowired
	private Environment environment;


	public void sendSimpleMessage(String to, String subject, String msg) {

		final String from = "awesomenotesapp@gmail.com";
		final String username = environment.getProperty("awesomenotesapp.email.username");
		final String password = environment.getProperty("awesomenotesapp.email.password");

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			message.setSubject(subject);
			message.setContent(msg, "text/html; charset=utf-8");
			Transport.send(message);



		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

	}
}


