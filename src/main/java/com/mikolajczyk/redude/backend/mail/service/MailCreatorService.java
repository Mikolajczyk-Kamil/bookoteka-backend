package com.mikolajczyk.redude.backend.mail.service;

import com.mikolajczyk.redude.backend.config.AdminConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailCreatorService {

    private final AdminConfig adminConfig;

    @Qualifier("templateEngine")
    private final TemplateEngine templateEngine;

    public String buildWelcomeEmail(String message, String name) {
        return templateEngine.process("mail/welcome_template.html", prepareContext(message, name));
    }

    public String buildDeleteAccountEmail(String message, String name) {
        return templateEngine.process("mail/delete_account_template.html", prepareContext(message, name));
    }

    private Context prepareContext(String message, String name) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("email", adminConfig.getAdminMail());
        context.setVariable("message", message);
        return context;
    }
}
