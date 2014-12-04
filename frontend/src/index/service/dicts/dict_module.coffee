angular.module('Service.Dict', [])
.config ($stateProvider, $urlRouterProvider)->
  $stateProvider.state 'dicts',
    url: '/dicts',
    abstract: true,
    templateUrl: 'service/dicts/index.tpl.jade',
    controller: 'DictsCtrl',
    data: {pageTitle: '字典管理', default: 'dicts.list'}
  $stateProvider.state 'dicts.list',
    url: '/list',
    templateUrl: 'service/dicts/list.tpl.jade'
    controller: 'DictsListCtrl',
    data: {pageTitle: '字典列表'}
  $stateProvider.state 'dicts.new',
    url: '/new',
    templateUrl: 'service/dicts/new.tpl.jade'
    controller: 'DictsNewCtrl',
    data: {pageTitle: '新增字典'}
  $stateProvider.state 'dicts.view',
    url: '/{code}',
    templateUrl: 'service/dicts/view.tpl.jade'
    controller: 'DictsViewCtrl',
    data: {pageTitle: '查看字典'}
  $stateProvider.state 'dicts.edit',
    url: '/{code}/edit',
    templateUrl: 'service/dicts/edit.tpl.jade'
    controller: 'DictsEditCtrl',
    data: {pageTitle: '编辑字典'}
  $urlRouterProvider.when '/dicts', '/dicts/list'

.filter('stateFilter', () ->
  (input) ->
    return "有效" if input is "1"
    return "无效"
)
.filter('formatTime', ->
  (time) ->
    date = new Date(time)
    return date.toLocaleString()
)
.filter('detailFilter', ->
  (input) ->
    names=[]
    names.push detail.key for detail in input if input?
    names.join()
)

.factory('DictService', ['$resource', ($resource) ->
    $resource("/api/dictionaries/:code", {},
      query: { method: 'GET', params: {keyword: '@keyword'}, isArray: true},
      get: { method: 'GET', params: {name: '@code'}},
      save: { method: 'POST',params:{code:''}},
      update: { method: 'PUT', params: {name: '@code'}},
      remove: { method: 'DELETE', params: {code: '@code'}},
    )
  ])




.controller('DictsCtrl', ['$scope', '$resource', '$state', 'DictService', 'Feedback', 'CacheService', \
                          ($scope,   $resource,   $state,   dictService,   feedback,   CacheService) ->
#      $scope.statedatas = [
#        {name: "无效", value: 0},
#        {name: "有效", value: 1}
#      ]
#      $scope.options = {page: 1, count: 10}

      $scope.options =
        page: 1   # show first page
        count: 10 # count per page

      $scope.cacheService = new CacheService "code", (value)->
        dictService.get {name:value}

#      $scope.cacheService = new CacheService "code"
#      $scope.services = $resource("/api/dictionaries/:code", {sn:'@code'},
#        save: { method: 'POST',params:{code:''}},
#        update: { method: 'PUT', params: {code: 'code'}}
#      )

  ])
.controller('DictsListCtrl', ['$scope', '$location', 'ngTableParams', 'DictService', 'ActionService', 'SelectionService', 'CommonService', 'Feedback',\
                             ( $scope,   $location,   NgTable,         dictService,   ActionService,   SelectionService,   commonService,   feedback) ->

      args =
        total: 0
        getData: ($defer, params)->
          $location.search params.url() # put params in url
          dictService.query params.url(), (data, headers) ->
            params.total headers('total')
            $scope.cacheService.cache data
            $defer.resolve $scope.dicts = data
#          $scope.services.query params.url(), (datas, headers) ->
#            params.total headers 'total'
#            $defer.resolve $scope.dicts = datas;
#            $scope.cacheService.cache datas

      $scope.dictsTable = new NgTable(angular.extend($scope.options, $location.search()), args)
      $scope.selectionService = new SelectionService($scope.cacheService.records, "code")
      $scope.actionService = new ActionService({watch: $scope.selectionService.items, mapping: $scope.cacheService.find})

      $scope.destroy = (dict) ->
        dictService.remove {code: dict.code}, () ->
          feedback.success "删除字典#{dict.code}成功"
          delete $scope.selectionService.items[dict.code]
          $scope.dictsTable.reload()
        , (resp) ->
          feedback.error("删除字典#{dict.code}失败", resp)

#      $scope.selection = {checked: false, items: {}}
#      $scope.dictsTable = new NgTable(angular.extend($scope.options, $location.search()), args);
#      commonService.watchSelection($scope.selection, $scope.cacheService.records, "code")
#      $scope.reload = ->
#        $scope.dictsTable.reload()
#      $scope.actionService = new ActionService {watch: $scope.selection.items, mapping: $scope.cacheService.find}

#      $scope.destroy = (dict)->
#        $scope.Destroy dict, ->
#          delete $scope.selection.items[dict.code]
#          $scope.reload()
  ])
##--------------------------------------------------------------------
.controller('DictsNewCtrl', ['$scope', '$state', 'Feedback','DictService',\
                            ( $scope,   $state,   feedback, dictService) ->
      $scope.createview = true
      $scope.updateview = false
      $scope.create = ->
##-----------------------------------------------------
        details = []
        keyArray = $("input[name='keyInput']")
        valueArray = $("input[name='valueInput']")
        for input,index in keyArray
          detail = {}
          detail.key = input.value
          detail.value = valueArray[index].value
          details.push detail

        $scope.dict.details=details

        dictService.save $scope.dict, ->
          feedback.success "新建#{$scope.dict.name}成功"
          $state.go "dicts.list"
        , (resp) ->
          feedback.error("新建角色#{$scope.dict.name}失败", resp)
  ])

##------------------------------------------------------------------------------
.controller('DictsEditCtrl', ['$scope', '$state', '$stateParams', 'Feedback','DictService',\
                             ( $scope,   $state,   $stateParams,   feedback,  dictService) ->
      code = $stateParams.code
      dictService.get
        code: code
      , (data) ->
        $scope.dict = data
        showAdd(data)

      $scope.createview = false
      $scope.updateview = true
      $scope.update = () ->

        details = []
        keyArray = $("input[name='keyInput']")
        valueArray = $("input[name='valueInput']")
        for input,index in keyArray
         detail = {}
         detail.key = input.value
         detail.value = valueArray[index].value
         details.push detail

        $scope.dict.details=details
        delete $scope.dict.$promise
        delete $scope.dict.$resolved

        dictService.update {code: code}, $scope.dict, () ->
          feedback.success "修改#{$scope.dict.name}成功"
          $state.go "dicts.list"
        , (resp) ->
          feedback.error("修改#{$scope.dict.code}失败", resp);
  ])
.controller('DictsViewCtrl', ['$scope', '$state', '$stateParams', '$filter', 'Feedback','DictService',\
                             ($scope,    $state,   $stateParams, $filter,  feedback,  dictService) ->
      code = $stateParams.code
      dictService.get
        code: code
      , (data) ->
        $scope.dict = data
        $scope.dict.createdAtFMT = $filter('formatTime')($scope.dict.createdAt)
        showAdd(data)
  ])