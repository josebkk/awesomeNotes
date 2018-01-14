package smithmicro.apps.awesomeNotes.service;

import org.springframework.security.core.Authentication;

import smithmicro.apps.awesomeNotes.dto.FacebookDTO;
import smithmicro.apps.awesomeNotes.model.User;

public interface FacebookService {

	void singInWithFaebook(FacebookDTO user) throws Exception;
	Authentication loginWithFacebook(User fbUser);
	User createFacebookUser(FacebookDTO user, User fbUser) throws Exception;
	void removeAuthentication(Authentication authentication);
	
}
