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

        vm.account = null;
        vm.resetPasswordError = null;
        vm.resetPasswordSuccess = null;

        vm.updatePasswordError = null;
        vm.updatePasswordSuccess = null;

        vm.requestReset = null;
        vm.resetAccount = {};

        vm.resetAccountEmail = null;
        vm.resetAccountCompanyShortName = null;
        vm.isAuthenticated = Principal.isAuthenticated;
        vm.password = null;
        vm.confirmPassword = null;
        vm.doNotMatch = false;
        vm.changePassword = changePassword;
        vm.login = LoginService.open;
        vm.requestReset = requestReset;

        Principal.identity().then(function (account) {
            vm.account = account;
        });

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
        function requestReset() {
            vm.resetPasswordError = null;
            vm.resetPasswordSuccess = null;

            var companyShortName = null;
            if (vm.isAuthenticated())
                companyShortName = vm.account.opConfig.companyShortName;
            else
                companyShortName = vm.resetAccountCompanyShortName;
            Auth.resetPasswordInit({
                "email": vm.resetAccountEmail,
                "companyShortName": companyShortName
            }).then(function (response) {
                vm.resetPasswordSuccess = 'OK';
            }).catch(function (response) {
                vm.resetPasswordSuccess = null;
                if (response.data != null)
                    vm.resetPasswordError = response.data.message;
                else
                    vm.resetPasswordError = 'ERROR';
            });
        };
    }
})();
