String::interpolate = ()->
  return this if arguments.length is 0
  string = this
  for val, i in arguments when i >= 0
    regx = new RegExp "\\{#{i}\\}", "gm"
    string = string.replace regx, val
  return string
Array::remove = (callback) ->
  x = $.grep @, (e)-> callback(e)
  @pop x

angular.module('Lib.Utils', [])
  .factory('Utils', [->
    {
      ### @function: stringFormat | format the string
        @demo: stringFormat("i have {0} {1}", "two", "apples")
          print: i have two apples
        @return: format string###
      stringFormat: ->
        return null if arguments.length is 0
        string = arguments[0]
        for val, i in arguments when i isnt 0
          regx = new RegExp "\\{#{i - 1}\\}", "gm"
          string = string.replace regx, val
        return string

      isEmpty: (obj) ->
        for name of obj
          return false
        true
      isNotEmpty: (obj) ->
        !@isEmpty(obj)
    }
  ])
