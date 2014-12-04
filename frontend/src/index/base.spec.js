describe( 'Itsnow Index', function() {
  describe( 'loading', function() {
    var AppCtrl, $scope;

    beforeEach( module( 'Itsnow.Index' ) );

    beforeEach( inject( function( $controller, $rootScope ) {
      $scope = $rootScope.$new();
      AppCtrl = $controller( 'IndexCtrl', { $scope: $scope });
    }));

    it( 'should pass a dummy test', inject( function() {
      expect( AppCtrl ).toBeTruthy();
    }));
  });
});
