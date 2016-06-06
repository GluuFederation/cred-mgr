/**
 * Created by eugeniuparvan on 6/5/16.
 */
(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .factory('LoginUri', LoginUri);

    LoginUri.$inject = ['$resource'];

    function LoginUri($resource) {
        var service = $resource('api/openid/login-uri', {}, {
            'get': {method: 'GET', params: {}, isArray: false}
        });

        return service;
    }
})();
