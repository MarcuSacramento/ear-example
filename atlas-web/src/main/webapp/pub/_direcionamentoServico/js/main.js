/* global angular */
var app = angular.module('solrSearchApp', ['ngResource', 'ui.bootstrap', 'ngGeolocation', 'atlasServicos','angular-error', 'app.mapa']);

app.config(
		['$httpProvider', function ($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    }
]);

app.constant('urls', {
    BASE_SOLR: 'http://164.41.222.67:443/solr',
    BASE: '',
    BASE_API: '../restful-services/json'
 });

app.factory('ServicoDetalheService', ['$resource', 'urls',
    function ($resource, urls) {
        return $resource(urls.BASE_SOLR + "/mj_servico/select?wt=json&indent=true&q=:id&df=id");
    }
]);

app.factory('DocumentoService', ['$resource', 'urls',
    function ($resource, urls) {
	var servico = this;
		
        servico.pesquisar = function (params, callback, callbackError) {
                var urlBase = urls.BASE_SOLR +"/mj_orgao/select?wt=json&indent=true";
                var query = "&q=:texto";
                var queryLocation = "";
                servico.termoBusca = {};
                
                if (params) {
                		servico.termoBusca.nmeTermoBusca = params.texto; 
                	
                        if (params.uf) {
                    		servico.termoBusca.codUf = params.uf; 
                        	query += " AND cod_uf::uf";
                        }

                        if (params.municipio) {
                        		servico.termoBusca.nmeMunicipio = params.municipio; 
                                query += " AND nme_municipio::municipio";
                        }

                        if (params.lat && params.lat !== 0) {
                                queryLocation = "&sort=geodist(localidade,:lat,:lng) asc";
                        }
                }
                
                query += "&pf=nme_orgao^10 nme_servico^20&df=texto_busca&ps=5";
                
                var urlPaginacao = "&start=:inicio&rows=:tamanhoPagina&";
                
                var url = urlBase + query + urlPaginacao + queryLocation;
                
                return $resource(url).get(params, callback, callbackError);
        };
        
        return servico;
    }
]);

app.directive('onLastRepeat', function () {
    return function (scope, element, attrs) {
        if (scope.$last)
            setTimeout(function () {
                $('.collapsible').collapsible({
                    accordion: false
                });
            }, 2);
    };
});

//Controla o formulário de pesquisa
app.controller('SearchController', ['$scope', '$http', '$log', '$geolocation', 'DocumentoService', 'urls', 'ATLASServico','$location','ServicoDetalheService','$timeout',
    function ($scope, $http, $log, $geolocation, DocumentoService, urls, ATLASServico, $location, ServicoDetalheService, $timeout) {
		var pathServicoUf = urls.BASE_API + '/ufs';
		var pathServicoMunicipio = urls.BASE_API + '/municipios/sigla';
		var pathTermoBusca = urls.BASE_API + '/termobusca';
		var pathTermosMaisBuscados = urls.BASE_API + '/termosbusca/5';
		
		$scope.map = { center: { latitude: 45, longitude: -73 }, zoom: 8 };
		
		var searchController = this;
        searchController.resposta = {};
        searchController.ufSelecionada = {};
        searchController.municipioSelecionado = {};
        searchController.enderecos = [];
        searchController.ufs = [];
        searchController.termosMais = [];
        searchController.encontrou = false;
        searchController.pesquisou = false;
        searchController.erroPesquisa = false;
        searchController.distancia = 5;
        searchController.exibirBannerConsumidor = false;
        searchController.exibirBannerMulher = false;
        searchController.mapaController = undefined;
        searchController.exibeAjuda = false;
        
        //paginacao
        searchController.currentPage = 1;
        searchController.pageSize = 10;
        searchController.maxSize = 5;
        searchController.limiteAtivo = true;
        
        searchController.init = function (){
            ATLASServico.config(pathServicoUf).recuperarTodos(searchController.carregarUfs);
            ATLASServico.config(pathTermosMaisBuscados).recuperarTodos(searchController.carregarTermosMaisBuscados);
            //-------------- INICIO GeoLocalizacao ----------------------
            searchController.$geolocation = $geolocation;
            $geolocation.getCurrentPosition().then(searchController.setLocation);
            // regular updates
            $geolocation.watchPosition({
                timeout: 60000,
                maximumAge: 2,
                enableHighAccuracy: true
            });
          //-------------- FIM da GeoLocalizacao ----------------------
        };
        
        
        
        searchController.exibirAjuda = function (){
        	searchController.exibeAjuda = !searchController.exibeAjuda;
        };
        
        searchController.setMapaController = function (controller){
        	searchController.mapaController = controller;
        };
        
        searchController.carregarUfs = function (dados) {
        	searchController.ufs = dados;
        };
        
        searchController.carregarTermosMaisBuscados = function (dados) {
        	searchController.termosMais = dados;
        };
        
        searchController.recuperarMunicipios = function (sgUf) {
            if (sgUf) {
                ATLASServico.config(pathServicoMunicipio).recuperarPorID(sgUf, searchController.carregarMunicipios);
            } else {
                //se limpou UF, limpa municípios.
            	searchController.carregarMunicipios([]);
            }
        };
        
        searchController.carregarMunicipios = function (dados) {
        	searchController.municipios = dados;
        };
        
        searchController.processarDadosDocumento = function (doc){
        	searchController.ativarDesativarLimiteTexto(doc);
        	searchController.recuperarNotasDosServicos(doc);
        };
        
        searchController.ativarDesativarLimiteTexto = function (doc) {
            doc.ativado = !doc.ativado;
            $('#contato' + doc.id).toggle();
            $('iframe').css("pointer-events", "none");
            
        };

        searchController.recuperarNotasDosServicos = function (doc){
        	doc.servicos = [];
        	angular.forEach(doc.nme_servico_json, function (strServico) {
        		var servico = JSON.parse(strServico);
        		$log.debug("Código servico pesquisado: " + servico.id_tema);
                ServicoDetalheService.get(
                        {id: servico.id_tema},
                        function (data) {
                        	doc.servicos.push(data.response.docs[0]);
                        }
                    );
            });
        };
        
        searchController.setLocation = function (location) {
            searchController.location = location;
            if(searchController.location){
            	searchController.recuperarEndereco();
            }
        };

        searchController.limitarTexto = function (texto, tamanhoMaximo, inativo) {
            if ((inativo === undefined || !inativo) && texto.length > tamanhoMaximo) {
                return texto.substring(0, tamanhoMaximo) + '...';
            }
            return texto;
        };


        searchController.getLongitude = function () {
            if (searchController.location) {
                if (searchController.location.coords.longitude) {
                    return searchController.location.coords.longitude;
                }
                return $geolocation.position.coords.longitude;
            }
        };

        searchController.getLatitude = function () {
            if (searchController.location) {
                if (searchController.location.coords.latitude) {
                    return searchController.location.coords.latitude;
                }
                return $geolocation.position.coords.latitude;
            }
        };

        searchController.recuperarEndereco = function () {
            $http.get('http://maps.google.com/maps/api/geocode/json?latlng=' + searchController.getLatitude() + ',' +
                    searchController.getLongitude() + '&sensor=false').success(function (response) {

                searchController.retornouEndereco = (response.status) && response.status === 'OK';

                if (searchController.retornouEndereco) {
                    angular.forEach(response.results, function (result) {
                        if (result.types[0] === 'administrative_area_level_2') {
                        	searchController.enderecos.push(result.address_components);
                        }
                    });
                    // Setando estado e município recuperado pelo google
                    searchController.ufSelecionada = {"sigla":searchController.enderecos[0][1].short_name};
                    searchController.recuperarMunicipios(searchController.ufSelecionada.sigla);
                    searchController.municipioSelecionado = {"nome":searchController.enderecos[0][0].short_name};
                };
            }).error(function () {
                searchController.retornouEndereco = false;
            });
        };

        searchController.expandirNota = function (idNota) {
            $('#bodyServico' + idNota).toggle();
        };

        searchController.pesquisar = function (nrPage, texto) {
        	$('#mensagemIndisponibilidade').hide();
        	searchController.erroPesquisa = false;
        	searchController.pesquisou = false;
        	searchController.encontrou = false;
        	$('#mensagemPesquisaSemResultado').hide();
        	searchController.exibirBannerConsumidor = false;
        	searchController.exibirBannerMulher = false;
            searchController.currentPage = nrPage;
            searchController.resposta = {};
            $log.debug("Chamou o pesquisar: " + searchController.currentPage);
            if (texto) {
                searchController.texto = texto;
            }

            if (searchController.texto) {
                var inicioPagina = (searchController.currentPage - 1) * searchController.pageSize;

                $log.debug("Pesquisou por: " + searchController.texto);
                var documentoServico = DocumentoService;
                documentoServico.pesquisar(
                		//params
                	{
                		texto: searchController.texto,
                		distancia: searchController.distancia,
                		tamanhoPagina: searchController.pageSize,
                		inicio: inicioPagina,
                		uf: searchController.ufSelecionada.sigla,
                		municipio: searchController.municipioSelecionado.nome,
                		lat: searchController.getLatitude(),
                		lng: searchController.getLongitude()
                	}
                	, //callback
                	function (data) {
            			searchController.resposta = data;
            			if (inicioPagina === 0) {
            				searchController.totalItems = searchController.getTotalDocumentos();
            			}
            			searchController.encontrou = searchController.resposta.response.docs.length > 0;
            			searchController.pesquisou = true;
            			searchController.erroPesquisa = false;
            			$('#mensagemIndisponibilidade').hide();
            			searchController.validarExibicaoBanner();
            			if(searchController.encontrou){
            				// Atualizando o mapa e marcações com os documentos encontrados
            				if(searchController.mapaController){
            					$timeout(function() {
            						searchController.mapaController.init(searchController.getDocumentos());
            		        	});
            				}
            			}else{
            				// Pesquisou, mas não encontrou, exibe a mensagem
            				$('#mensagemPesquisaSemResultado').show();
            			}
            			// Realiza a contabilidade dos termos pesquisados 
            			documentoServico.termoBusca.sitLocalizado = searchController.encontrou;
            			ATLASServico.config(pathTermoBusca).salvar(documentoServico.termoBusca, searchController.carregarResultado);
            			ATLASServico.config(pathTermosMaisBuscados).recuperarTodos(searchController.carregarTermosMaisBuscados);
                	}
                	,
                	//callbackerror
                	function (){
                		$('#mensagemIndisponibilidade').show();
                		searchController.erroPesquisa = true;
                		searchController.pesquisou = false;
                	}
                );
                
            }
        };
        
        searchController.validarExibicaoBanner = function (){
            angular.forEach(searchController.getDocumentos(), function (documento) {
                if(documento.nme_tema){
                	angular.forEach(documento.nme_tema, function (tema) {
                		if(tema){
                			if(angular.uppercase(tema).indexOf('MULHER') != -1){
                				searchController.exibirBannerMulher = true;
                			}
                			if(angular.uppercase(tema).indexOf('CONSUMIDOR') != -1){
                            	searchController.exibirBannerConsumidor = true;
                			}	
                		}
                	});
                };
            });
        };
        
        searchController.carregarResultado = function (dados) {
        };
        
        searchController.getTotalDocumentos = function () {
            if (angular.isUndefined(this.resposta) || this.resposta === null) {
                return 0;
            }
            if (angular.isUndefined(this.resposta.response) || this.resposta.response === null) {
                return 0;
            }
            return searchController.resposta.response.numFound;
        };

        searchController.getQuantidadeDocumentos = function () {
            return searchController.getDocumentos().length;
        };


        searchController.getEnderecos = function () {
            return searchController.enderecos;
        };

        searchController.getDocumentos = function () {
            if (angular.isUndefined(searchController.resposta) || searchController.resposta === null) {
                return [];
            }
            if (angular.isUndefined(searchController.resposta.response) || searchController.resposta.response === null) {
                return [];
            }
            return searchController.resposta.response.docs;
        };

        searchController.calcularDistancia = function (pontoUm) {
            if (searchController.getLatitude() && pontoUm) {
            	
            	if (Array.isArray(pontoUm)) {
            		pontoUm = pontoUm[0];            		
            	}
            	
                var ponto1 = pontoUm.replace(' ', '').split(',');

                var lat1 = eval(ponto1[0]);
                var lon1 = eval(ponto1[1]);

                var lat2 = searchController.getLatitude();
                var lon2 = searchController.getLongitude();

                var R = 6371; // Radius of the earth in km
                var dLat = searchController.deg2rad(lat2 - lat1);  // deg2rad below
                var dLon = searchController.deg2rad(lon2 - lon1);
                var a =
                        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(searchController.deg2rad(lat1)) * Math.cos(searchController.deg2rad(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2)
                        ;
                var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                var d = R * c; // Distance in km
                return d;
            }
            return 0;
        };

        searchController.deg2rad = function (deg) {
            return deg * (Math.PI / 180);
        };

        searchController.getTextoPesquisado = function () {
        	return searchController.texto;
        };


    }
]);
app.controller('ServicoController', ['$log',
     function ($log) {
         var servicoController = this;

         servicoController.aberto = false;
         
         servicoController.verMaisVerMenos = "+ mais detalhes";

         servicoController.servico = {};
         
         servicoController.propriedadeManipulada = {};

         servicoController.getServicoPropriedade = function (servico, propriedade) {
             if (servico) {
                 var a = JSON.parse(servico);
                 return a[propriedade];
             }
             return "";
         };
         
         servicoController.setPropriedadeManipulada = function (nomePropriedade){
        	 servicoController.propriedadeManipulada = nomePropriedade;
         };
         
         servicoController.getServico = function (resposta) {
             if (angular.isUndefined(resposta) || resposta === null) {
                 return {};
             }
             if (angular.isUndefined(resposta.response) || resposta.response === null) {
                 return {};
             }
             return resposta.response.docs[0];
         };
         
         servicoController.expandirNota = function (idServico, idDiv) {
             servicoController.aberto = !servicoController.aberto;
             
             servicoController.verMaisVerMenos = servicoController.aberto ? "- recolher detalhes" : "+ mais detalhes";

             $('#bodyServico' + idDiv).toggle('show');
         };        
     }
 ]);
app.controller('ServicoMapaController',['$log','$timeout', function($log, $timeout){
		var mapaCtrl = this;
		mapaCtrl.init = function (documentos){
        	$log.debug("Chamou o init da rota...");
        	var latLngBrasilia = new google.maps.LatLng(-15.795003, -47.880028);
        	
        	// Configurando o mapa
        	var mapProp = {
        		center:latLngBrasilia, 
        		zoom:3,
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
        	map.setCenter(bounds.getCenter());
        	map.fitBounds(bounds);
        	
        	var listener = google.maps.event.addListener(map, "idle", function () {
        	    google.maps.event.removeListener(listener);
        	});
        	
        };
	} 
]);