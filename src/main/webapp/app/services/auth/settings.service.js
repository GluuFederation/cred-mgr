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
        var service = $resource('api/openid/settings-update', {}, {
            'update': {
                method: 'PUT',
                headers: {'Content-Type': undefined, enctype: 'multipart/form-data'},
                params: {},
                isArray: false
            }
        });

        return service;
    }
})();

