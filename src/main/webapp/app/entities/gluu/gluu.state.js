(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('gluu', {
            parent: 'entity',
            url: '/gluu',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'credmgrApp.gluu.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/gluu/gluus.html',
                    controller: 'GluuController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('gluu');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('gluu-detail', {
            parent: 'entity',
            url: '/gluu/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'credmgrApp.gluu.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/gluu/gluu-detail.html',
                    controller: 'GluuDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('gluu');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Gluu', function($stateParams, Gluu) {
                    return Gluu.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('gluu.new', {
            parent: 'gluu',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/gluu/gluu-dialog.html',
                    controller: 'GluuDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                host: null,
                                clientId: null,
                                clientSecret: null,
                                loginRedirectUri: null,
                                logoutRedirectUri: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('gluu', null, { reload: true });
                }, function() {
                    $state.go('gluu');
                });
            }]
        })
        .state('gluu.edit', {
            parent: 'gluu',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/gluu/gluu-dialog.html',
                    controller: 'GluuDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Gluu', function(Gluu) {
                            return Gluu.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('gluu', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('gluu.delete', {
            parent: 'gluu',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/gluu/gluu-delete-dialog.html',
                    controller: 'GluuDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Gluu', function(Gluu) {
                            return Gluu.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('gluu', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
