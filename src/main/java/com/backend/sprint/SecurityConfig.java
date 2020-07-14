package com.backend.sprint;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}

	// @Override
	// protected void configure(HttpSecurity httpSecurity) throws Exception {
	// httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().cors().and()
	// .csrf().disable().authorizeRequests().antMatchers("*").permitAll();
	// }

	// @Bean
	// public CorsConfigurationSource corsConfigurationSource() {
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
	// return source;
	// }
}
