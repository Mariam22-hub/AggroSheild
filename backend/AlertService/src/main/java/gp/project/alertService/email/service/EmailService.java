package gp.project.alertService.email.service;

import gp.project.alertService.email.EmailTemplateName;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailServiceInterface {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendAlertEmail(String to, String fullName, String vehicleNumber,
                               EmailTemplateName emailTemplate) throws MessagingException {
        String templateName = emailTemplate.name();


        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MULTIPART_MODE_MIXED,
                UTF_8.name()
        );
        Map<String, Object> properties = new HashMap<>();

        properties.put("fullName", fullName);
        properties.put("vehicleNumber", vehicleNumber);

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom("alerts@aggroshield.com");
        helper.setTo(to);
        helper.setSubject("Aggressive Driving Alert");

        String template = templateEngine.process(templateName, context);
        helper.setText(template, true);

        mailSender.send(mimeMessage);
    }
}