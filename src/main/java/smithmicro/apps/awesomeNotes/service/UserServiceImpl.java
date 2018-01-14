package smithmicro.apps.awesomeNotes.service;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smithmicro.apps.awesomeNotes.model.Role;
import smithmicro.apps.awesomeNotes.model.User;
import smithmicro.apps.awesomeNotes.repository.RoleRepository;
import smithmicro.apps.awesomeNotes.repository.UserRepository;

@Transactional
@Service("userService")
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;


	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User saveUser(User user) {
		user.setActive(1);
		return userRepository.save(user);
	}

	@Override
	public List<User> findAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public String getAvatarBase64Image(User loggedUser) {

		return Base64.encodeBase64String(loggedUser.getAvatar());

	}

	@Override
	public Role findRoleByName(String roleName) {
		return roleRepository.findByRole(roleName);
	}


	@Override
	public User findUserByResetToken(String resetToken) {
		return userRepository.findByResetToken(resetToken);
	}

	@Override
	public User findByFacebookId(String facebookId) {
		return userRepository.findByFacebookId(facebookId);

	}

	@Override
	public User findById(Long userId) {
		return userRepository.getOne(userId);
	}



}