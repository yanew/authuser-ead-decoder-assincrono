package com.ead.authuser.clients;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.ead.authuser.dtos.CourseDto;
import com.ead.authuser.dtos.ResponsePageDto;
import com.ead.authuser.services.UtilsService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class CourseClient {

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	UtilsService utilsService;
	
	@Value("${ead.api.url.course}")
	String REQUEST_URL_COURSE;
	
	//@Retry(name = "retryInstance", fallbackMethod = "retryFallback")
	@CircuitBreaker(name = "circuitbreakerInstance"/*, fallbackMethod = "circuitBreakerFallback"*/)
	public Page<CourseDto> getAllCoursesByUser(UUID userId, Pageable pageable, String token){
		List<CourseDto> searchResult = null;
		String url = REQUEST_URL_COURSE + utilsService.createUrlGetAllCoursesByUser(userId, pageable);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		HttpEntity<String> requestEntity = new HttpEntity<String>("parameters", headers);
		
		log.debug("Request URL: {}", url);
		log.info("Request URL: {}", url);
		
		/*System.out.println("VAI CHAMAR COURSE");
		try {*/
			ParameterizedTypeReference<ResponsePageDto<CourseDto>> responseType = new ParameterizedTypeReference<ResponsePageDto<CourseDto>>() {}; 
			ResponseEntity<ResponsePageDto<CourseDto>> result = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType);
			searchResult = result.getBody().getContent();
			log.debug("Response number of elements: {}", searchResult.size());
		/*}catch(HttpStatusCodeException e ) {
			log.error("Error request /courses {}", e);
		}*/
		log.info("Ending request /courses userId {}", userId);
		return new PageImpl<>(searchResult);
	}
	
	public Page<CourseDto> circuitBreakerFallback(UUID userId, Pageable pageable, Throwable t) {
		log.error("Retentativa interna circuitBreakerFallback, cause - {}", t.toString());
		return new PageImpl<>(new ArrayList<CourseDto>());
	}
	
	public Page<CourseDto> retryFallback(UUID userId, Pageable pageable, Throwable t) {
		log.error("Retentativa interna retryFallback, cause - {}", t.toString());
		return new PageImpl<>(new ArrayList<CourseDto>());
	}
	
}
