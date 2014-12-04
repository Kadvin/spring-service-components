angular.module('dnt.action.service', [
  'ui.router',
  'Lib.Feedback'
])
  .factory('ActionService', ['$rootScope', '$state', 'Feedback', \
                             ($rootScope,   $state,   feedback)->
    class ActionService
      CODE:
        TR_VALUE: "value"
        TOOLBAR_CSS: ".btn-toolbar"
      CSS:
        WEIGHT: "weight"
        REJECT_CSS: "reject-css"
        REQUIRE_CSS: "require-css"
      INFO:
        REJECT: "you must select {0} records to perform this action"
        REJECT_AT_LEAST: "you must select {0} records to perform this action at least"
        CHECK_STATUS: "you can't perform this action, please check the status of records which were selected"

      ### @function constructor | construct function
          @param: options | {watch: {}, mapping: function}###
      constructor: (@options)->
        instance = this
        instance.options.buttons = []

        $rootScope.$watchCollection ->
          return instance.options.watch
        , ->
          instance.options.buttons = $(instance.CODE.TOOLBAR_CSS).find("button[#{instance.CSS.WEIGHT}], a[#{instance.CSS.WEIGHT}]") if instance.options.buttons.length is 0
          instance.toggleButtons()

      ### @function: toggleButtons | toggle the disabled attribute  of buttons###
      toggleButtons: ->
        selections = @getSelections()
        for button in @options.buttons
          try
            @beforeExec button, selections
            $(button).removeAttr "disabled"
          catch error
            $(button).attr "disabled", "disabled"

      ### @function: beforeExec | check conditions before execute the action
          @param: element | the event of element
          @param: selections | selection datas###
      beforeExec: (element, selections)->
        conditions = @getConditions element
        @checkCondition conditions, selections

      ### @function: gotoState | redirect to another page
          @param: state | state of the page you want to redirect###
      gotoState: (state, event)->
        element = event.srcElement || event.target
        selections = @getSelections()
        try
          @beforeExec element, selections
          $state.go state, selections.datas[0]
        catch error
          feedback.warn "#{error}"

      ### @function: perform | perform the callback function
          @param: callback | the function you want to perform###
      perform: (callback, event)->
        element = event.srcElement || event.target
        selections = @getSelections()
        try
          @beforeExec element, selections
          if callback.length is 1
            callback item for item in selections.datas
            return
          return callback.apply this, selections.datas
        catch error
          feedback.warn "#{error}"

      ### @function: checkCondition | you can perform the action if the conditon is passed
          @param: conditions | button attributes: weight, reject-css, require-css
          @param: selections | contains the selected datas and elements of tr ###
      checkCondition: (conditions, selections)->
        if /^\d+$/.test conditions.weight
          if parseInt(conditions.weight) is selections.datas.length
              return @checkStatus(conditions, selections)
          throw "#{@INFO.REJECT}".interpolate(conditions.weight)

        if /^\d+\+$/.test conditions.weight
          if selections.datas.length >= parseInt(conditions.weight.split(/\+/)[0])
            return @checkStatus(conditions, selections)
          throw "#{@INFO.REJECT_AT_LEAST}".interpolate(conditions.weight)

      ### @function: checkStatus | used in isConditionPass function
          @param: conditions | button attributes: weight, reject-css, require-css
          @param: selections | contains the selected datas and elements of tr ###
      checkStatus: (conditions, selections)->
        throw "#{@INFO.CHECK_STATUS}" if !@isCssPass conditions, selections

      ### @function: isCssPass | were css passed
          @param: conditions | button attributes: weight, reject-css, require-css
          @param: selections | contains the selected datas and elements of tr
          @return: true|false | true: passed; false: not pass###
      isCssPass: (conditions, selections)->
        rejectCssJson = @getCss(conditions.rejectCss)
        requireCssJson = @getCss(conditions.requireCss)
        return false if @isReject(rejectCssJson, selections.trs)
        return @isRequire(requireCssJson, selections.trs)

      ### @function: isReject | were reject-css passed
          @param: rejectCssJson | reject-css
          @param: trs | selected elements of tr
          @return: true|false | true: rejected; false: passed###
      isReject: (rejectCssJson, trs)->
        for tr in trs
          trCssJson = @getClass(tr)
          return true for rejectCss of rejectCssJson when trCssJson[rejectCss]?
        return false

      ### @function: isRequire | were require-css passed
          @param: requireCssJson | require-css
          @param: trs | selected elements of tr
          @return: true|false | true: passed; false: not pass###
      isRequire: (requireCssJson, trs)->
        for tr in trs
          trCssJson = @getClass(tr)
          return false for requireCss of requireCssJson when !trCssJson[requireCss]?
        return true

      ### @function: getConditions | get attributes of button: weight, reject-css, require-css
          @param: element | button of toolbar
          @return: conditions###
      getConditions: (element)->
        conditions =
          weight: $(element).attr(@CSS.WEIGHT)
          rejectCss: $(element).attr(@CSS.REJECT_CSS)
          requireCss: $(element).attr(@CSS.REQUIRE_CSS)
        return conditions

      ### @function: getSelections | get selected datas and elements of tr
          @return: selections###
      getSelections: ->
        selections = {datas: [], trs: []}
        for key, val of @options.watch when val
          selections.datas.push @options.mapping(key)
          selections.trs.push $("[#{@CODE.TR_VALUE}=#{key}]")
        return selections

      ### @function: getCss | get css
          @param: classes | class of elements
          @return: json datas of css###
      getCss: (classes)->
        cssJson = {}
        if classes? and classes isnt '' then cssJson[css] = css for css in classes.split(/\s+/)
        return cssJson

      ### @function: getClass | get css of which selected elements of tr
          @param: tr | elements of tr
          @return: json datas of css###
      getClass: (tr)->
        return @getCss($(tr).attr("class"))

      ### @function: @make | instance a new ActionService
          @param: options | init params
          @return: instance of ActionService###
      @make = (options) ->
        return new ActionService(options)

  ])