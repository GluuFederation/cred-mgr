(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('ResetFinishController', ResetFinishController);

    ResetFinishController.$inject = ['Principal', '$stateParams', '$timeout', 'Auth', 'LoginService'];

    function ResetFinishController(Principal, $stateParams, $timeout, Auth, LoginService) {
        var vm = this;

        vm.isAuthenticated = null;
        vm.keyMissing = angular.isUndefined($stateParams.key);
        vm.confirmPassword = null;
        vm.doNotMatch = null;
        vm.error = null;
        vm.finishReset = finishReset;
        vm.login = LoginService.open;
        vm.resetAccount = {};
        vm.success = null;

        Principal.identity().then(function (account) {
            vm.isAuthenticated = Principal.isAuthenticated;
        });

        $timeout(function (){angular.element('#password').focus();});

        function finishReset() {
            vm.doNotMatch = null;
            vm.error = null;
            if (vm.resetAccount.password !== vm.confirmPassword) {
                vm.doNotMatch = 'ERROR';
            } else {
                Auth.resetPasswordFinish({
                    key: $stateParams.key,
                    newPassword: vm.resetAccount.password,
                    companyShortName: $stateParams.csn
                }).then(function () {
                    vm.success = 'OK';
                }).catch(function () {
                    vm.success = null;
                    vm.error = 'ERROR';
                });
            }
        }
    }
})();
