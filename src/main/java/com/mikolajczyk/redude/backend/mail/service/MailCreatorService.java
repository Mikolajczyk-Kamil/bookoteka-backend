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

    public String buildWelcomeEmail(String name) {
        return templateEngine.process("mail/welcome_template.html", prepareContext(name));
    }

    public String buildWeekEmail(String name) {
        return templateEngine.process("mail/week_template.html", prepareContext(name));
    }

    public String buildDeleteAccountEmail(String name) {
        return templateEngine.process("mail/delete_account_template.html", prepareContext(name));
    }

    private Context prepareContext(String name) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("email", adminConfig.getAdminMail());
        return context;
    }
}
