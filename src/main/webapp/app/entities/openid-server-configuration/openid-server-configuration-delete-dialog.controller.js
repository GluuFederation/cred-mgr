(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('OpenidServerConfigurationDeleteController',OpenidServerConfigurationDeleteController);

    OpenidServerConfigurationDeleteController.$inject = ['$uibModalInstance', 'entity', 'OpenidServerConfiguration'];

    function OpenidServerConfigurationDeleteController($uibModalInstance, entity, OpenidServerConfiguration) {
        var vm = this;

        vm.openidServerConfiguration = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            OpenidServerConfiguration.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
