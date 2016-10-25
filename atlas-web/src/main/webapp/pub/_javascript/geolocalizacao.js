latitude = null;
longitude = null;
reverseGeolocation = null;
position = null;

if (navigator.geolocation) {
    checkGeolocationByHTML5();
} else {
    checkGeolocationByLoaderAPI(); // HTML5 n�o suportado
}

function checkGeolocationByHTML5() {
	navigator.geolocation.getCurrentPosition(function(position) {
		this.position = position;
		latitude = position.coords.latitude;
		longitude = position.coords.longitude;
		defineReverseGeolocation();
	}, function() {
		checkGeolocationByLoaderAPI(); // Erro ao obter geolocaliza��o! Realiza tentativa by Loader API.
	});
}

function defineReverseGeolocation() {
	var geocoder = new google.maps.Geocoder();
	var latlng = new google.maps.LatLng(latitude, longitude);
	geocoder.geocode({'latLng': latlng}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			reverseGeolocation = results;
		} else {
			console.log('Falha no Geocoder: ' + status);
		}
	});
}

function checkGeolocationByLoaderAPI() {
    if (google.loader.ClientLocation) {
		latitude = google.loader.ClientLocation.latitude;
		longitude = google.loader.ClientLocation.longitude;
		defineReverseGeolocation();
    } else {
        console.log('N�o foi poss�vel obter a geolocaliza��o do usu�rio.');
    }
}