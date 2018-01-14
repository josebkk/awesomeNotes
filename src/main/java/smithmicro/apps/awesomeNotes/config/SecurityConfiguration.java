package smithmicro.apps.awesomeNotes.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;

import smithmicro.apps.awesomeNotes.interceptor.CustomConnectInterceptor;
import smithmicro.apps.awesomeNotes.service.FacebookService;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private Environment environment;

	@Autowired
	private FacebookService facebookService;

	@Value("${spring.queries.users-query}")
	private String usersQuery;

	@Value("${spring.queries.roles-query}")
	private String rolesQuery;

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.
		jdbcAuthentication()
		.usersByUsernameQuery(usersQuery)
		.authoritiesByUsernameQuery(rolesQuery)
		.dataSource(dataSource)
		.passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.
		authorizeRequests()
		.antMatchers("/css/**", "/js/**", "/webjars/**").permitAll()
		.antMatchers("/login", "/registration", "/resetPasswordRequest", "/resetPassword", "/connect/*").permitAll()
		.antMatchers("/").hasAnyAuthority("USER").anyRequest()
		.authenticated().and().csrf().disable().formLogin()
		.loginPage("/login").failureUrl("/login?error=true")
		.defaultSuccessUrl("/")
		.usernameParameter("email")
		.passwordParameter("password")
		.and().logout()
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		.logoutSuccessUrl("/login").and().exceptionHandling()
		.accessDeniedPage("/customError");
		
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web
		.ignoring()
		.antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
	}



	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication();
	}


	@Bean
	public ConnectController connectController() {
		ConnectController controller = new ConnectController(connectionFactoryLocator(), connectionRepository());
		controller.addInterceptor(new CustomConnectInterceptor(facebookService));
		return controller;
	}

	@Bean
	public ConnectionFactoryLocator connectionFactoryLocator() {
		ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
		registry.addConnectionFactory(new FacebookConnectionFactory(
				environment.getProperty("facebook.clientId"),
				environment.getProperty("facebook.clientSecret")));
		return registry;
	}

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	public ConnectionRepository connectionRepository() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new IllegalStateException("Unable to get a ConnectionRepository: no user signed in");
		}
		return usersConnectionRepository().createConnectionRepository(authentication.getName());
	}


	@Bean
	public UsersConnectionRepository usersConnectionRepository() {
		return new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator(), textEncryptor());
	}

	@Bean
	public TextEncryptor textEncryptor() {
		String localEncryptionPassword = environment.getProperty("encryption.password");
		String localEncryptionSalt = environment.getProperty("encryption.hexSalt");
		return Encryptors.delux(localEncryptionPassword, localEncryptionSalt);
	}

}