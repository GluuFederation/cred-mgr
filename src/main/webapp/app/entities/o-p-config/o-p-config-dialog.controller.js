(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('OPConfigDialogController', OPConfigDialogController);

    OPConfigDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'OPConfig'];

    function OPConfigDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, OPConfig) {
        var vm = this;

        vm.oPConfig = entity;
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
            if (vm.oPConfig.id !== null) {
                OPConfig.update(vm.oPConfig, onSaveSuccess, onSaveError);
            } else {
                OPConfig.save(vm.oPConfig, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('credmgrApp:oPConfigUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
