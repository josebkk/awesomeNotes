package smithmicro.apps.awesomeNotes.service;


import java.util.List;

import smithmicro.apps.awesomeNotes.model.Role;
import smithmicro.apps.awesomeNotes.model.User;

public interface UserService {
	public User findUserByEmail(String email);
	public Role findRoleByName(String roleName);
	public User findUserByResetToken(String resetToken);
	public List<User> findAllUsers();
	public User saveUser(User user);
	public String getAvatarBase64Image(User loggedUser);
	public User findByFacebookId(String facebookId);
	public User findById(Long userId);
}