package com.ead.authuser.configs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled=true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
	@Autowired
	private AuthenticationEntryPointImpl authenticationEntryPointImpl;
	
	private static final String[] AUTH_WHITELIST = {
		"/auth/**"	
	};
	
	/*@Value("${ead.configServer.username}")
	private String username;
	
	@Value("${ead.configServer.password}")
	private String password;*/
	
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http.
			httpBasic().
			authenticationEntryPoint(authenticationEntryPointImpl).
			and().
			authorizeRequests().
			antMatchers(AUTH_WHITELIST).permitAll().
			antMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN").
			anyRequest().authenticated().
			and().
			csrf().disable().
			formLogin();
	}
	
	/*@Override - Autenticacao em memoria
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.inMemoryAuthentication().
		withUser("admin").
		password(passwordEncoder().encode("123456")).
		roles("ADMIN");
	}*/
	
	//Autenticacao usando banco de dados
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(userDetailsServiceImpl).
			passwordEncoder(passwordEncoder());
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
