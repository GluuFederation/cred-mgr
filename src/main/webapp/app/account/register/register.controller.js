(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('CredMgrRegisterController', CredMgrRegisterController);


    CredMgrRegisterController.$inject = ['$translate', '$timeout', 'Auth', 'LoginService'];

    function CredMgrRegisterController($translate, $timeout, Auth, LoginService) {
        var vm = this;

        vm.doNotMatch = null;
        vm.error = null;
        vm.login = LoginService.open;
        vm.register = register;
        vm.registerAccount = {};
        vm.success = null;

        $timeout(function () {
            angular.element('#login').focus();
        });

        function register() {
            if (vm.registerAccount.password !== vm.confirmPassword) {
                vm.doNotMatch = 'ERROR';
            } else {
                vm.registerAccount.langKey = $translate.use();
                vm.doNotMatch = null;
                vm.error = null;
                Auth.createAccount(vm.registerAccount).then(function () {
                    vm.success = 'OK';
                }).catch(function (response) {
                    vm.success = null;
                    if (response.data != null)
                        vm.error = response.data.message;
                    else
                        vm.error = 'ERROR';
                });
            }
        }
    }
})();
