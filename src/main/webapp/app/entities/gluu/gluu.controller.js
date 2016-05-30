(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .controller('GluuController', GluuController);

    GluuController.$inject = ['$scope', '$state', 'Gluu'];

    function GluuController ($scope, $state, Gluu) {
        var vm = this;
        
        vm.gluus = [];

        loadAll();

        function loadAll() {
            Gluu.query(function(result) {
                vm.gluus = result;
            });
        }
    }
})();
