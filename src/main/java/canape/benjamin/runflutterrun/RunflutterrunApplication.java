package canape.benjamin.runflutterrun;

import canape.benjamin.runflutterrun.security.SecurityConfig;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@Import(SecurityConfig.class)
@ComponentScan(basePackages = {"canape.benjamin.runflutterrun.controllers", "canape.benjamin.runflutterrun.security", "canape.benjamin.runflutterrun.services", "canape.benjamin.runflutterrun.repositories", "canape.benjamin.runflutterrun.model", "canape.benjamin.runflutterrun.dto"})
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
