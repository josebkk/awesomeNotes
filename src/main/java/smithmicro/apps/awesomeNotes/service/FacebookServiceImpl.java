package smithmicro.apps.awesomeNotes.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Service;

import smithmicro.apps.awesomeNotes.helper.SocialUserDetails;
import smithmicro.apps.awesomeNotes.dto.FacebookDTO;
import smithmicro.apps.awesomeNotes.model.Role;
import smithmicro.apps.awesomeNotes.model.User;


@Service
public class FacebookServiceImpl implements FacebookService {

	@Autowired
	private UserService userService;

	@Autowired
	private ConnectionRepository connectionRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;



	@Override
	public void singInWithFaebook(FacebookDTO user) throws Exception {
		User fbUser = userService.findByFacebookId(String.valueOf(user.getId()));

		// if user not found on application by facebook id, create new user with facebook data.
		if(null == fbUser){
			fbUser = createFacebookUser(user, fbUser);
		}

		//login with facebook.
		Authentication auth = loginWithFacebook(fbUser);

		//user authenticated, so not necessary anymore
		removeAuthentication(auth);
	}



	@Override
	public Authentication loginWithFacebook(User fbUser){
		SocialUserDetails userDetails = new SocialUserDetails(fbUser.getId(), Arrays.asList(new SimpleGrantedAuthority("USER")));
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		return authentication;
	}



	@Override
	public User createFacebookUser(FacebookDTO user, User fbUser) throws Exception{
		fbUser = new User();

		fbUser.setFacebookId(user.getId());
		fbUser.setEmail(user.getEmail());
		// image from URL and transform to byte array
		fbUser.setAvatar(getImageFromUrl(user.getImageURL()));
		// set First and Last name by splitting by whitespace
		fbUser.setName(user.getDisplayName().split(" ")[0]);
		fbUser.setLastName(user.getDisplayName().split(" ")[1]);
		// set password with encription
		fbUser.setPassword(bCryptPasswordEncoder.encode(UUID.randomUUID().toString()));
		// set facebook associated email
		fbUser.setEmail(user.getEmail());
		//set default role (USER)
		Role userRole = userService.findRoleByName("USER");
		fbUser.setRoles(Arrays.asList(userRole));
		
		//create user with facebook data.
		fbUser = userService.saveUser(fbUser);
		
		return fbUser;
	}



	@Override
	public void removeAuthentication(Authentication authentication){
		Connection<Facebook> connection = connectionRepository.findPrimaryConnection(Facebook.class);		
		connectionRepository.removeConnection(connection.getKey());		

		SecurityContextHolder.clearContext();
		SecurityContextHolder.getContext().setAuthentication(authentication);

	}



	public byte[] getImageFromUrl(String urlText) throws Exception {
		URL url = new URL(urlText);
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		try (InputStream inputStream = url.openStream()) {
			int n = 0;
			byte [] buffer = new byte[ 1024 ];
			while (-1 != (n = inputStream.read(buffer))) {
				output.write(buffer, 0, n);
			}
		}

		return output.toByteArray();
	}

}
