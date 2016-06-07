(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('NavbarController', NavbarController);

    NavbarController.$inject = ['$state', 'Auth', 'Principal', 'LogoutUri', 'ProfileService', 'LoginService'];

    function NavbarController($state, Auth, Principal, LogoutUri, ProfileService, LoginService) {
        var vm = this;

        vm.isNavbarCollapsed = true;
        vm.isAuthenticated = Principal.isAuthenticated;

        ProfileService.getProfileInfo().then(function(response) {
            vm.inProduction = response.inProduction;
            vm.swaggerDisabled = response.swaggerDisabled;
        });

        vm.login = login;
        vm.logout = logout;
        vm.toggleNavbar = toggleNavbar;
        vm.collapseNavbar = collapseNavbar;
        vm.$state = $state;
        vm.logoutError = true;
        vm.logoutUri = null;

        if (vm.isAuthenticated()) {
            LogoutUri.get({},
                function (response) {
                    vm.logoutUri = angular.fromJson(response).value;
                    vm.logoutError = false;
                },
                function (err) {
                    vm.logoutError = true;
                }.bind(this)
            ).$promise;
        }

        function login() {
            collapseNavbar();
            LoginService.open();
        }

        function logout() {
            if (vm.logoutError) return;
            collapseNavbar();
            Auth.logout();
            window.location = vm.logoutUri;
        }

        function toggleNavbar() {
            vm.isNavbarCollapsed = !vm.isNavbarCollapsed;
        }

        function collapseNavbar() {
            vm.isNavbarCollapsed = true;
        }
    }
})();
