(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('OPConfigDeleteController',OPConfigDeleteController);

    OPConfigDeleteController.$inject = ['$uibModalInstance', 'entity', 'OPConfig'];

    function OPConfigDeleteController($uibModalInstance, entity, OPConfig) {
        var vm = this;

        vm.oPConfig = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            OPConfig.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
