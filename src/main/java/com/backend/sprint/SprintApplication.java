package com.backend.sprint;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@SpringBootApplication
@EnableAutoConfiguration
public class SprintApplication {

	public static void main(String[] args) {
		SpringApplication.run(SprintApplication.class, args);
	}

	// @Bean
	// public WebMvcConfigurer corsConfigurer() {
	// return new WebMvcConfigurer() {
	// @Override
	// public void addCorsMappings(CorsRegistry registry) {
	// registry.addMapping("/**").allowedOrigins("*");
	// }
	// };
	// }

	// @Bean
	// public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
	// UrlBasedCorsConfigurationSource source = new
	// UrlBasedCorsConfigurationSource();
	// CorsConfiguration config = new CorsConfiguration();
	// config.setAllowCredentials(false);
	// config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
	// config.setAllowedMethods(Collections.singletonList("*"));
	// config.setAllowedHeaders(Collections.singletonList("*"));
	// source.registerCorsConfiguration("/**", config);
	// FilterRegistrationBean<CorsFilter> bean = new
	// FilterRegistrationBean<>(new CorsFilter(source));
	// bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
	// return bean;
	// }

	// @Bean
	// public CorsFilter corsConfigurationSource() {
	//
	// CorsConfiguration corsConfiguration = new CorsConfiguration();
	// corsConfiguration.applyPermitDefaultValues();
	//
	// corsConfiguration.addAllowedOrigin("*");
	// corsConfiguration.addAllowedHeader("*");
	// corsConfiguration.addAllowedMethod(HttpMethod.OPTIONS);
	// corsConfiguration.addAllowedMethod(HttpMethod.HEAD);
	// corsConfiguration.addAllowedMethod(HttpMethod.GET);
	// corsConfiguration.addAllowedMethod(HttpMethod.PUT);
	// corsConfiguration.addAllowedMethod(HttpMethod.POST);
	// corsConfiguration.addAllowedMethod(HttpMethod.DELETE);
	// corsConfiguration.addAllowedMethod(HttpMethod.PATCH);
	//
	// UrlBasedCorsConfigurationSource source = new
	// UrlBasedCorsConfigurationSource();
	// source.registerCorsConfiguration("/**", corsConfiguration);
	//
	// return new CorsFilter(source);
	// }

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration
				.setAllowedOrigins(Arrays.asList("https://club-sprint-front.herokuapp.com", "http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Headers", "Access-Control-Allow-Origin",
				"Access-Control-Request-Method", "Access-Control-Request-Headers", "Origin", "Cache-Control",
				"Content-Type"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
