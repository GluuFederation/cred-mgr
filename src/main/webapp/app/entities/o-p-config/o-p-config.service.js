(function() {
    'use strict';
    angular
        .module('credmgrApp')
        .factory('OPConfig', OPConfig);

    OPConfig.$inject = ['$resource'];

    function OPConfig ($resource) {
        var resourceUrl =  'api/o-p-configs/:id';

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
