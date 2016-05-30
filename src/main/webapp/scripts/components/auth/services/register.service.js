'use strict';

angular.module('credmgrApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


