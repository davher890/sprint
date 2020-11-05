//package com.backend.sprint;
//
//import java.util.Arrays;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//	private static final String[] SWAGGER_URL = { "/herokuapp**", "/localhost**" };
//	private static final String LOGIN_URL = "/login/**";
//	private static final String USER_URL = "/users";
//
//	// @Autowired
//	// UserDetailsServiceImpl userDetailsService;
//	//
//	// @Autowired
//	// BCryptPasswordEncoder bCryptPasswordEncoder;
//
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http// .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//				.cors(c -> {
//					CorsConfigurationSource source = request -> {
//						CorsConfiguration config = new CorsConfiguration();
//						config.setAllowedOrigins(Arrays.asList(new String[] { CorsConfiguration.ALL }));
//						config.setAllowedMethods(Arrays.asList(new String[] { "GET", "POST", "PUT", "DELETE" }));
//						return config;
//					};
//					c.configurationSource(source);
//				});
//		http.csrf().disable();
//
//		http.authorizeRequests().anyRequest().permitAll();
//
//		// .authorizeRequests()// .antMatchers(SWAGGER_URL).permitAll()
//		// .antMatchers(HttpMethod.OPTIONS, "**").permitAll();// allow CORS
//		// option
//		// calls
//
//		// httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//		// .authorizeRequests().antMatchers(LOGIN_URL).permitAll()
//		// .antMatchers(HttpMethod.POST, USER_URL).permitAll()
//		// .antMatchers(SWAGGER_URL).permitAll().anyRequest()
//		// .authenticated().and()
//		// .addFilter(new JWTAuthenticationFilter(authenticationManager()))
//		// .addFilter(new JWTAuthorizationFilter(authenticationManager()));
//	}
//
//	// @Override
//	// protected void configure(AuthenticationManagerBuilder auth) throws
//	// Exception {
//	// auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
//	// }
//	//
//	// @Override
//	// public AuthenticationManager authenticationManagerBean() throws Exception
//	// {
//	// return super.authenticationManagerBean();
//	// }
//	//
//	// @Bean
//	// public BCryptPasswordEncoder getEncoder() {
//	// return new BCryptPasswordEncoder();
//	// }
//	//
//
//}
package com.backend.sprint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String[] SWAGGER_URL = { "/herokuapp**", "/localhost**" };

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().cors().and()
				.csrf().disable().authorizeRequests().antMatchers(SWAGGER_URL).permitAll();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.applyPermitDefaultValues();
		corsConfiguration.addAllowedMethod(HttpMethod.PUT);
		corsConfiguration.addAllowedMethod(HttpMethod.DELETE);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);

		return source;
	}
}