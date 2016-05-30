(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('GluuDetailController', GluuDetailController);

    GluuDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Gluu'];

    function GluuDetailController($scope, $rootScope, $stateParams, entity, Gluu) {
        var vm = this;

        vm.gluu = entity;

        var unsubscribe = $rootScope.$on('credmgrApp:gluuUpdate', function(event, result) {
            vm.gluu = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
