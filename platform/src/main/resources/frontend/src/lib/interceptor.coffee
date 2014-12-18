# CSRF拦截器
angular.module('Lib.Interceptor', ['ngResource'])

.factory('CSRFService', ['$resource', ($resource)->
    $resource 'security/csrf',
      get: {method: 'GET'}
  ])

# 拦截器（拦截POST|PUT|DELETE请求）
.factory('SessionInjector', ['$injector', '$q', '$rootScope',\
    ($injector, $q, $rootScope) ->
      request: (config) ->
        $rootScope.loading = true
        if config.method is 'POST' || config.method is 'PUT' || config.method is 'DELETE'
          csrfService = $injector.get 'CSRFService'
          csrfService.get().$promise
          .then (data) ->
            config.headers[data.headerName] = data.token
            $q.when config
          , (resp) ->
            $q.reject resp
        else
          $q.when config
      requestError: (rejection) ->
        $rootScope.loading = true
        $q.reject rejection
      response: (response) ->
        $rootScope.loading = false
        response
      responseError: (rejection) ->
        $rootScope.loading = false
        $q.reject rejection
  ])

# 注入拦截器
.config(['$httpProvider', ($httpProvider) ->
    $httpProvider.interceptors.push 'SessionInjector'
  ])
