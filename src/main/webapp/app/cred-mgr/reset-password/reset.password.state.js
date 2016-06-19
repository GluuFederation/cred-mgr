/**
 * Created by eugeniuparvan on 5/30/16.
 */
(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('reset-password', {
                parent: 'home',
                url: 'reset-password/:host?',
                data: {
                    authorities: [],
                    pageTitle: 'Reset Password'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/cred-mgr/reset-password/reset.password.html',
                        controller: 'ResetPasswordController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('reset-password');
                        return $translate.refresh();
                    }]
                }
            });
    }

})();
