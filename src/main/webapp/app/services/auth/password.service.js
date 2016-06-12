(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .factory('Password', Password);

    Password.$inject = ['$resource'];

    function Password($resource) {
        var service = $resource('api/openid/change_password', {}, {});

        return service;
    }
})();
