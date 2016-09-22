/**
 * Created by eugeniuparvan on 5/30/16.
 */
(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('RegisterFIDOController', RegisterFIDOController);

    RegisterFIDOController.$inject = ['$uibModalInstance', '$state', 'fidoRegistrationResponse', 'Fido'];

    function RegisterFIDOController($uibModalInstance, $state, fidoRegistrationResponse, Fido) {
        var vm = this;

        setTimeout(register(fidoRegistrationResponse), 1000);

        function register(response) {
            var appId = response.appId;
            var registerRequests = [{version: response.version, challenge: response.challenge}];
            u2f.register(appId, registerRequests, [], function (data) {
                console.log("Register callback", data);
                if (data.errorCode) {
                    $state.go('reset-password', {
                        registerFidoSuccess: null,
                        registerFidoError: "ERROR"
                    }, {reload: true});
                    $uibModalInstance.dismiss('cancel');
                    return;
                }
                Fido.finishRegistration({value: angular.toJson(data)},
                    function () {
                        $state.go('reset-password', {
                            registerFidoSuccess: "SUCCESS",
                            registerFidoError: null
                        }, {reload: true});
                        $uibModalInstance.dismiss('cancel');
                    },
                    function () {
                        $state.go('reset-password', {
                            registerFidoSuccess: null,
                            registerFidoError: "ERROR"
                        }, {reload: true});
                        $uibModalInstance.dismiss('cancel');
                    }
                );
            });
        }

        vm.clear = function clear() {
            $uibModalInstance.dismiss('cancel');
            $state.go('reset-password', null, {reload: true});
        }
    }
})();
