package com.ead.authuser.configs.security;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class JwtProvider {

	@Value("${ead.auth.jwtSecret}")
	private String jwtSecret;
	
	@Value("${ead.auth.jwtExpirationMs}")
	private String jwtExpirationMs;
	
	
	public String generateJwt(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		
		final String roles = userPrincipal.getAuthorities().stream().
				map(role -> {return role.getAuthority();}).collect(Collectors.joining(","));
		
		long hojeMs = (new Date()).getTime();
		Date expiracao = new Date(hojeMs + Long.valueOf(jwtExpirationMs));
		//Da pra fazer com DateFormat tb, creio eu.
		
		return Jwts.builder().
				setSubject(userPrincipal.getUserId().toString()).
				claim("roles", roles).
				setIssuedAt(new Date()).
				setExpiration(expiracao).
				signWith(SignatureAlgorithm.HS512, jwtSecret).
				compact();
	}
	
	public String getSubjectJwt(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}
	
	public boolean validateJwt(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		}catch (SignatureException e) {
			log.error("JWT signature inválida: {}", e.getMessage());
		}catch (MalformedJwtException e) {
			log.error("Token JWT inválido: {}", e.getMessage());
		}catch (ExpiredJwtException e) {
			log.error("Token JWT está expirado: {}", e.getMessage());
		}catch (UnsupportedJwtException e) {
			log.error("Token JWT não é suportado: {}", e.getMessage());
		}catch (IllegalArgumentException e) {
			log.error("JWT claims string está vazia: {}", e.getMessage());
		}
		return false;
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
