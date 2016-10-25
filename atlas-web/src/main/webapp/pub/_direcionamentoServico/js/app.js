'use strict';
/**
 * *
 * 
 * @ngdoc overview *
 * @name yoTestApp *
 * @description * # yoTestApp * * Main module of the application.
 */
angular
		.module('app.mapa', ['ngRoute'])
		.config(
				function($routeProvider) {
					$routeProvider
							.when('/mapa',
								{
									template : '<div id="mapa-direcionamento-servico" style="width: 750px; height: 250px;" ng-init="mapaCtrl.init(searchController.getDocumentos());"></div>',
									controller : 'MapaController',
									controllerAs : 'mapaCtrl'
								}).otherwise({
								redirectTo : '/'
							});
				})
		.controller('MapaController',['$log','$timeout', function($log, $timeout) {
	        var mapaCtrl = this;
	        
	        mapaCtrl.init = function (documentos){
	        	$log.debug("Chamou o init da rota...");
	        	var latLngBrasilia = new google.maps.LatLng(-15.795003, -47.880028);
	        	
	        	// Configurando o mapa
	        	var mapProp = {
	        		center:latLngBrasilia, 
	        		zoom:12,
	       			mapTypeId:google.maps.MapTypeId.ROADMAP
	        	};
	        	
	        	// Inserindo na tela
	        	var map = new google.maps.Map(document.getElementById("mapa-direcionamento-servico"), mapProp);
	        	var bounds = new google.maps.LatLngBounds();
	        	var infowindow = new google.maps.InfoWindow();
	        	
	        	// Percorrendo os órgãos e setando a latitude e longitude no mapa
	        	var i = 0;
	        	angular.forEach(documentos, function(value, key){
	    			var documento = documentos[key];
	    			var latitude = 0;
					var longitude = 0;
	    			if(documento.localidade){
	    				// Recuperando coordenadas dos órgãos
	    				var coordenada = documento.localidade[0].split(",");
	    				latitude = coordenada[0];
	    				longitude = coordenada[1];
	    				
	    				// Adicionando a marcação dos órgãos ao mapa
	    				var marker = new google.maps.Marker({
	    					position : new google.maps.LatLng(latitude, longitude),
	    					map : map,
	    					title : documento.nme_orgao,
	    					status: "active"
	    				});
	    				
	    				bounds.extend(marker.position);
	    				
	    				google.maps.event.addListener(marker, 'click', (function (marker, i) {
	    		            return function () {
	    		                infowindow.setContent(documento.nme_orgao);
	    		                infowindow.open(map, marker);
	    		            };
	    		        })(marker, i));
	    				i++;
	    			}
	    			$log.debug(documento.nme_orgao +" >> latitude: "+latitude+" >> longitude:"+longitude);
	    		});
	        	// Centraliza automaticamente o mapa de acordo com as marcações
	        	map.fitBounds(bounds);
	        	
	        	var listener = google.maps.event.addListener(map, "idle", function () {
	        	    map.setZoom(15);
	        	    google.maps.event.removeListener(listener);
	        	});
	        	
	        	// Exibe o mapa na tela
	        	$timeout(function() {
					google.maps.event.trigger(map, 'resize');
	        	});
	        };

		} ]);