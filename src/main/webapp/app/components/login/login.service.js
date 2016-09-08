(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .factory('LoginService', LoginService);

    LoginService.$inject = ['LoginUri'];

    function LoginService(LoginUri) {
        var service = {
            open: open
        };
        return service;

        function open() {
            return LoginUri.get({},
                function (response) {
                    window.location = angular.fromJson(response).value;
                }.bind(this)
            ).$promise;
        }
    }
})();
