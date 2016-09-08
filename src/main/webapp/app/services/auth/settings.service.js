/**
 * Created by eugeniuparvan on 7/20/16.
 */
(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .factory('Settings', Settings);

    Settings.$inject = ['$resource'];

    function Settings($resource) {
        var service = $resource('api/openid/settings', {}, {
            'update': {
                method: 'PUT'
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
        });

        return service;
    }
})();

