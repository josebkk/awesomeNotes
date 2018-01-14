package smithmicro.apps.awesomeNotes.service;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import smithmicro.apps.awesomeNotes.model.Location;

public interface LocationService {

	public Location getMyLocation(String ipAddress) throws IOException;
	public String locationToJsonString(Location location) throws JsonProcessingException;
	public Location jsonStringToLocation(String jsonString) throws JsonParseException, JsonMappingException, IOException;
}
