/**
 * Created by eugeniuparvan on 5/30/16.
 */
(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('ResetPasswordController', ResetPasswordController);

    ResetPasswordController.$inject = ['Auth', 'Principal', 'LoginService', 'ResetOptions', '$scope', '$state'];

    function ResetPasswordController(Auth, Principal, LoginService, ResetOptions, $scope, $state) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = Principal.isAuthenticated;
        vm.login = LoginService.open;

        vm.resetOptions = [];
        vm.noConfigForReset = false;
        vm.errorRetrievingResetOptions = false;
        vm.onResetOptionChanged = onResetOptionChanged;
        vm.selectedResetOption = {};
        vm.resetPasswordError = null;
        vm.resetPasswordSuccess = null;
        vm.resetAccountEmail = null;
        vm.resetAccountMobile = null;
        vm.resetAccount = {};
        vm.disableResetPasswordBtn = false;
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

        ResetOptions.get(
            function (response) {
                vm.resetOptions = [];
                if (response.email == false && response.mobile == false) {
                    vm.noConfigForReset = true;
                }
                else {
                    if (response.email == true) {
                        vm.resetOptions.push({code: "email", name: "reset-password.reset.request.email.title"});
                    }
                    if (response.mobile == true) {
                        vm.resetOptions.push({code: "mobile", name: "reset-password.reset.request.mobile.title"});
                    }
                    vm.selectedResetOption = vm.resetOptions[0];
                    vm.noConfigForReset = false;
                }
                vm.errorRetrievingResetOptions = false;
            },
            function (data) {
                vm.errorRetrievingResetOptions = true;
            }
        );

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
            vm.disableResetPasswordBtn = true;
            Auth.resetPasswordInit({
                "email": vm.resetAccountEmail,
                "mobile": vm.resetAccountMobile
            }).then(function (response) {
                vm.resetPasswordSuccess = 'OK';
                vm.disableResetPasswordBtn = false;
            }).catch(function (response) {
                vm.resetPasswordSuccess = null;
                vm.disableResetPasswordBtn = false;
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
