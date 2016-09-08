(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('SettingsController', SettingsController);

    SettingsController.$inject = ['$scope', 'Principal', 'Settings', '$window'];

    function SettingsController($scope, Principal, Settings, $window) {
        var vm = this;

        vm.error = null;
        vm.save = save;
        vm.login = null;
        vm.success = null;
        vm.opConfig = {};
        vm.jks = {};

        Principal.identity().then(function (account) {
            Settings.get(
                function (response) {
                    vm.opConfig = response;
                },
                function (data) {
                    vm.opConfig = {};
                }
            );
            vm.login = account.login;
        });
        $scope.$watch('vm.jks', function () {
            if (vm.jks.name != undefined && vm.jks.name != null)
                vm.opConfig.clientJKS = vm.jks.name;
        });

        function save() {
            Settings.update(vm.opConfig,
                function (response) {
                    vm.error = null;
                    vm.success = 'OK';
                    $window.scrollTo(0, 0);
                },
                function (data) {
                    vm.success = null;
                    vm.error = 'ERROR';
                    $window.scrollTo(0, 0);
                }
            );
        }
    }
})();
