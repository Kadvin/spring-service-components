angular.module('Lib.Feedback', [])
  .factory 'Feedback', ['$rootScope', '$timeout', ($rootScope, $timeout)->
    $rootScope.feedback = {klass: '', message: '' }
    # don't use data-dismiss
    $('.close').click (e)->
      $(e.target).closest(".alert").hide()
    {
      #
      # 向用户反馈操作结果
      #
      # message: 反馈的内容，可以包括html信息
      # options 包括如下选项
      #   level:   反馈的信息类型 0: success, 1: info, 2: primary, -1: warning, -2: danger
      #   dismiss: 反馈信息如何自动消失 0: 不会自动消失，需要用户手工关闭， >0 若干秒之后自动消失
      # level的默认值为 1(info)
      # dismiss的默认值为 10
      feedback: (message, options) ->
        klass = switch options.level || 2
          when 1 then "bg-success"
          when 2 then "bg-info"
          when 3 then "bg-primary"
          when -1 then "bg-warning"
          when -2 then "bg-danger"
          else "bg-info"
        dismiss = options.dismiss || 10
        $rootScope.feedback =
          message: message
          klass: klass
          dismiss: dismiss
        # ng bind with $rootScope variable is not work
        try
          html = angular.element(message)
          html = message if html.length == 0
        catch
          html = message

        $("#feedback_content").html(html)
        $("#feedback").removeClass("bg-success bg-info bg-primary bg-warning bg-danger")
        $("#feedback").addClass(klass)
        $("#feedback").show()

        dismissFeedback = ()->
         $("#feedback").hide()
        $timeout(dismissFeedback, dismiss * 1000) if dismiss > 0

      success: (message, dismiss) ->
        this.feedback(message, {level: 1, dismiss: dismiss})
      info: (message, dismiss) ->
        this.feedback(message, {level: 2, dismiss: dismiss})
      primary: (message, dismiss) ->
        this.feedback(message, {level: 3, dismiss: dismiss})
      warn: (message, dismiss) ->
        this.feedback(message, {level: -1, dismiss: dismiss})
      error: (message, resp, dismiss) ->
        try
          extra = JSON.parse resp.data
        catch
          extra = resp.statusText
        this.feedback(message + ", 错误原因：" + extra, {level: -2, dismiss: dismiss})
    }
  ]