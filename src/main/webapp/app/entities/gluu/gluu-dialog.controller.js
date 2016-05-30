(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('GluuDialogController', GluuDialogController);

    GluuDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Gluu'];

    function GluuDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Gluu) {
        var vm = this;

        vm.gluu = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.gluu.id !== null) {
                Gluu.update(vm.gluu, onSaveSuccess, onSaveError);
            } else {
                Gluu.save(vm.gluu, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('credmgrApp:gluuUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
