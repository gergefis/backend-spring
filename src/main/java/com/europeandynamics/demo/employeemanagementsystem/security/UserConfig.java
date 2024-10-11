package com.europeandynamics.demo.employeemanagementsystem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserConfig {

	@Bean
	public InMemoryUserDetailsManager userDetailsManager () {

		UserDetails dimitris = User.builder()
				.username("dimitris")
				.password("{noop}100200300")
				.roles("ADMIN") //EMPLOYEE, MANAGER
				.build();

		return new InMemoryUserDetailsManager(dimitris);
	}


}
