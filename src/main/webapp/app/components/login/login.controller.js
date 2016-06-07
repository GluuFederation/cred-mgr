(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('LoginController', LoginController);

    LoginController.$inject = ['$rootScope', '$state', '$timeout', 'LoginUri', 'Auth', '$uibModalInstance'];

    function LoginController($rootScope, $state, $timeout, LoginUri, Auth, $uibModalInstance) {
        var vm = this;
        vm.companyShortName = null;
        vm.loginUri = null;
        vm.authenticationError = false;
        vm.cancel = cancel;
        vm.credentials = {};
        vm.login = login;
        vm.password = null;
        vm.register = register;
        vm.rememberMe = true;
        vm.requestResetPassword = requestResetPassword;
        vm.username = null;

        $timeout(function () {
            angular.element('#companyShortName').focus();
        });

        vm.getLoginUri = function getLoginUri() {
            LoginUri.get({companyShortName: vm.companyShortName}, function (response) {
                    vm.loginUri = angular.fromJson(response).value;
                    vm.authenticationError = false;
                    $timeout(function () {
                        angular.element('#loginBtn').focus();
                    });
                },
                function (err) {
                    vm.authenticationError = true;
                }.bind(this)).$promise;
        };

        function cancel() {
            vm.credentials = {
                username: null,
                password: null,
                rememberMe: true
            };
            vm.authenticationError = false;
            $uibModalInstance.dismiss('cancel');
        }

        function login(event) {
            if (!vm.authenticationError)
                window.location = vm.loginUri;
        }

        function register() {
            $uibModalInstance.dismiss('cancel');
            $state.go('register');
        }

        function requestResetPassword() {
            $uibModalInstance.dismiss('cancel');
            $state.go('requestReset');
        }
    }
})();
