(function() {
    'use strict';
    angular
        .module('credmgrApp')
        .factory('OpenidServerConfiguration', OpenidServerConfiguration);

    OpenidServerConfiguration.$inject = ['$resource'];

    function OpenidServerConfiguration ($resource) {
        var resourceUrl =  'api/openid-server-configurations/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
