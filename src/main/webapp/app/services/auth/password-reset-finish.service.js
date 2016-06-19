(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .factory('PasswordResetFinish', PasswordResetFinish);

    PasswordResetFinish.$inject = ['$resource'];

    function PasswordResetFinish($resource) {
        var service = $resource('api/openid/reset_password/finish', {}, {});

        return service;
    }
})();
