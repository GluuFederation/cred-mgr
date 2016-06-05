(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('o-p-config', {
                parent: 'entity',
                url: '/o-p-config',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'credmgrApp.oPConfig.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/o-p-config/o-p-configs.html',
                        controller: 'OPConfigController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('oPConfig');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('o-p-config-detail', {
                parent: 'entity',
                url: '/o-p-config/{id}',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'credmgrApp.oPConfig.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/o-p-config/o-p-config-detail.html',
                        controller: 'OPConfigDetailController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('oPConfig');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'OPConfig', function ($stateParams, OPConfig) {
                        return OPConfig.get({id: $stateParams.id}).$promise;
                    }]
                }
            })
            .state('o-p-config.new', {
                parent: 'o-p-config',
                url: '/new',
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/o-p-config/o-p-config-dialog.html',
                        controller: 'OPConfigDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    inum: null,
                                    companyName: null,
                                    companyShortName: null,
                                    host: null,
                                    clientId: null,
                                    clientJWKS: null,
                                    authenticationLevel: null,
                                    requiredOpenIdScope: null,
                                    requiredClaim: null,
                                    requiredClaimValue: null,
                                    enablePasswordManagement: null,
                                    enableAdminPage: null,
                                    enableEmailManagement: null,
                                    activationKey: null,
                                    email: null,
                                    activated: false,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function () {
                        $state.go('o-p-config', null, {reload: true});
                    }, function () {
                        $state.go('o-p-config');
                    });
                }]
            })
            .state('o-p-config.edit', {
                parent: 'o-p-config',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/o-p-config/o-p-config-dialog.html',
                        controller: 'OPConfigDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['OPConfig', function (OPConfig) {
                                return OPConfig.get({id: $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function () {
                        $state.go('o-p-config', null, {reload: true});
                    }, function () {
                        $state.go('^');
                    });
                }]
            })
            .state('o-p-config.delete', {
                parent: 'o-p-config',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/o-p-config/o-p-config-delete-dialog.html',
                        controller: 'OPConfigDeleteController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            entity: ['OPConfig', function (OPConfig) {
                                return OPConfig.get({id: $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function () {
                        $state.go('o-p-config', null, {reload: true});
                    }, function () {
                        $state.go('^');
                    });
                }]
            });
    }

})();
