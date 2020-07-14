package com.backend.sprint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class SprintApplication {

	public static void main(String[] args) {
		SpringApplication.run(SprintApplication.class, args);
	}

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
}
