package com.hp.hpsc.logservice.service;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import com.hp.it.sp4ts.xa.mail.AbstractMailSender;

public class MailSender extends AbstractMailSender {

    public MailSender(String fromAddress, String toAddress, String subject, String bodyTemplateFileName, Locale loc) throws IllegalArgumentException {
        super(fromAddress, toAddress, subject, bodyTemplateFileName, loc);
    }

    public MailSender(String fromAddress, String toAddress, String subject, String body) throws IllegalArgumentException {
        super(fromAddress, toAddress, subject, body);
    }

    public MailSender(List<String> fromAddresses,
                      List<String> toAddresses,
                      String subject,
                      String bodyTemplateFileName,
                      Locale loc) throws IllegalArgumentException {
        super(fromAddresses, toAddresses, subject, bodyTemplateFileName, loc);
    }

    public MailSender(List<String> fromAddresses, List<String> toAddresses, String subject, String body) throws IllegalArgumentException {
        super(fromAddresses, toAddresses, subject, body);
    }

    @Override
    protected String getMessage(String paramString) {
        return null;
    }

    @Override
    protected InputStream getBodyTemplateFileStream(String paramString, Locale paramLocale) {
        return null;
    }

    @Override
    protected InputStream getBodyTemplateFileStream(String paramString) {
        return null;
    }

}
