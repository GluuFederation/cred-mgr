(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('OpenidServerConfigurationController', OpenidServerConfigurationController);

    OpenidServerConfigurationController.$inject = ['$scope', '$state', 'OpenidServerConfiguration'];

    function OpenidServerConfigurationController ($scope, $state, OpenidServerConfiguration) {
        var vm = this;
        
        vm.openidServerConfigurations = [];

        loadAll();

        function loadAll() {
            OpenidServerConfiguration.query(function(result) {
                vm.openidServerConfigurations = result;
            });
        }
    }
})();
