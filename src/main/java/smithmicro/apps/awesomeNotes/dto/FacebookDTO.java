package smithmicro.apps.awesomeNotes.dto;

import java.io.Serializable;

public class FacebookDTO implements Serializable {

	private static final long serialVersionUID = -6214606890253226746L;

	private String email;
	private String id;
	private String displayName;
	private String imageURL;

	public FacebookDTO() {
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
}