package smithmicro.apps.awesomeNotes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "note")
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"created", "updated"}, 
allowGetters = true)
public class Note implements Serializable{

	private static final long serialVersionUID = 1L;

	@GeneratedValue(strategy=GenerationType.AUTO)
	@Id
	@Column(name = "note_id")
	private int id;


	@Version
	@Column(name = "version")
	private long version;


	@NotEmpty(message = "*Please insert a note title")
	private String title;

	@Column(columnDefinition = "TEXT", length=1500)
	@NotEmpty(message = "*Please insert some text on the note")
	private String content;


	@Column(nullable = false, updatable = false, columnDefinition="DATETIME DEFAULT CURRENT_TIMESTAMP")
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date created;

	@Column(nullable = true, updatable = true, columnDefinition="DATETIME ON UPDATE CURRENT_TIMESTAMP")
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	private Date updated;

	@Lob
	@Column(name="image", nullable = true)
	private byte[] image;


	@ManyToOne(cascade = CascadeType.PERSIST, fetch=FetchType.LAZY)
	@JoinColumn(name = "created_by", nullable=false, referencedColumnName="user_id")
	@JsonBackReference
	private User createdBy;


	@ManyToOne(cascade = CascadeType.PERSIST, fetch=FetchType.LAZY)
	@JoinColumn(name = "updated_by", referencedColumnName="user_id")
	@JsonBackReference
	private User updatedBy;


	@ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "location_id")
	@JsonBackReference
	private Location location;



	@ManyToMany(mappedBy = "notes", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
	@JsonBackReference
	private List<User> users;



	public Note(){

	}



	public Note(String title, String content, Date created, Date updated, byte[] image, User createdBy,
			User updatedBy, Location location, List<User> users) {
		this.title = title;
		this.content = content;
		this.created = new Date();
		this.image = image;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.location = location;
		this.users = new ArrayList<User>();
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}


	public int getNoteId() {
		return id;
	}


	public void setNoteId(int noteId) {
		this.id = noteId;
	}



	public User getCreatedBy() {
		return createdBy;
	}



	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}



	public User getUpdatedBy() {
		return updatedBy;
	}



	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}



	public Location getLocation() {
		return location;
	}



	public void setLocation(Location location) {
		this.location = location;
	}



	public List<User> getUsers() {
		return users;
	}



	public void setUsers(List<User> users) {
		this.users = users;
	}

}
