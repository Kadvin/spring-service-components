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
      uuid : (len, radix) ->
        chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".split("")
        uuid = []
        i = undefined
        radix = radix or chars.length
        if len

          # Compact form
          i = 0
          while i < len
            uuid[i] = chars[0 | Math.random() * radix]
            i++
        else

          # rfc4122, version 4 form
          r = undefined

          # rfc4122 requires these characters
          uuid[8] = uuid[13] = uuid[18] = uuid[23] = "-"
          uuid[14] = "4"

          # Fill in random data.  At i==19 set the high bits of clock sequence as
          # per rfc4122, sec. 4.1.5
          i = 0
          while i < 36
            unless uuid[i]
              r = 0 | Math.random() * 16
              uuid[i] = chars[(if (i is 19) then (r & 0x3) | 0x8 else r)]
            i++
        uuid.join ""
    }
  ])
