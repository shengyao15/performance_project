/**
 * Copyright : HP
 * project : Report
 * Create by : Steven Zhang(Ling Kai)
 * Create on : 2011-6-29
 */
package com.hp.ucmdb.report.send;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.hp.ucmdb.report.build.BuildMail;
import com.hp.ucmdb.report.util.AllConstants;
import com.hp.ucmdb.report.util.ReportUtil;

public class SendMail {

	private String mailTo = null;
	private String mailFrom = null;
	private String smtpHost = null;
	private String subject;
	private String msgContent;
	String attachedFilename = "";
	private Vector<String> attachedFileList = new Vector<String>();
	private String messageContentMimeType = "text/html; charset=UTF-8";
	private String mailbccTo = null;
	private String mailccTo = null;

	public void init() {
		this.setSmtpHost(ReportUtil.getConfig().getString(AllConstants.SMTP_HOST));
		this.setMailFrom(ReportUtil.getConfig().getString(AllConstants.MAIL_FROM));
		this.setMailTo(ReportUtil.getConfig().getString(AllConstants.MAIL_TO).replaceAll(";", ","));
		this.setMailccTo(ReportUtil.getConfig().getString(AllConstants.MAIL_CC_TO).replaceAll(";", ","));
		this.setMailbccTo(ReportUtil.getConfig().getString(AllConstants.MAIL_BCC_TO).replaceAll(";", ","));
		this.setSubject(BuildMail.buildSubject());
		this.setMsgContent(BuildMail.buildMsgContent());
	}
	

	private boolean fillMail(Session session, MimeMessage msg) throws IOException,
			MessagingException {
		ReportUtil.getLogger().info("This Mail From: "+ mailFrom);
		ReportUtil.getLogger().info("This Mail To: "+ mailTo);
		ReportUtil.getLogger().info("This Mail cc To: "+ mailccTo);
		ReportUtil.getLogger().info("This Mail bcc To: "+ mailbccTo);
		//fill from address
		if (mailFrom != null) {
			msg.setFrom(new InternetAddress(mailFrom));
		} else {
			ReportUtil.getLogger().info("Address from is null! ");
			return false;
		}
		if(mailTo == null && mailccTo == null && mailbccTo == null){
			ReportUtil.getLogger().info("There must be at least one address to or cc or bcc! ");
			return false;
		}else {
			//fill to address
			if (mailTo != null) {
				InternetAddress[] address = InternetAddress.parse(mailTo);
				msg.setRecipients(Message.RecipientType.TO, address);
			}
			//fill cc address
			if (mailccTo != null) {
				InternetAddress[] ccaddress = InternetAddress.parse(mailccTo);
				msg.setRecipients(Message.RecipientType.CC, ccaddress);
			}
			//fill bcc address
			if (mailbccTo != null) {
				InternetAddress[] bccaddress = InternetAddress.parse(mailbccTo);
				msg.setRecipients(Message.RecipientType.BCC, bccaddress);
			}
		}
		
		msg.setSubject(subject);
		
		Multipart mPart = new MimeMultipart();
		
//		InternetAddress[] replyAddress = { new InternetAddress(mailFrom) };
//		msg.setReplyTo(replyAddress);
		
		MimeBodyPart mBodyContent = new MimeBodyPart();
		if (msgContent != null)
			mBodyContent.setContent(msgContent, messageContentMimeType);
		else
			mBodyContent.setContent("", messageContentMimeType);
		mPart.addBodyPart(mBodyContent);

		// attach the file to the message
		@SuppressWarnings("rawtypes")
		Enumeration efile = attachedFileList.elements();   
		while (efile.hasMoreElements()) {   
			MimeBodyPart mbpFile = new MimeBodyPart();   
			attachedFilename = efile.nextElement().toString();   
			FileDataSource fds = new FileDataSource(attachedFilename);   
			mbpFile.setDataHandler(new DataHandler(fds));   
			mbpFile.setFileName(fds.getName());
			mPart.addBodyPart(mbpFile);   
		}
		
		attachedFileList.removeAllElements();
		msg.setContent(mPart);
		msg.setSentDate(new Date());
		return true;

	}

	/**
	 * @return 0 is send success, 1 is send failed
	 */
	public int sendMail(){

		Properties props = System.getProperties();
		props.put("mail.smtp.host", smtpHost);
		Session session = Session.getInstance(props, null);
		MimeMessage msg = new MimeMessage(session);
		try {

			boolean fillFlag = fillMail(session, msg);
			if(!fillFlag){
				return 1;
			}
			Transport.send(msg);

		} catch (MessagingException mex) {

			ReportUtil.getLogger().error("send mail failed£º");
			mex.printStackTrace();
			Exception ex = null;
			if ((ex = mex.getNextException()) != null) {
				ReportUtil.getLogger().error(ex.toString());
				ex.printStackTrace();
			}
			return 1;

		} catch (IOException iex) {

			ReportUtil.getLogger().error("send mail failed£º");
			iex.printStackTrace();
			return 1;

		}
		ReportUtil.getLogger().info("send mail success! ");

		return 0;

	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public void setMailbccTo(String mailbccTo) {
		this.mailbccTo = mailbccTo;
	}

	public void setMailccTo(String mailccTo) {
		this.mailccTo = mailccTo;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	
	public void attachfile(String fname) {
		attachedFileList.addElement(fname);
	}

	public static void main(String[] argv) {

		SendMail sm = new SendMail();
		sm.setSmtpHost("smtp3.hp.com");
		sm.setMailFrom("ling-kai.zhang@hp.com");
		sm.setMailTo("yiren.ding@hp.com");
		sm.setMsgContent("Just for Test! ");
		sm.setMailccTo("ling-kai.zhang@hp.com");
		sm.setSubject("Test");
		sm.sendMail();
		
	}

}
