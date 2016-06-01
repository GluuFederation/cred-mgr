'use strict';

describe('Controller Tests', function() {

    describe('OpenidServerConfiguration Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockOpenidServerConfiguration, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockOpenidServerConfiguration = jasmine.createSpy('MockOpenidServerConfiguration');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'OpenidServerConfiguration': MockOpenidServerConfiguration,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("OpenidServerConfigurationDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'credmgrApp:openidServerConfigurationUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
