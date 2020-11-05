package com.backend.sprint;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String[] SWAGGER_URL = { "/herokuapp**", "/localhost**" };
	private static final String LOGIN_URL = "/login/**";
	private static final String USER_URL = "/users";

	// @Autowired
	// UserDetailsServiceImpl userDetailsService;
	//
	// @Autowired
	// BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity// .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.cors().and().csrf().disable();
		// .authorizeRequests()// .antMatchers(SWAGGER_URL).permitAll()
		// .antMatchers(HttpMethod.OPTIONS, "**").permitAll();// allow CORS
		// option
		// calls

		// httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		// .authorizeRequests().antMatchers(LOGIN_URL).permitAll()
		// .antMatchers(HttpMethod.POST, USER_URL).permitAll()
		// .antMatchers(SWAGGER_URL).permitAll().anyRequest()
		// .authenticated().and()
		// .addFilter(new JWTAuthenticationFilter(authenticationManager()))
		// .addFilter(new JWTAuthorizationFilter(authenticationManager()));
	}

	// @Override
	// protected void configure(AuthenticationManagerBuilder auth) throws
	// Exception {
	// auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	// }
	//
	// @Override
	// public AuthenticationManager authenticationManagerBean() throws Exception
	// {
	// return super.authenticationManagerBean();
	// }
	//
	// @Bean
	// public BCryptPasswordEncoder getEncoder() {
	// return new BCryptPasswordEncoder();
	// }
	//

	@Bean
	public FilterRegistrationBean corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
		bean.setOrder(0);
		return bean;
	}
}
