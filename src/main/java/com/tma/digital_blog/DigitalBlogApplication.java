package com.tma.digital_blog;

import com.tma.digital_blog.model.Article;
import com.tma.digital_blog.model.User;
import com.tma.digital_blog.model.enumType.Role;
import com.tma.digital_blog.repository.ArticleRepository;
import com.tma.digital_blog.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication
public class DigitalBlogApplication {

    public static void main(final String[] args) {
        SpringApplication.run(DigitalBlogApplication.class, args);
    }
    @Bean
    public CommandLineRunner loadData(
            UserRepository userRepository,
            ArticleRepository articleRepository
    ) {
        return (args) -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (userRepository.findUserByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setRole(Role.SYS_ADMIN);
                admin.setPassword(encoder.encode("admin"));
                userRepository.save(admin);
            }
            for (int i = 1; i <= 20; i++) {
                if (userRepository.findUserByUsername("Han" + i).isEmpty()) {
                    User u = new User();
                    u.setUsername("Han" + i);
                    u.setRole(Role.CUSTOMER_USER);
                    u.setPassword(encoder.encode("Han" + i));
                    userRepository.save(u);
                }
            }
            for (int i = 1; i <= 20; i++) {
                User u = userRepository.findUserByUsername("Han"+i).orElseThrow();
                Article a = new Article();
                a.setTitle("News_"+i);
                a.setContent("Content_"+i);
                a.setCreatedBy(u);
                articleRepository.save(a);
            }
        };
    }
}
