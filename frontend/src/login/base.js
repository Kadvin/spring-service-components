/**
 * The shared login Application(SPA)
 */
angular.module("Itsnow.Login", [
    'ngResource',
    'ngLocale',
    'ui.router',
    'jcs-autoValidate',

    'Lib.Feedback',
    'Lib.Commons',
    'Lib.Utils',
    'Lib.Interceptor',
    'Lib.Templates',
    'Lib.Directives',
    'Lib.JcsEnhance',
    'Login.Templates',
    'Itsnow.Security',
    'Login.Authenticate',
    'Login.Forgot'
])

  .config(function($stateProvider, $urlRouterProvider){
    $urlRouterProvider.otherwise("/authenticate");
  })

  .controller('LoginCtrl', ['$rootScope', '$scope', function($rootScope, $scope){
    $rootScope.system = window.system;
    $scope.$on('$stateChangeSuccess', function(evt, toState){
        if ( angular.isDefined( toState.data.pageTitle ) ) {
            $rootScope.pageTitle = toState.data.pageTitle + ' | ItsNow' ;
        }
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

