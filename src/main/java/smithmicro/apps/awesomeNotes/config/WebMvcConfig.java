package smithmicro.apps.awesomeNotes.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {


	@Autowired
	Environment environment;


	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;
	}


	@Bean
	@Description("Thymeleaf template resolver serving HTML 5")
	public ClassLoaderTemplateResolver templateResolver() {

		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

		templateResolver.setPrefix("templates/");
		templateResolver.setCacheable(false);
		templateResolver.setSuffix(".html");        
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setCharacterEncoding("UTF-8");

		return templateResolver;
	}

	@Bean
	@Description("Thymeleaf template engine with Spring integration")
	public SpringTemplateEngine templateEngine() {

		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());

		return templateEngine;
	}

	@Bean
	@Description("Thymeleaf view resolver")
	public ViewResolver viewResolver() {

		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();

		viewResolver.setTemplateEngine(templateEngine());
		viewResolver.setCharacterEncoding("UTF-8");

		return viewResolver;
	}    

	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("login");
	}

	//register resources and set cache
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/")
		.setCachePeriod(3600*24);
		super.addResourceHandlers(registry);
	}

	//configure byteArray to image http converters
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		final ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
		final List<MediaType> list = new ArrayList<>();
		list.add(MediaType.IMAGE_JPEG);
		list.add(MediaType.IMAGE_PNG);
		arrayHttpMessageConverter.setSupportedMediaTypes(list);
		converters.add(arrayHttpMessageConverter);

		super.configureMessageConverters(converters);
	}

}