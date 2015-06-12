/**
 * The forget module in Login
 */
angular.module( 'Login.Forgot', [])

  .config(function( $stateProvider ) {
    $stateProvider.state( 'forgot', {
      url: '/forgot',
      views: {
        "login": {
          controller: 'ForgotCtrl as forgot',
          templateUrl: 'forgot/forgot.tpl.html'
        }
      },
      data:{ pageTitle: '忘记密码' }
    });
  })

  .controller( 'ForgotCtrl', function ForgotCtrl( $scope, Password ) {
  })

;
