package com.ead.authuser.configs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
	
	@Bean
	public AuthenticationJwtFilter authenticationJwtFilter() {
		return new AuthenticationJwtFilter();
	}
	
	/*@Value("${ead.configServer.username}")
	private String username;
	
	@Value("${ead.configServer.password}")
	private String password;*/
	
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http.
			exceptionHandling().authenticationEntryPoint(authenticationEntryPointImpl).
			and().
			sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
			and().
			authorizeRequests().
			antMatchers(AUTH_WHITELIST).permitAll().
			//antMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN").
			anyRequest().authenticated().
			and().
			csrf().disable()/*.
			formLogin()*/;
		http.addFilterBefore(authenticationJwtFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception{
		return super.authenticationManagerBean();
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
