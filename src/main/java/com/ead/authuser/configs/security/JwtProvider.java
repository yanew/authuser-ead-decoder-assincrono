package com.ead.authuser.configs.security;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class JwtProvider {

	@Value("${ead.auth.jwtSecret}")
	private String jwtSecret;
	
	@Value("${ead.auth.jwtExpirationMs}")
	private String jwtExpirationMs;
	
	
	public String generateJwt(Authentication authentication) {
		UserDetails userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		
		long hojeMs = (new Date()).getTime();
		Date expiracao = new Date(hojeMs + Long.valueOf(jwtExpirationMs));
		//Da pra fazer com DateFormat tb, creio eu.
		
		return Jwts.builder().
				setSubject(userPrincipal.getUsername()).
				setIssuedAt(new Date()).
				setExpiration(expiracao).
				signWith(SignatureAlgorithm.HS512, jwtSecret).
				compact();
	}
	
	/*public static void main(String[] args) {
		long hojeMs = (new Date()).getTime();
		Date dt = new Date (hojeMs+14400000);
        System.out.println (dt);
        DateFormat df = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss.SSS");
        df.setTimeZone (TimeZone.getTimeZone ("GMT"));
        System.out.println (df.format (dt));
	}*/
	
}
