/*
 * angular-error
 * Version: 1.0 - 09/06/2015
 * License: Apache
 */
angular.module('angular-error', ['ui.bootstrap', 'ngSanitize'])
.controller('ErrorModalController', function($scope, $modalInstance, data) {
  $scope.data = angular.copy(data);

  $scope.ok = function () {
    $modalInstance.close();
  };

})
.value('$errorModalDefaults', {
	template: '<div class="modal-header"><h3 class="modal-title" ng-class="{\'text-info\': data.info, \'text-danger\': data.danger, \'text-warning\' : data.warning}">{{data.title}}</h3></div><div class="modal-body">{{data.text}}<p ng-bind-html="data.incluirHTML"></p></div><div class="modal-footer"><button class="btn btn-success" ng-click="ok()">OK</button>',
	controller: 'ErrorModalController'
})
.factory('$error', function($modal, $errorModalDefaults) {
  return function(data, settings) {
    settings = angular.extend($errorModalDefaults, (settings || {}));
    data = data || {};
    
    if ('templateUrl' in settings && 'template' in settings) {
      delete settings.template;
    }
    
    settings.resolve = {data: function() { return data; }};

    return $modal.open(settings).result;
  };
})
.provider('errorProvider', function ErrorProvider(){
    this.$get = ['$error', function errorFactory($error) {
            this.openError = function (data, settings) {
                
            };
    }];
})
.directive('error', function($error) {
    return {
      priority: 1,
      restrict: 'A',
      scope: {
        errorIf: "=",
        ngClick: '&',
        error: '@'
      },
      link: function(scope, element, attrs) {
        function reBind(func) {
          element.unbind("click").bind("click", function() {
            func();
          });
        }
        
        function bindError() {
          $error({text: scope.error}).then(scope.ngClick);
        }
        
        if ('errorIf' in attrs) {
          scope.$watch('errorIf', function(newVal) {
            if (newVal) {
              reBind(bindError);
           } else {
              reBind(function() {
             	  scope.$apply(scope.ngClick);
              }); 
            }
          });
        } else {
          reBind(bindError);
        }
      }
    };
});
