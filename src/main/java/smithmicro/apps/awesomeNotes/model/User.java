package smithmicro.apps.awesomeNotes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)

public class User implements Serializable{

	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id")
	private Long id;
	
	
	@Column(unique=true)
	@Email(message = "*Please provide a valid Email")
	@NotEmpty(message = "*Please provide an email")
	private String email;
	
	@Length(min = 8, message = "*Your password must have at least 8 characters")
	@NotEmpty(message = "*Please provide your password")
	@JsonIgnore
	private String password;
	
	@NotEmpty(message = "*Please provide your name")
	private String name;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Lob
	@Column(name="avatar", nullable = true)
	private byte[] avatar;
	
	private int active;
	
	@Column(name = "reset_token", unique=true)
	private String resetToken;
	
	
	// used to shared note checkbox
	@Transient
	private boolean checked;
	
	
	/** FACEBOOK ELEMENTS **/
	@Column(name = FACEBOOK_ID, length = 500)
	private String facebookId;
	public static final String FACEBOOK_ID = "facebook_id";
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"), 
    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
	@JsonManagedReference
	private List<Role> roles;
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
	@JoinTable(name = "user_note", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"), 
    inverseJoinColumns = @JoinColumn(name = "note_id", referencedColumnName = "note_id"))
	@JsonManagedReference
	private List<Note> notes;

	
	public User(){
		
	}
	
	public User(String email, String password, String name, String lastName, int active,
			String resetToken, List<Role> roles, List<Note> notes) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.lastName = lastName;
		this.active = active;
		this.resetToken = resetToken;
		this.roles = new ArrayList<Role>();;
		this.notes = new ArrayList<Note>();;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public byte[] getAvatar() {
		return avatar;
	}

	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}
	
	
	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}


	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	

}
