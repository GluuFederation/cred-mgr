(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('OPConfigDetailController', OPConfigDetailController);

    OPConfigDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'OPConfig'];

    function OPConfigDetailController($scope, $rootScope, $stateParams, entity, OPConfig) {
        var vm = this;

        vm.oPConfig = entity;

        var unsubscribe = $rootScope.$on('credmgrApp:oPConfigUpdate', function(event, result) {
            vm.oPConfig = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
