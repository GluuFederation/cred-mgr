/**
 * Created by eugeniuparvan on 5/30/16.
 */
(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('ResetPasswordController', ResetPasswordController);

    ResetPasswordController.$inject = ['$scope', '$state'];

    function ResetPasswordController($scope, $state) {
        var ctrl = this;
    }
})();
