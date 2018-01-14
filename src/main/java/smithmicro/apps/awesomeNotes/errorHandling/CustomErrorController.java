package smithmicro.apps.awesomeNotes.errorHandling;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.java.Log;


@Log
@RestController
@PropertySource("classpath:application.properties")
@Configuration
public class CustomErrorController implements ErrorController{

	private static final String PATH = "/error";
	private static final String CUSTOM_ERROR = "/customError";

	@Value("${custom-error-controller.debug}")
	private boolean debug;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Autowired
	private ErrorAttributes errorAttributes;

	// Override /error mapping
	@RequestMapping(value = PATH)
	ModelAndView error(HttpServletRequest request, HttpServletResponse response){
		// Get error details
		ErrorJson customError = new ErrorJson(response.getStatus(), getErrorAttributes(request, debug));

		// Push error object to custom error view.
		ModelMap model = new ModelMap();
		model.addAttribute("customError", customError);

		return new ModelAndView(CUSTOM_ERROR, model);
	}

	@Override
	public String getErrorPath(){
		return PATH;
	}

	private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
	}

}
