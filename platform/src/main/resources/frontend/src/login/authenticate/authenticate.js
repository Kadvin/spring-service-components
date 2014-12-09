/**
 * The authenticate module in Login
 */
angular
  .module('Login.Authenticate', [])

  .config(function ($stateProvider) {
    $stateProvider.state('authenticate', {
      url: '/authenticate',
      views: {
        "login": {
          controller: 'AuthenticateCtrl as authenticate',
          templateUrl: 'authenticate/authenticate.tpl.jade'
        }
      },
      data: { pageTitle: '系统登录' }
    });
  })

  .controller('AuthenticateCtrl', ['$scope', '$http', 'Feedback', 'SessionService',
    function ($scope, $http, Feedback, sessionService) {
      $scope.credential = {username: window.system.defaultUser, password: window.system.defaultPassword, remember: true};

      $scope.challenge = function () {
        sessionService.challenge($scope.credential, function(){
          window.location.href = '/index.html';
        }, function(resp){
          $scope.error = resp.statusText;
          Feedback.error("登录失败", resp);
        });
      };
    }]);


