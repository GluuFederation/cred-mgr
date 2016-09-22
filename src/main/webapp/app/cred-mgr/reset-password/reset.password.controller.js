/**
 * Created by eugeniuparvan on 5/30/16.
 */
(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('ResetPasswordController', ResetPasswordController);

    ResetPasswordController.$inject = ['Auth', 'Principal', 'LoginService', 'ResetOptions', 'Fido', '$scope', '$state', '$window', 'registerFidoSuccess', 'registerFidoError'];

    function ResetPasswordController(Auth, Principal, LoginService, ResetOptions, Fido, $scope, $state, $window, registerFidoSuccess, registerFidoError) {
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

        vm.fidoDevices = [];
        vm.unregisterFidoError = null;
        vm.unregisterFidoSuccess = null;
        vm.updateFidoError = null;
        vm.updateFidoSuccess = null;
        vm.registerFidoError = registerFidoError;
        vm.registerFidoSuccess = registerFidoSuccess;
        vm.onUnregisterFidoSubmit = onUnregisterFidoSubmit;
        vm.onUpdateFidoSubmit = onUpdateFidoSubmit;
        vm.onRegisterFidoSubmit = onRegisterFidoSubmit;

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

        if (Principal.isAuthenticated()) {
            onFidoGetAllDevices();
        }


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

        function onFidoGetAllDevices() {
            Fido.get(
                function (response) {
                    vm.fidoDevices = response;
                },
                function (data) {
                    vm.fidoDevices = [];
                }
            );
        }

        function onUpdateFidoSubmit(device) {
            Fido.update(device,
                function (response) {
                    clearFlags();
                    vm.updateFidoSuccess = "SUCCESS";
                    onFidoGetAllDevices();
                },
                function (data) {
                    clearFlags();
                    vm.updateFidoError = "ERROR";
                    onFidoGetAllDevices();
                }
            );
        };

        function onUnregisterFidoSubmit(id) {
            Fido.delete({id: id},
                function (response) {
                    clearFlags();
                    vm.unregisterFidoSuccess = "SUCCESS";
                    onFidoGetAllDevices();
                }, function (data) {
                    clearFlags();
                    vm.unregisterFidoError = "ERROR";
                    onFidoGetAllDevices();
                }
            );
        };

        function onRegisterFidoSubmit() {
            Fido.getRegisterRequest(
                function (response) {
                    $state.go('reset-password.fido', {fidoRegistrationResponse: response});
                }, function (data) {
                    vm.registerFidoError = "ERROR";
                }
            );
        };

        function clearFlags() {
            vm.unregisterFidoError = null;
            vm.unregisterFidoSuccess = null;
            vm.updateFidoError = null;
            vm.updateFidoSuccess = null;
            vm.registerFidoError = null;
            vm.registerFidoSuccess = null;
        }
    }
})();
