(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('OpenidServerConfigurationDialogController', OpenidServerConfigurationDialogController);

    OpenidServerConfigurationDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'OpenidServerConfiguration', 'User'];

    function OpenidServerConfigurationDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, OpenidServerConfiguration, User) {
        var vm = this;

        vm.openidServerConfiguration = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.openidServerConfiguration.id !== null) {
                OpenidServerConfiguration.update(vm.openidServerConfiguration, onSaveSuccess, onSaveError);
            } else {
                OpenidServerConfiguration.save(vm.openidServerConfiguration, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('credmgrApp:openidServerConfigurationUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
