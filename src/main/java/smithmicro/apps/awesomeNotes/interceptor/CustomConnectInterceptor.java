package smithmicro.apps.awesomeNotes.interceptor;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

import smithmicro.apps.awesomeNotes.dto.FacebookDTO;
import smithmicro.apps.awesomeNotes.service.FacebookService;


public class CustomConnectInterceptor implements ConnectInterceptor<Facebook> {


	private FacebookService facebookService;

	public CustomConnectInterceptor(FacebookService userTaskService) {
		this.facebookService = userTaskService;
	}

	@Override
	public void preConnect(ConnectionFactory<Facebook> connectionFactory, MultiValueMap<String, String> valueMap, WebRequest request) {

	}

	@Override
	public void postConnect(Connection<Facebook> connection, WebRequest request) {
		Facebook facebook = connection.getApi();
		String [] fields = { "id", "email"};

		//fetch id and email fields to facebookDTO object
		FacebookDTO userProfile = facebook.fetchObject("me", FacebookDTO.class, fields);

		// 
		FacebookDTO user = new FacebookDTO();
		user.setEmail(userProfile.getEmail());
		//extract facebook Id from profile URL
		user.setId(extractId(connection.getProfileUrl()));
		// get facebook image URL
		user.setImageURL(connection.getImageUrl());
		user.setDisplayName(connection.getDisplayName());
		user.setEmail(user.getEmail());

		try {
			facebookService.singInWithFaebook(user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * Retrieving the ID sometimes yields null values through the connection
	 * However this data is simply included in both the image and the profile url
	 * <br /><br />
	 * You could leave this as such, or change it to something else you prefer. 
	 * Only rule is that it has to be something persistent.
	 * 
	 * @param profileUrl
	 * @return Facebook ID extracted from profile url as a {@link String}
	 */
	private String extractId(String profileUrl) {
		profileUrl = profileUrl.replace("https://www.facebook.com/app_scoped_user_id/", "");
		profileUrl = profileUrl.replace("/", "");
		return profileUrl;
	}

}