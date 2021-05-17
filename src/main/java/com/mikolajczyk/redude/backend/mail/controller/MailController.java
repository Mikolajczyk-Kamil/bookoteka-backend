package com.mikolajczyk.redude.backend.mail.controller;

import com.mikolajczyk.redude.backend.mail.domain.Mail;
import com.mikolajczyk.redude.backend.mail.service.MailService;
import com.mikolajczyk.redude.backend.mail.service.MailCreatorService;
import com.mikolajczyk.redude.backend.mail.type.MailType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;
    private final MailCreatorService mailCreatorService;

    public void createAndSend(String name, String mailTo, String subject, MailType mailType) {
        if (mailType.equals(MailType.WELCOME)) {
            mailService.send(Mail.builder()
                    .mailTo(mailTo)
                    .subject(subject)
                    .message(mailCreatorService.buildWelcomeEmail(name))
                    .build()
            );
        } else if (mailType.equals(MailType.WEEK)) {
            mailService.send(Mail.builder()
                    .mailTo(mailTo)
                    .subject(subject)
                    .message(mailCreatorService.buildWeekEmail(name))
                    .build()
            );
        } else if (mailType.equals(MailType.DELETE_ACCOUNT)) {
            mailService.send(Mail.builder()
                    .mailTo(mailTo)
                    .subject(subject)
                    .message(mailCreatorService.buildDeleteAccountEmail(name))
                    .build()
            );
        }
    }
}
