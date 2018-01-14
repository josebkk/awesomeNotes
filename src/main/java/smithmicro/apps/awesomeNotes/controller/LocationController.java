package smithmicro.apps.awesomeNotes.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import smithmicro.apps.awesomeNotes.model.Location;
import smithmicro.apps.awesomeNotes.service.LocationService;

@RestController
public class LocationController {


	@Autowired
	private LocationService locationService;


	@RequestMapping(value = "/getLocationByIpAddress", method = RequestMethod.GET)
	public @ResponseBody
	String getLocation(@RequestParam String ipAddress) throws IOException {

		//get location object by public ip address. For dynamic IPs, the location could not be accurate.
		Location location = locationService.getMyLocation(ipAddress);

		return locationService.locationToJsonString(location);

	}

}
