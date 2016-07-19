/**
 * Created by eugeniuparvan on 7/19/16.
 */
(function () {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('ErrorController', ErrorController);

    ErrorController.$inject = ['$scope', '$location'];

    function ErrorController($scope, $location) {
        var vm = this;
        vm.errorMessage = $location.search().detailMessage;
    }
})();
