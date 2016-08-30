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
        vm.isAuthenticated = Principal.isAuthenticated;
        vm.login = LoginService.open;

        vm.resetOptions = [
            {
                code: "email", name: "reset-password.reset.request.email.title"
            }, {
                code: "mobile", name: "reset-password.reset.request.mobile.title"
            }];
        vm.onResetOptionChanged = onResetOptionChanged;
        vm.selectedResetOption = {};
        vm.resetPasswordError = null;
        vm.resetPasswordSuccess = null;
        vm.resetAccountEmail = null;
        vm.resetAccountMobile = null;
        vm.resetAccountCompanyShortName = null;
        vm.resetAccount = {};
        vm.onRequestResetSubmit = onRequestResetSubmit;

        vm.unregisterFidoError = null;
        vm.unregisterFidoSuccess = null;
        vm.onUnregisterFidoSubmit = onUnregisterFidoSubmit;

        vm.updatePasswordError = null;
        vm.updatePasswordSuccess = null;
        vm.password = null;
        vm.confirmPassword = null;
        vm.doNotMatch = false;
        vm.onChangePasswordSubmit = onChangePasswordSubmit;


        Principal.identity().then(function (account) {
            vm.account = account;
        });

        function onChangePasswordSubmit() {
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

        function onResetOptionChanged() {
            vm.resetAccountEmail = null;
            vm.resetAccountMobile = null;
            vm.resetPasswordSuccess = null;
            vm.resetPasswordError = null;
        };

        function onRequestResetSubmit() {
            vm.resetPasswordError = null;
            vm.resetPasswordSuccess = null;

            var companyShortName = null;
            if (vm.isAuthenticated())
                companyShortName = vm.account.opConfig.companyShortName;
            else
                companyShortName = vm.resetAccountCompanyShortName;
            Auth.resetPasswordInit({
                "email": vm.resetAccountEmail,
                "mobile": vm.resetAccountMobile,
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

        function onUnregisterFidoSubmit() {
            Auth.unregisterFido().then(function () {
                vm.unregisterFidoError = null;
                vm.unregisterFidoSuccess = 'OK';
            }).catch(function () {
                vm.unregisterFidoError = 'ERROR';
                vm.unregisterFidoSuccess = null;
            });
        };

    }
})();
