
angular.module('Lib.Directives', ['Lib.Utils'])
  .directive('pwCheck', [->
    {
      require: 'ngModel',
  		link: (scope, elem, attrs, ctrl) ->
  			firstPassword = '#' + attrs.pwCheck;
  			elem.add(firstPassword).on 'keyup', ->
  				scope.$apply ->
  					v = elem.val() == $(firstPassword).val();
  					ctrl.$setValidity('pwmatch', v);
    }
  ])
  .directive('ngCheck', ['$http', 'Utils', (async, Utils) ->
    {
      require: 'ngModel',
      scope: {
        callback : "=callback"
      }
      link: (scope, elem, attrs, ctrl)->
        elem.on 'blur', () ->
          scope.$apply ->
            errType = 'unique'
            errType = attrs.errType if attrs.errType?
            val = elem.val()
            return ctrl.$setValidity(errType, true) if !val? or val is ''
            # GET /public/accounts/check/{0}/name
            # GET /public/accounts/check/domain/$accountDomain
            # GET /public/users/check/username/$username
            # GET /public/users/check/email/$email
            # GET /public/users/check/phone/$phone
            async.get(Utils.stringFormat attrs.ngCheck, val).success((data)->
              ctrl.$setValidity(errType, true);
              scope.callback(data) if scope.callback?
            ).error(->
              ctrl.$setValidity(errType, false)
            )
    }
  ])
  .directive('itsnowFileCheck', ->
    require: 'ngModel'
    link: (scope, elem, attrs, ctrl)->
      scope.$watch "workflow", ->
        if(scope.selectedFiles.length <= 0)
          return ctrl.$setValidity("emptyFile", false)
        if(scope.selectedFiles[0].name.indexOf('.bpmn20.xml') < 0)
          return ctrl.$setValidity("fileType", false)
        if(scope.selectedFiles[0].size > 1048576)
          return ctrl.$setValidity("fileSize", false)
        return ctrl.$setValidity("emptyFile", true)
  )
