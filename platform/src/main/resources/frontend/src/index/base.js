angular.module('Itsnow.Index', [
    'ui.router',
    'ngResource',
    'ngLocale',
    'ngSanitize',
    'ngTable',
    'dnt.action.service',
    'jcs-autoValidate',
    'Lib.Interceptor',
    'Lib.Directives',
    'Lib.Templates',
    'Index.Templates',
    'Index.Menu',
    'Index.Dialog',
    'Lib.JcsEnhance',
    'Lib.Filters',
    'Lib.Feedback',
    'Lib.Utils',
    'Lib.Commons'
])
  .config(function ($httpProvider, $stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise('dashboard');
        $stateProvider.state('dashboard', {
                url: '/dashboard',
                templateUrl: 'dashboard/dashboard.tpl.jade',
                data: {pageTitle: '总览'}
        });


  })

  // 登录/登出
  .factory('SessionService', ['$resource', function ($resource) {
      return $resource('/api/session', null, {
          logout: {method: 'DELETE'}
      });
  }])

  // 获取用户信息工厂
  .factory('ProfileService', ['$resource', function ($resource) {
      return $resource('/api/profile', null, {
          get: {method: 'GET'}
      });
  }])

  .controller('IndexCtrl', ['$rootScope', '$scope', '$state',  'SessionService', '$window',
    function ($rootScope, $scope, $state, sessionService, $window) {
      $rootScope.system = window.system;
      $scope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams){
        // Record current breadcrumbs
        var breadcrumb = toState;
        var breadcrumbs = [breadcrumb];
        var last = breadcrumb.name.lastIndexOf('.');
        var pageTitle = (breadcrumb.data || {pageTitle: "未命名"}).pageTitle;
        while(last > 0 ){
          breadcrumb = $state.get(breadcrumb.name.substring(0, last));
          breadcrumbs.push(breadcrumb);
          pageTitle = pageTitle + " | " + (breadcrumb.data || {pageTitle: "未命名"}).pageTitle;
          last = breadcrumb.name.lastIndexOf('.');
        }
        $rootScope.pageTitle = pageTitle;
        $rootScope.breadcrumbs = breadcrumbs.reverse();
      });
      $scope.logout = function(){
        sessionService.logout(function(){
          $window.location.href='/login.html';
        });
      };
  }])

  // 获取用户信息
  .run(['$rootScope', 'ProfileService',
    function ($rootScope, profileService) {
      profileService.get(function (data) {
          $rootScope.user = data;
      });
  }])

  // angular-auto-validate error message
  .run(['defaultErrorMessageResolver', 'validator', 'AceElementModifier',
    function (defaultErrorMessageResolver, validator, aceElementModifier) {
      defaultErrorMessageResolver.setI18nFileRootPath('assets/json');
      defaultErrorMessageResolver.setCulture('zh-CN');
      validator.registerDomModifier(aceElementModifier.key, aceElementModifier);
      validator.setDefaultElementModifier(aceElementModifier.key);
    }
  ]);
