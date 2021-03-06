package smithmicro.apps.awesomeNotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import smithmicro.apps.awesomeNotes.model.User;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);
	User findByResetToken(String resetToken);
	User findByFacebookId(String facebookId);

}
