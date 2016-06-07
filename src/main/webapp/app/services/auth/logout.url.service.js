/**
 * Created by eugeniuparvan on 6/7/16.
 */
(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .factory('LogoutUri', LogoutUri);

    LogoutUri.$inject = ['$resource'];

    function LogoutUri($resource) {
        var service = $resource('api/openid/logout-uri', {}, {
            'get': {method: 'GET', params: {}, isArray: false}
        });

        return service;
    }
})();

