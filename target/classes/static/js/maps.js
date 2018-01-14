function showMap(latitude,longitude,dragMarker) { 
			
			var googleLatandLong = new google.maps.LatLng(latitude,longitude);
		
			var mapOptions = { 
				zoom: 7,
				center: googleLatandLong,
				mapTypeId: google.maps.MapTypeId.HYBRID
			};
		
			var mapDiv = document.getElementById("map");
			map = new google.maps.Map(mapDiv, mapOptions);
			
			var title = "My Location"; 
			addMarker(map, googleLatandLong, title, "", dragMarker);
			
		}




function addMarker(map, latlong, title, content, dragMarker) { 
	
	var markerOptions = {
		position: latlong, 
		draggable: dragMarker,
		map: map,
		title: title, 
		clickable: true
	};
	
	marker = new google.maps.Marker(markerOptions);
	
	
	
	google.maps.event.addListener(marker, 'dragend', function (event) {
	    var newLat = this.getPosition().lat();
	    var newLon = this.getPosition().lng();
	    
	    var jString = $("#jsonStringLocation").val();
	    
        var newjson = JSON.parse(jString);
        
        newjson["latitude"] = newLat;
        newjson["longitude"] = newLon;
        
        var locationNewCoord = JSON.stringify(newjson);
        
        $("#jsonStringLocation").val(locationNewCoord);
	    
	    var geocoder;
	    geocoder = new google.maps.Geocoder();
	    var latlng = new google.maps.LatLng(newLat, newLon);

	    geocoder.geocode(
	        {'latLng': latlng}, 
	        function(results, status) {
	            if (status == google.maps.GeocoderStatus.OK) {
	                    if (results[0]) {
	                        var add= results[0].formatted_address;
	                        console.log(add);
	                       
	                        
	                        try{
	                        	var  postalCode = add.split(", ")[1].split(" ")[0];
	                        }catch(err){
	                        	alert("Cannot retrieve postal code. Please select another location");
	                        }
	                        
	                        try{
	                        	var  cityName = add.split(", ")[1].split(" ")[1];
	                        }catch(err){
	                        	alert("Cannot retrieve city. Please select another location");
	                        }
	                        
	                        try{
	                        	var  countryName = add.split(", ")[2];
	                        }catch(err){
	                        	alert("Cannot retrieve country. Please select another location");
	                        }
	                        
	                        
	                        var jsonString = $("#jsonStringLocation").val();
	                        var jsonNewNames = JSON.parse(jsonString);
	                        
	                        jsonNewNames["postalCode"] = postalCode;
	                        jsonNewNames["city"] = cityName;
	                        jsonNewNames["countryName"] = countryName;
	                        
	                        var newLocation = JSON.stringify(jsonNewNames);
	                        console.log(newLocation);
	                        $("#jsonStringLocation").val(newLocation);
	        				$("#locationText").html('Choosed location: ' + jsonNewNames["postalCode"]+', ' + jsonNewNames["city"]+' - ' + jsonNewNames["countryName"] + '. (Hint: You can drag the location pin to set other location)');
	                        
	                    }
	                    else  {
	                        alert("address not found");
	                    }
	            }
	             else {
	                alert("Geocoder failed due to: " + status);
	            }
	        }
	    );    
	});
	
	
	
	
}





