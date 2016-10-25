'use strict';

// Equivalente a @Autowired
var atlasServicos = angular.module('atlasServicos', []);

/*
 * Servico Básico para chamadas REST
 * 
 */
atlasServicos.factory('ATLASServico', ['$resource', '$error', '$log',
    function ($resource, $error, $log) {
        var servico = this;

        servico.path;

        servico.entidade = {};
        servico.totalItens = 0;
        servico.listaDados = [];

        servico.config = function (path) {
            servico.path =  path;
            return servico;
        };

        servico.tratarErrorDesconhecido = function (error) {
            var tipoMensagem = "ERRO";
            var componente = '';
            var mensagem = 'Erro desconhecido.';
            if (error.status > 529 && error.status < 581) {
                if (error.data.mensagem) {
                    mensagem = error.data.mensagem;
                }
            }
            $error({title: tipoMensagem, text: mensagem, incluirHTML: componente});
        };

        servico.recuperarPorId = function (id, callback) {
            return $resource(servico.path + '/:id').get({id: id}, callback, servico.tratarErrorDesconhecido);
        };

        servico.alterar = function (_id, _entidade, callback) {
            var recursoAlterar = $resource(servico.path + '/:id', null,
                    {
                        'update': {method: 'PUT'}
                    });
            recursoAlterar.update({id: _id}, _entidade, callback, servico.tratarErrorDesconhecido);
        };

        servico.salvar = function (_entidade, callback) {
            $resource(servico.path).save({}, _entidade, callback, servico.tratarErrorDesconhecido);
        };

        servico.excluir = function (_id, callback) {
            $resource(servico.path + '/:id').delete({id: _id}, callback, servico.tratarErrorDesconhecido);
        };

        servico.excluirLogicamente = function (_id, callback) {
            var recursoExcluirLogicamente = $resource(servico.path + '/logicRemove/:id', null,
                    {
                        'update': {method: 'GET'}
                    });
            recursoExcluirLogicamente.update({id: _id}, callback, servico.tratarErrorDesconhecido);
        };

        servico.recuperarTodos = function (callback) {
        	if(!callback){
        		alert('servico.recuperarTodos >> Callback indefinido!');
        		return;
        	}
            $resource(servico.path).query({}, callback, servico.tratarErrorDesconhecido);
        };

        servico.pesquisar = function (_entidade, inicio, fim, callback) {
            var recursoPesquisa = $resource(servico.path + '/pesquisar/' + inicio + '/' + fim, null,
                    {
                        'pesquisar': {method: 'PUT', isArray: true}

                    });
            recursoPesquisa.pesquisar({}, _entidade, callback, servico.tratarErrorDesconhecido);
        };

        servico.pesquisarTotal = function (_entidade, callback) {
            var recursoPesquisa = $resource(servico.path + '/pesquisarTotal', null,
                    {
                        'pesquisarTotal': {method: 'PUT'}
                    });

            recursoPesquisa.pesquisarTotal({}, _entidade, callback, servico.tratarErrorDesconhecido);
        };

        servico.recuperarTodosFilhos = function (_id, pathFilhos, callback) {
            $resource(servico.path + '/:id/' + pathFilhos).query({id: _id}, callback, servico.tratarErrorDesconhecido);
        };
        
        servico.recuperarPorID = function (_id, callback) {
        	$resource(servico.path + '/:id').query({id: _id}, callback, servico.tratarErrorDesconhecido);
        };

        servico.recuperarTodosPorIntervalo = function (inicio, fim, callback) {
            $resource(servico.path + '/' + inicio + '/' + fim).query({}, callback, servico.tratarErrorDesconhecido);
        };

        servico.recuperarTotal = function (callback) {
            $resource(servico.path + '/total').get({}, callback, servico.tratarErrorDesconhecido);
        };

        servico.recuperar = function (path, callback) {
            $resource(servico.path + '/' + path).get({}, callback, servico.tratarErrorDesconhecido);
        };

        servico.recuperarTodosPorPath = function (path, callback) {
            $resource(servico.path + '/' + path).query({}, callback, servico.tratarErrorDesconhecido);
        };
        
        servico.recuperarParametrizado = function (path, paramObjeto, callback, callbackErro) {
        	var resourceParametrizado = $resource(servico.path + '/' + path, null, 
        			{
        				'recuperarParametrizado': {method: 'POST'} 
        			});
        	resourceParametrizado.recuperarParametrizado({}, paramObjeto, callback, callbackErro ? callbackErro : servico.tratarErrorDesconhecido);
        }
        
        return servico;
    }

]);

/*
 * Servico utilitário para construir tabelas
 */
atlasServicos.factory('TabelaUtil', [
    function () {
        var servico = this;

        servico.ocultarPreload = function (ocultar) {
            if (ocultar) {
                $('#preload').hide();
            }
        };

        //Recebe o padrão de navegação do objeto, tipo "object.property.name" e o objeto
        servico.toString = function (padrao, objecto, formatarFunc) {
            var valor = objecto;

            if (padrao || padrao.indexOf(".") > -1) {
                var propriedades = padrao.split(".");

                for (var i = 0; i < propriedades.length; i++) {
                    valor = valor[propriedades[i]];
                }

                if (formatarFunc) {
                    return formatarFunc(valor);
                }

                return valor;
            }

            if (formatarFunc) {
                return formatarFunc(valor[padrao]);
            }

            return valor[padrao];
        };

        return servico;
    }

]);