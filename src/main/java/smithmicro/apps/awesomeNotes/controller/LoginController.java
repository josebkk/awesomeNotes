package smithmicro.apps.awesomeNotes.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import smithmicro.apps.awesomeNotes.model.Role;
import smithmicro.apps.awesomeNotes.model.User;
import smithmicro.apps.awesomeNotes.service.EmailService;
import smithmicro.apps.awesomeNotes.service.UserService;

@RestController
public class LoginController {

	private static final String REGISTRATION_VIEW = "registration";
	private static final String LOGIN_VIEW = "login";
	private static final String REQUEST_RESET_PASSWORD_VIEW = "resetPasswordRequest";
	private static final String RESET_PASSWORD_VIEW = "resetPassword";


	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	// Bind byteArray class with ByteArrayMultiPartFileEditor class for images
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor()); 
	}

	//Render login view
	@GetMapping(value={"/login"})
	public ModelAndView login(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(LOGIN_VIEW);
		return modelAndView;
	}

	//Render request reset password view
	@GetMapping("/resetPasswordRequest")
	public ModelAndView requestResetPassword(){
		ModelAndView modelAndView = new ModelAndView();
		
		// Push user object to UI model.
		User user = new User();
		modelAndView.addObject("user", user);
		
		modelAndView.setViewName(REQUEST_RESET_PASSWORD_VIEW);
		return modelAndView;
	}

	//Process request reset password
	@PostMapping("/resetPasswordRequest")
	public ModelAndView processRequestResetPassword(@Valid User user, BindingResult bindingResult, ModelAndView modelAndView, HttpServletRequest request){
		
		//get user inserted e-mail
		String userEmail = user.getEmail();

		// Lookup user in database by e-mail
		User userLookup = userService.findUserByEmail(userEmail);

		if (userLookup == null) {
			// user for inserted e-mail does not exists on application
			bindingResult
			.rejectValue("email", "error.user",
					"No user is registered with the provided e-mail.");
		} else {
			// user exists on application
			
			// Generate random 36-character string token for reset password 
			userLookup.setResetToken(UUID.randomUUID().toString());

			// Save token to database
			userService.saveUser(userLookup);

			//Construct URL to send on request reset password e-mail notification
			String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort();

			// Send email notification
			emailService.sendSimpleMessage(userEmail, "Password Reset Request", "To reset your password, click the link below:\n" + appUrl
					+ "/resetPassword?token=" + userLookup.getResetToken());


			// Add success message to view
			modelAndView.addObject("successMessage", "A password reset link has been sent to " + userEmail);
		}

		modelAndView.setViewName(REQUEST_RESET_PASSWORD_VIEW);
		return modelAndView;
	}


	//Render reset password view
	@GetMapping("/resetPassword")
	public ModelAndView resetPasswordForm(ModelAndView modelAndView, @RequestParam("token") String token){

		//Lookup for user by generated reset token
		User user = userService.findUserByResetToken(token);

		if (user == null) { // Token not found in DB
			user = new User();
			modelAndView.addObject("errorMessage", "Oops!  This is an invalid password reset link.");
		} 

		modelAndView.addObject("user", user);

		modelAndView.setViewName(RESET_PASSWORD_VIEW);
		return modelAndView;
	}


	//Process reset password
	@PostMapping(value = "/resetPassword")
	public ModelAndView setNewPassword(ModelAndView modelAndView, @Valid User user, BindingResult bindingResult, RedirectAttributes redir) {

		// Find the user associated with the reset token
		User tokenUser = userService.findUserByResetToken(user.getResetToken());

		// This should always be non-null (validated on get request), but double check anyway
		if (tokenUser != null) { // Token found in DB

			// Set new password    
			tokenUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

			// Set the reset token to null so it cannot be used again
			tokenUser.setResetToken(null);

			// Save user
			userService.saveUser(tokenUser);

			// In order to set a model attribute on a redirect, we must use
			// RedirectAttributes
			redir.addFlashAttribute("successMessage", "You have successfully reset your password.  You may now login.");

			modelAndView.setViewName("redirect:/"+LOGIN_VIEW);

		} else { // Token not found - invalid reset password link
			modelAndView.addObject("errorMessage", "Oops!  This is an invalid password reset link.");
			modelAndView.setViewName(RESET_PASSWORD_VIEW);	
		}

		return modelAndView;
	}



	// Reset page without a token redirects to login page
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ModelAndView handleMissingParams(MissingServletRequestParameterException ex) {
		return new ModelAndView("redirect:/"+LOGIN_VIEW);
	}


	//Render registration view
	@GetMapping("/registration")
	public ModelAndView registration(){
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName(REGISTRATION_VIEW);
		return modelAndView;
	}


	//Process registration
	@PostMapping("/registration")
	public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult, @RequestParam("avatar") MultipartFile avatar) throws IOException {

		ModelAndView modelAndView = new ModelAndView();
		//Lookup user by user inserted e-mail
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			// user already registered on application
			bindingResult
			.rejectValue("email", "error.user",
					"There is already an user registered with the email provided");

		}else if (bindingResult.hasErrors()) {
			//Do nothing (UI binding validations)
		} else {
			if (!avatar.isEmpty()){
				// if present, set avatar image
				user.setAvatar(avatar.getBytes());
			}else{
				// if no avatar was set, force avatar to be null on User object
				user.setAvatar(null);
			}
			
			//set user password with encription.
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			//set default role (USER)
			Role userRole = userService.findRoleByName("USER");
			user.setRoles(Arrays.asList(userRole));
			//create user
			userService.saveUser(user);
			modelAndView.addObject("successMessage", "User has been registered successfully");
		}

		modelAndView.setViewName(REGISTRATION_VIEW);	
		return modelAndView;
	}

}
