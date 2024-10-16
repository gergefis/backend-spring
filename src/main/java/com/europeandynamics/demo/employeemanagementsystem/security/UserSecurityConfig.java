package com.europeandynamics.demo.employeemanagementsystem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class UserSecurityConfig {

//	add support for JDBC
	@Bean
	public UserDetailsManager userDetailsManager(DataSource dataSource) {

		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
		jdbcUserDetailsManager.setUsersByUsernameQuery("select username, password, active from members where username=?");
		jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select username, role from roles where username=?");
		return jdbcUserDetailsManager;
	}

@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(configurer ->
				configurer.
//						requestMatchers(HttpMethod.GET, "/").hasRole("USER") //TODO to Sign Up page
						requestMatchers(HttpMethod.GET, "/api/user/getUsers").hasRole("MANAGER")
						.requestMatchers(HttpMethod.GET, "/api/user/{id}").hasRole("MANAGER")
						.requestMatchers(HttpMethod.PUT, "/api/user/**").hasRole("MANAGER") //updateUser
						.requestMatchers(HttpMethod.POST, "/api/user/registerUser").hasRole("MANAGER")
						.requestMatchers(HttpMethod.DELETE, "/api/user/deleteUser/{id}").hasRole("ADMIN")
		);
//		Use HTTP Basic authentication
	http.httpBasic(Customizer.withDefaults());

//	Disable Cross Site Request Forgery (CSRF), but now not required because of stateless REST APIs (POST,PUT, DELETE and/or PATCH)
	http.csrf(csrf -> csrf.disable());
		return http.build();
	}

}
