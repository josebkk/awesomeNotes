package smithmicro.apps.awesomeNotes.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "location")
@EntityListeners(AuditingEntityListener.class)
public class Location implements Serializable {


	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "location_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int locationId;

	@Version
	@Column(name = "version")
	private long version;

	@Column(nullable = false)
	private String latitude;

	@Column(nullable = false)
	private String longitude;

	@Column(name = "country_name", nullable = true)
	private String countryName;

	@Column(nullable = true)
	private String city;

	@Column(name = "postal_code", nullable = true)
	private String postalCode;


	public Location(){

	}


	public Location(String latitude, String longitude,
			String countryName, String city, String postalCode) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.countryName = countryName;
		this.city = city;
		this.postalCode = postalCode;
	}


	public int getLocationId() {
		return locationId;
	}


	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}


	public String getLatitude() {
		return latitude;
	}


	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}


	public String getLongitude() {
		return longitude;
	}


	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}


	public long getVersion() {
		return version;
	}



	public void setVersion(long version) {
		this.version = version;
	}



	public String getCountryName() {
		return countryName;
	}



	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}



	public String getCity() {
		return city;
	}



	public void setCity(String city) {
		this.city = city;
	}



	public String getPostalCode() {
		return postalCode;
	}



	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

}
