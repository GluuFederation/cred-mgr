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
                params: {
                    registerFidoError: null,
                    registerFidoSuccess: null
                },
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
                    }],
                    registerFidoError: ['$stateParams', function ($stateParams) {
                        return $stateParams.registerFidoError;
                    }],
                    registerFidoSuccess: ['$stateParams', function ($stateParams) {
                        return $stateParams.registerFidoSuccess;
                    }]
                }
            })
            .state('reset-password.fido', {
                parent: 'reset-password',
                params: {
                    fidoRegistrationResponse: null
                },
                data: {
                    authorities: ['OP_USER', 'OP_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/cred-mgr/reset-password/register.fido.device.html',
                        controller: 'RegisterFIDOController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                                $translatePartialLoader.addPart('reset-password');
                                return $translate.refresh();
                            }],
                            fidoRegistrationResponse: $stateParams.fidoRegistrationResponse
                        }
                    });
                }]
            });
    }

})();
