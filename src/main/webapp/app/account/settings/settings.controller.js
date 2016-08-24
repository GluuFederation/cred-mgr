(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('SettingsController', SettingsController);

    SettingsController.$inject = ['Upload', '$scope', 'Principal', 'Auth', 'OPConfig', 'Settings', 'JhiLanguageService', '$window', '$translate'];

    function SettingsController(Upload, $scope, Principal, Auth, OPConfig, Settings, JhiLanguageService, $window, $translate) {
        var vm = this;

        vm.error = null;
        vm.save = save;
        vm.login = null;
        vm.success = null;
        vm.opConfig = {};
        vm.jks = {};

        Principal.identity().then(function(account) {
            OPConfig.get({id: account.opConfigId}, function (opConfig) {
                vm.opConfig = opConfig;
            }, function (error) {
                vm.opConfig = {};
            });
            vm.login = account.login;
        });
        $scope.$watch('vm.jks', function () {
            if (vm.jks.name != undefined && vm.jks.name != null)
                vm.opConfig.clientJKS = vm.jks.name;
        });

        function save () {
            Upload.upload({
                url: 'api/openid/settings-update',
                data: JSON.parse(angular.toJson(vm.opConfig)),
                file: vm.jks
            }).progress(function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                console.log('progress: ' + progressPercentage + '% ' + evt.config.file.name);
            }).success(function (data, status, headers, config) {
                vm.error = null;
                vm.success = 'OK';
                $window.scrollTo(0, 0);
                console.log('file ' + config.file.name + 'uploaded. Response: ' + data);
            }).error(function () {
                vm.success = null;
                vm.error = 'ERROR';
                $window.scrollTo(0, 0);
            });
        }
    }
})();
