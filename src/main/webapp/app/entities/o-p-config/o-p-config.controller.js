(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('OPConfigController', OPConfigController);

    OPConfigController.$inject = ['$scope', '$state', 'OPConfig'];

    function OPConfigController ($scope, $state, OPConfig) {
        var vm = this;
        
        vm.oPConfigs = [];

        loadAll();

        function loadAll() {
            OPConfig.query(function(result) {
                vm.oPConfigs = result;
            });
        }
    }
})();
