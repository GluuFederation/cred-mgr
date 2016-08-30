/**
 * Created by eugeniuparvan on 8/30/16.
 */
(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .factory('Fido', Fido);

    Fido.$inject = ['$resource'];

    function Fido($resource) {
        var service = $resource('api/openid/fido/unregister', {});
        return service;
    }
})();
