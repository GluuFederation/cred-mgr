 'use strict';

angular.module('credmgrApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-credmgrApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-credmgrApp-params')});
                }
                return response;
            }
        };
    });
