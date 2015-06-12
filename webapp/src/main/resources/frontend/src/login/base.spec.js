/**
 * Created by kadvin on 14-7-2.
 */
describe('Platform Login', function () {
    describe('loading', function () {
        var ctrl, scope;

        beforeEach(
            module('Platform.Login')
        );

        beforeEach(inject(function ($controller, $rootScope) {
          scope = $rootScope.$new();
          ctrl = $controller('LoginCtrl', {$scope: scope});
        }));

        it('should pass a dummy test', function () {
            expect(scope).toBeDefined();
        });
    });
});

