angular
  .module('Lib.Security', [])

  .factory('SessionService', ['$resource',
    function ($resource) {
      return $resource("/api/session?username=:username&password=:password", {username: '@username', password: '@password'}, {
        challenge: {method: 'POST'} /* login */
      });
    }
  ])

  .factory('PasswordService', ['$resource',
    function($resource){
      return $resource("api/password", {}, {
        forgot: {method: 'POST'},
        reset: {method: 'PUT'}
      });
    }]
  );
