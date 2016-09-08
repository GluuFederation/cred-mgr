/**
 * Created by eugeniuparvan on 9/8/16.
 */
(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .factory('ResetOptions', ResetOptions);

    ResetOptions.$inject = ['$resource'];

    function ResetOptions($resource) {
        var service = $resource('api/openid/reset/options', {}, {
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            }
        });

        return service;
    }
})();
