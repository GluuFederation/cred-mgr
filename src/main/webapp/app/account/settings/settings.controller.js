(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('SettingsController', SettingsController);

    SettingsController.$inject = ['Principal', 'Auth', 'OPConfig', 'JhiLanguageService', '$window', '$translate'];

    function SettingsController(Principal, Auth, OPConfig, JhiLanguageService, $window, $translate) {
        var vm = this;

        vm.error = null;
        vm.save = save;
        vm.login = null;
        vm.success = null;
        vm.opConfig = {};

        Principal.identity().then(function(account) {
            OPConfig.get({id: account.opConfigId}, function (opConfig) {
                vm.opConfig = opConfig;
            }, function (error) {
                vm.opConfig = {};
            });
            vm.login = account.login;
        });

        function save () {
            OPConfig.update(vm.opConfig, function () {
                vm.error = null;
                vm.success = 'OK';
                $window.scrollTo(0, 0);
            }, function () {
                vm.success = null;
                vm.error = 'ERROR';
                $window.scrollTo(0, 0);
            });
        }
    }
})();
