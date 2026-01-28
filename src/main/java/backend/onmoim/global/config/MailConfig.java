package backend.onmoim.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final MailProperties mailProperties;

    @Bean
    public List<JavaMailSender> javaMailSenders() {
        return mailProperties.accounts().stream()
                .map(account -> {
                    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
                    mailSender.setHost(mailProperties.host());
                    mailSender.setPort(mailProperties.port());
                    mailSender.setUsername(account.username());
                    mailSender.setPassword(account.password());

                    Properties props = new Properties();
                    mailProperties.properties().forEach(props::setProperty);
                    mailSender.setJavaMailProperties(props);

                    return mailSender;
                })
                .collect(Collectors.toList());
    }
}