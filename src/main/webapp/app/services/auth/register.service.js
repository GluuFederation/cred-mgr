(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .factory('Register', Register);

    Register.$inject = ['$resource'];

    function Register ($resource) {
        return $resource('api/openid/register', {}, {});
    }
})();
