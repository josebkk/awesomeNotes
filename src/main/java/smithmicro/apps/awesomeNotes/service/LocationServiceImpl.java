package smithmicro.apps.awesomeNotes.service;

import java.io.IOException;
import java.net.URL;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip.LookupService;

import smithmicro.apps.awesomeNotes.model.Location;

@Transactional
@Service("locationService")
public class LocationServiceImpl implements LocationService{


	@Autowired
	private Environment environment;


	@Override
	public Location getMyLocation(String ipAddress) throws IOException {
		String dataFile = environment.getProperty("awesomenotesapp.geolocation.datafile");
		return getLocation(ipAddress, dataFile);
	}


	private Location getLocation(String ipAddress, String locationDataFile) {
		Location location = null;
		URL url = getClass().getClassLoader().getResource(locationDataFile);
		if (url == null) {
			System.err.println("location database is not found - "
					+ locationDataFile);
		} else {
			try {
				location = new Location();
				LookupService lookup = new LookupService(url.getPath(),
						LookupService.GEOIP_MEMORY_CACHE);
				com.maxmind.geoip.Location locationServices = lookup.getLocation(ipAddress);

				location.setCountryName(locationServices.countryName);
				location.setCity(locationServices.city);
				location.setPostalCode(locationServices.postalCode);
				location.setLatitude(String
						.valueOf(locationServices.latitude));
				location.setLongitude(String
						.valueOf(locationServices.longitude));
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
		return location;
	}



	@Override
	public String locationToJsonString(Location location) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(location);
	}


	@Override
	public Location jsonStringToLocation(String jsonString) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(jsonString, Location.class);
	}


}
