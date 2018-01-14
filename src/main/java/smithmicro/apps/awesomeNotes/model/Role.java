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
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "role")
@EntityListeners(AuditingEntityListener.class)
public class Role implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="role_id")
	private int id;
	
	private String role;
	
	@ManyToMany(mappedBy = "roles", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
	@JsonBackReference
	private List<User> users;
	
	
	public Role(){
		
	}
	
	public Role(String role, List<User> users) {
		this.role = role;
		this.users = new ArrayList<User>();;
	}



	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	
}
