package com.springvuegradle.seng302example;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import java.util.*;
import java.util.stream.Stream;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
public class Seng302ExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(Seng302ExampleApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StudentRepository repository) {
		return args -> {
			Stream.of("Campbell Mercer", "Matthew Minnish", "Dr Moffat", "Fabien Gilson", "I Am Brose").forEach(name -> {
				Student student = new Student(name);
				repository.save(student);
			});
			repository.findAll().forEach(System.out::println);
		};
	}

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