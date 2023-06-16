package canape.benjamin.runflutterrun;

import canape.benjamin.runflutterrun.config.SecurityConfig;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@Import(SecurityConfig.class)
@ComponentScan(basePackages = {"canape.benjamin.runflutterrun.config", "canape.benjamin.runflutterrun.controller", "canape.benjamin.runflutterrun.security", "canape.benjamin.runflutterrun.service", "canape.benjamin.runflutterrun.repository", "canape.benjamin.runflutterrun.model", "canape.benjamin.runflutterrun.dto"})
public class RunflutterrunApplication {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(RunflutterrunApplication.class, args);
    }

}
