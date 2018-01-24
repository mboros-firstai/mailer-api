package com.github.rlewan.mailer.services.emailserviceproviders;

import com.github.rlewan.mailer.services.ProviderResponseVerifier;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Qualifier("primaryEmailServiceProvider")
public class SendgridEmailServiceProvider implements EmailServiceProvider {

    private final ProviderResponseVerifier providerResponseVerifier;

    public SendgridEmailServiceProvider(ProviderResponseVerifier providerResponseVerifier) {
        this.providerResponseVerifier = providerResponseVerifier;
    }

    @Override
    public void sendEmail(String sender, String recipient, String subject, String text) {
        Email from = new Email(sender);
        Email to = new Email(recipient);
        Content content = new Content("text/plain", text);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            providerResponseVerifier.assertResponseIsSuccessful(response.getStatusCode());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
