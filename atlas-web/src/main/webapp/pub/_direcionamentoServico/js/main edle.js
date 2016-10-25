/* global angular */
var app = angular.module('solrSearchApp', ['ngResource', 'ngSanitize', 'ui.bootstrap', 'ngGeolocation']);

app.config(['$httpProvider', function ($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    }
]);

app.factory('DocumentoService', ['$resource',
    function ($resource) {
        return $resource("http://localhost:8983/solr/mj/select?wt=json&indent=true&q=:texto&df=texto_busca&start=:inicio&rows=:tamanhoPagina");
//        return $resource("http://172.17.6.76:8983/solr/mj/select?wt=json&indent=true&q=:texto&df=texto_busca&start=:inicio&rows=:tamanhoPagina");
    }
]);

app.factory('DocumentoServiceLocal', ['$resource',
    function ($resource) {
        return $resource("http://localhost:8983/solr/mj/select?wt=json&indent=true&q=:texto&df=texto_busca&start=:inicio&rows=:tamanhoPagina&sort=geodist(localidade,:lat,:lng) asc");
//        return $resource("http://172.17.6.76:8983/solr/mj/select?wt=json&indent=true&q=:texto&df=texto_busca&start=:inicio&rows=:tamanhoPagina&sort=geodist(localidade,:lat,:lng) asc");

        //        return $resource("http://localhost\\:8983/solr/mj/select?q=:texto&df=texto_busca&start=:inicio&rows=:tamanhoPagina&wt=json&indent=true&spatial=true&pt=:lat,:lng&sfield=localidade&d=:distancia");
//        return $resource("http://localhost:8983/solr/mj/select?wt=json&indent=true&q=:texto&df=texto_busca&start=:inicio&rows=:tamanhoPagina&fq={!geofilt%20sfield=localidade}&pt=:lat,:lng&d=:distancia&sort=geodist(localidade, :lat,:lng) asc");
//        return $resource("http://localhost\\:8983/solr/mj/select?q=:texto&df=texto_busca&start=:inicio&rows=:tamanhoPagina&wt=json&indent=true");
    }
]);


app.directive('onLastRepeat', function () {
    return function (scope, element, attrs) {
        if (scope.$last)
            setTimeout(function () {
                $('.collapsible').collapsible({
                    accordion: false
                });
                $('ul.tabs').tabs();
            }, 1);
    };
});

//Controla o formulário de pesquisa
app.controller('SearchController', ['$scope', '$http', '$log', '$geolocation', 'DocumentoService','DocumentoServiceLocal',
    function ($scope, $http, $log, $geolocation, DocumentoService, DocumentoServiceLocal) {
        //http://10.120.66.29\\:8983/solr/mj_core/select
        var searchController = this;
        searchController.resposta = {};
        searchController.enderecos = [];
        searchController.pesquisou = false;
        searchController.distancia = 5;
        //paginacao
        searchController.currentPage = 1;
        searchController.pageSize = 10;
        searchController.maxSize = 5;
        searchController.limiteAtivo = true;

        searchController.ativarDesativarLimiteTexto = function (doc) {
            doc.ativado = !doc.ativado;
        };

        //-------------- INICIO GeoLocalizacao ----------------------
        searchController.$geolocation = $geolocation;

        searchController.setLocation = function (location) {
            searchController.location = location;
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
            return 0;
        };

        searchController.getLatitude = function () {
            if (searchController.location) {
                if (searchController.location.coords.latitude) {
                    return searchController.location.coords.latitude;
                }

                return $geolocation.position.coords.latitude;
            }
            return 0;
        };
        // basic usage
        $geolocation.getCurrentPosition().then(searchController.setLocation);


        searchController.recuperarEndereco = function () {
            $http.get('http://maps.google.com/maps/api/geocode/json?latlng=' + searchController.getLatitude() + ',' +
                    searchController.getLongitude() + '&sensor=false').success(function (response) {
                // hacky
                var zipCode;

                searchController.retornouEndereco = (response.status) && response.status === 'OK';

                if (searchController.retornouEndereco) {
                    angular.forEach(response.results, function (result) {
                        if (result.types[0] === 'postal_code') {
                            //zipCode = result.address_components[0].short_name;
                            searchController.enderecos.push(result.address_components[0].formatted_address);
                        }
                    });

                }
                ;
            }).error(function () {
                searchController.retornouEndereco = false;
            });
        };


        // regular updates
        $geolocation.watchPosition({
            timeout: 60000,
            maximumAge: 2,
            enableHighAccuracy: true
        });
        //-------------- FIM GeoLocalizacao ----------------------


        searchController.pesquisar = function (nrPage) {
            searchController.currentPage = nrPage;
            $log.debug("Chamou o pesquisar: " + searchController.currentPage);
            searchController.resposta = null;
            if (searchController.texto) {
                var inicioPagina = (searchController.currentPage - 1) * searchController.pageSize;

                $log.debug("Pesquisou por: " + searchController.texto);
                var servico = DocumentoService;
                
                if (searchController.getLatitude() !== 0) {
                    servico = DocumentoServiceLocal;
                }
                
                servico.get({
                    texto: searchController.texto,
                    distancia: searchController.distancia,
                    tamanhoPagina: searchController.pageSize,
                    inicio: inicioPagina,
                    lat: searchController.getLatitude(),
                    lng: searchController.getLongitude()
                }, function (data) {
                    searchController.resposta = data;
                    if (inicioPagina === 0) {
                        searchController.totalItems = searchController.getTotalDocumentos();
                    }
                    searchController.pesquisou = true;
                });
            }
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
            return this.resposta.response.docs;
        };

        searchController.calcularDistancia = function (pontoUm) {
            if (searchController.getLatitude() && pontoUm) {
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
            if (angular.isUndefined(searchController.resposta) || searchController.resposta === null) {
                return "";
            }
            if (angular.isUndefined(searchController.resposta.response) || searchController.resposta.response === null) {
                return "";
            }
            if (angular.isUndefined(searchController.resposta.responseHeader.params) || searchController.resposta.responseHeader.params === null) {
                return "";
            }
            return searchController.resposta.responseHeader.params.q;
        };


    }
]);