(function() {
    'use strict';
    angular
        .module('credmgrApp')
        .factory('Gluu', Gluu);

    Gluu.$inject = ['$resource'];

    function Gluu ($resource) {
        var resourceUrl =  'api/gluus/:id';

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
