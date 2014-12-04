/**
* Created by kadvin on 14-7-9.
*/
describe( 'Login Authenticate', function() {
  describe( 'loading', function() {
    var AuthenticateCtrl, $location, $scope, mockSessionService;

    beforeEach( module( 'Login.Authenticate' ) );

    beforeEach( inject( function( $controller, _$location_, $rootScope ) {
      $location = _$location_;
      $scope = $rootScope.$new();
      mockSessionService = {};
      AuthenticateCtrl = $controller( 'AuthenticateCtrl', { $location: $location, $scope: $scope, $rootScope:$rootScope, SessionService: mockSessionService});
    }));

    it( 'should pass a dummy test', inject( function() {
      expect( AuthenticateCtrl ).toBeTruthy();
    }));
  });
});
