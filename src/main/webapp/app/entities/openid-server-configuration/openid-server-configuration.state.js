(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('openid-server-configuration', {
                parent: 'entity',
                url: '/openid-server-configuration',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'credmgrApp.openidServerConfiguration.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/openid-server-configuration/openid-server-configurations.html',
                        controller: 'OpenidServerConfigurationController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('openidServerConfiguration');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('openid-server-configuration-detail', {
                parent: 'entity',
                url: '/openid-server-configuration/{id}',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'credmgrApp.openidServerConfiguration.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/openid-server-configuration/openid-server-configuration-detail.html',
                        controller: 'OpenidServerConfigurationDetailController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('openidServerConfiguration');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'OpenidServerConfiguration', function ($stateParams, OpenidServerConfiguration) {
                        return OpenidServerConfiguration.get({id: $stateParams.id}).$promise;
                    }]
                }
            })
            .state('openid-server-configuration.new', {
                parent: 'openid-server-configuration',
                url: '/new',
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/openid-server-configuration/openid-server-configuration-dialog.html',
                        controller: 'OpenidServerConfigurationDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    host: null,
                                    clientId: null,
                                    clientJWKS: null,
                                    enableAdminPage: null,
                                    authenticationLevel: null,
                                    requiredOpenIdScope: null,
                                    requiredClaim: null,
                                    requiredClaimValue: null,
                                    enablePasswordManagement: null,
                                    enableEmailManagement: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function () {
                        $state.go('openid-server-configuration', null, {reload: true});
                    }, function () {
                        $state.go('openid-server-configuration');
                    });
                }]
            })
            .state('openid-server-configuration.edit', {
                parent: 'openid-server-configuration',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/openid-server-configuration/openid-server-configuration-dialog.html',
                        controller: 'OpenidServerConfigurationDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['OpenidServerConfiguration', function (OpenidServerConfiguration) {
                                return OpenidServerConfiguration.get({id: $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function () {
                        $state.go('openid-server-configuration', null, {reload: true});
                    }, function () {
                        $state.go('^');
                    });
                }]
            })
            .state('openid-server-configuration.delete', {
                parent: 'openid-server-configuration',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/openid-server-configuration/openid-server-configuration-delete-dialog.html',
                        controller: 'OpenidServerConfigurationDeleteController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            entity: ['OpenidServerConfiguration', function (OpenidServerConfiguration) {
                                return OpenidServerConfiguration.get({id: $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function () {
                        $state.go('openid-server-configuration', null, {reload: true});
                    }, function () {
                        $state.go('^');
                    });
                }]
            });
    }

})();
