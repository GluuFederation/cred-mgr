'use strict';

describe('Controller Tests', function() {

    describe('Gluu Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockGluu;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockGluu = jasmine.createSpy('MockGluu');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Gluu': MockGluu
            };
            createController = function() {
                $injector.get('$controller')("GluuDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'credmgrApp:gluuUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
