(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .factory('PasswordResetInit', PasswordResetInit);

    PasswordResetInit.$inject = ['$resource'];

    function PasswordResetInit($resource) {
        var service = $resource('api/openid/reset_password/init', {}, {});

        return service;
    }
})();
