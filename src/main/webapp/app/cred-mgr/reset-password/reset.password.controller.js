/**
 * Created by eugeniuparvan on 5/30/16.
 */
(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('ResetPasswordController', ResetPasswordController);

    ResetPasswordController.$inject = ['Auth', 'Principal', 'LoginService', '$scope', '$state'];

    function ResetPasswordController(Auth, Principal, LoginService, $scope, $state) {
        var vm = this;

        vm.resetPasswordSuccess = null;

        vm.updatePasswordError = null;
        vm.updatePasswordSuccess = null;

        vm.errorEmailNotExists = null;
        vm.requestReset = null;
        vm.resetAccount = {};

        vm.resetAccountEmail = null;
        vm.resetAccountUsername = null;
        vm.isAuthenticated = Principal.isAuthenticated;
        vm.password = null;
        vm.confirmPassword = null;
        vm.doNotMatch = false;
        vm.changePassword = changePassword;
        vm.login = LoginService.open;

        function changePassword() {
            if (vm.password !== vm.confirmPassword) {
                vm.updatePasswordError = null;
                vm.updatePasswordSuccess = null;
                vm.doNotMatch = 'ERROR';
            } else {
                vm.doNotMatch = null;
                Auth.changePassword(vm.password).then(function () {
                    vm.updatePasswordError = null;
                    vm.updatePasswordSuccess = 'OK';
                }).catch(function () {
                    vm.updatePasswordSuccess = null;
                    vm.updatePasswordError = 'ERROR';
                });
            }
        };
    }
})();
