package xyz.nyist.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import xyz.nyist.event.MailEvent;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @Author : fucong
 * @Date: 2021-03-30 13:47
 * @Description :
 */
@Slf4j
@Component
public class MailListener implements ApplicationListener<MailEvent> {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String form;

    @Override
    public void onApplicationEvent(MailEvent event) {
        log.info("收到消息: " + event);
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(form);
            helper.setTo(event.getMail());
            helper.setSubject(event.getSubject());
            helper.setText(event.getContent(), event.isHtml());
            mailSender.send(message);
            log.info("邮件发送成功");
        } catch (MessagingException e) {
            log.error("邮件发送失败");
            e.printStackTrace();
        }
    }
}
