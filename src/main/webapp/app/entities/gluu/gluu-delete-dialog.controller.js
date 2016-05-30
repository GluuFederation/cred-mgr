(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('GluuDeleteController',GluuDeleteController);

    GluuDeleteController.$inject = ['$uibModalInstance', 'entity', 'Gluu'];

    function GluuDeleteController($uibModalInstance, entity, Gluu) {
        var vm = this;

        vm.gluu = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Gluu.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
