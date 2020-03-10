package com.springvuegradle.seng302team600;

import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.*;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
//
//    @Bean
//    CommandLineRunner init(UserRepository repository) {
//        return args -> {
//            repository.save(new User(User.userBuilder()
//                    .withFirstName("Jerry")
//                    .withLastName("Ryan")
//                    .withPrimaryEmail("dsadsda")
//                    .withPassword("DWDA")
//                    .withDateOfBirth("12/02/2000")
//                    .withGender(User.Gender.FEMALE)));
//            repository.save(new User(User.userBuilder()
//                    .withFirstName("Maurice")
//                    .withLastName("Benson")
//                    .withPrimaryEmail("jack@google.com")
//                    .withPassword("jacky'sSecuredPwd")
//                    .withBio("Jacky loves to ride his bike on crazy mountains.")
//                    .withDateOfBirth("20/12/1985")
//                    .withGender(User.Gender.MALE)));
//
//            //Should print 4 lines of dsadsda and 1 line of jack@google.com
//            User user = repository.findByPrimaryEmail("dsadsda");
//            System.out.println(user.toString());
//            user = repository.findByFirstName("Jerry");
//            System.out.println(user);
//            System.out.println(repository.findById("dsadsda").get());
//            repository.findAll().forEach(System.out::println);
//        };
//    }
//  --------- Need to understand what this is doing and if we need it ----------
    // Fix the CORS errors
    @Bean
    public FilterRegistrationBean simpleCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // *** URL below needs to match the Vue client URL and port ***
        config.setAllowedOrigins(new ArrayList(Arrays.asList("http://localhost:9000", "http://localhost:9500", "https://csse-s302g0.canterbury.ac.nz/test", "https://csse-s302g0.canterbury.ac.nz/prod")));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

}
