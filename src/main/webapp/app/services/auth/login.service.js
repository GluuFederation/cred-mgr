/**
 * Created by eugeniuparvan on 6/5/16.
 */
(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .factory('Login', Login);

    Login.$inject = ['$resource'];

    function Login($resource) {
        var service = $resource('api/openid/login', {}, {
            'get': {method: 'GET', params: {}, isArray: false}
        });

        return service;
    }
})();
