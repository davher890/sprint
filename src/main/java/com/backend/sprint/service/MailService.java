package com.backend.sprint.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.backend.sprint.model.dto.UserDto;

@Service
public class MailService {

	// Sender's email ID needs to be mentioned
	String adminMail = "sportclubsmngr@gmail.com";

	// Assuming you are sending email from through gmails smtp
	String host = "smtp.gmail.com";

	public void sendContactMail(String from, String subject, String text) {

		Session session = getSession();

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(adminMail));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(adminMail));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(from));

			// Set Subject: header field
			message.setSubject(subject);

			// Now set the actual message
			message.setText(text);

			// Send message
			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}

	}

	public void sendMail(String to, String subject, String text) {

		Session session = getSession();

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(adminMail));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: header field
			message.setSubject(subject);

			// Now set the actual message
			message.setText(text);

			// Send message
			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}

	}

	private Session getSession() {
		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// Get the Session object.// and pass username and password
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(adminMail, "REDACTED");
			}
		});

		// Used to debug SMTP issues
		session.setDebug(true);

		return session;
	}

	public void sendExcelMail(ByteArrayOutputStream outputStream) {

		// Recipient's email ID needs to be mentioned.
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = (UserDto) auth.getPrincipal();
		String to = user.getEmail();

		Session session = getSession();

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(adminMail));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: header field
			message.setSubject("Descarga de datos");

			// Now set the actual message
			message.setText("Adjunto se encuentra el excel con la información solicitada");

			Multipart multipart = new MimeMultipart();
			MimeBodyPart attachmentPart = new MimeBodyPart();
			MimeBodyPart textPart = new MimeBodyPart();
			try {
				File f = new File("/tmp/excel.xlsx");
				FileOutputStream fout = new FileOutputStream(f);
				outputStream.writeTo(fout);

				attachmentPart.attachFile(f);
				textPart.setText("Adjunto se encuentra el excel con la información solicitada");
				multipart.addBodyPart(textPart);
				multipart.addBodyPart(attachmentPart);
			} catch (IOException e) {
				e.printStackTrace();
			}

			message.setContent(multipart);
			// Send message
			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}
