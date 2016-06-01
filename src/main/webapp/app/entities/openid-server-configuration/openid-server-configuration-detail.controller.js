(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('OpenidServerConfigurationDetailController', OpenidServerConfigurationDetailController);

    OpenidServerConfigurationDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'OpenidServerConfiguration', 'User'];

    function OpenidServerConfigurationDetailController($scope, $rootScope, $stateParams, entity, OpenidServerConfiguration, User) {
        var vm = this;

        vm.openidServerConfiguration = entity;

        var unsubscribe = $rootScope.$on('credmgrApp:openidServerConfigurationUpdate', function(event, result) {
            vm.openidServerConfiguration = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
