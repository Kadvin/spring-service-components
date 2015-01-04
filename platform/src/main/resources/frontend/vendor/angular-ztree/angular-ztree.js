angular.module('ng-ztree', ['ng'])

  .directive('ngZtree', function () {
    return {
      require: '?ngModel',
      restrict: 'EA',
      replace: true,
      scope: {
        ngModel: "=", // 被选中的数据存在这里
        treeModel: "=", // 展示的树模型
        type: "@", // radio or checkbox
        nodeName: "@", // 节点数据保存节点名称的属性名称
        children: "@", // 节点数据中保存子节点数据的属性名称
        checked: "@" // 节点数据中保存 check 状态的属性名称
      },
      template: '<div>' +
      '  <input id="citySel" type="text" readonly ng-click="showMenu();" style="width: 100%"/>' +
      '  <div id="menuContent">' +
      '    <ul id="nodeTree" class="ztree" style="margin-top:0; width:180px; height: 300px;"></ul>' +
      '  </div>' +
      '</div>',
      link: function (scope, element, attrs) {
        scope.beforeClick = function (treeId, treeNode) {
          var zTree = $.fn.zTree.getZTreeObj("nodeTree");
          zTree.checkNode(treeNode, !treeNode.checked, null, true);
          return false;
        };

        scope.onCheck = function (e, treeId, treeNode) {
          var nodes = scope.getSelectedValue();
          scope.$apply(function () {
            scope.ngModel = nodes;
          });
        };

        scope.getSelectedValue = function () {
          var zTree = $.fn.zTree.getZTreeObj("nodeTree");
          var nodes = zTree.getCheckedNodes(true);

          var names = [];
          for (var i = 0; i < nodes.length; i++) {
            names.push(nodes[i].name);
          }
          $("#citySel").val(names.join());
          return nodes;
        };

        scope.showMenu = function () {
          var cityObj = $("#citySel");
          var cityOffset = $("#citySel").offset();
          $("#menuContent").css({
            left: cityOffset.left + "px",
            top: cityOffset.top + cityObj.outerHeight() + "px"
          }).slideDown("fast");

          $("body").bind("mousedown", scope.onBodyDown);
        };

        scope.hideMenu = function () {
          $("#menuContent").fadeOut("fast");
          $("body").unbind("mousedown", scope.onBodyDown);
        };

        scope.onBodyDown = function (event) {
          if (!(event.target.id == "menuBtn" || event.target.id == "citySel" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length > 0)) {
            scope.hideMenu();
          }
        };

        var setting = {
          check: {
            enable: true,
            chkStyle: scope.type,
            radioType: "all"
          },
          callback: {
            beforeClick: scope.beforeClick,
            onCheck: scope.onCheck
          },
          data: {
            key: {
              name: scope.nodeName,
              children: scope.children,
              checked: scope.checked
            }
          }
        };

        scope.$watch('treeModel', function () {

          if (angular.isDefined(scope.treeModel)) {
            delete scope.treeModel.$promise;
            delete scope.treeModel.$resolved;
          }
          $.fn.zTree.init($("#nodeTree"), setting, scope.treeModel);

          scope.ngModel = scope.getSelectedValue();

        }, true);
      }
    };
  })

  .directive('ngZtreeAsync', function () {
    return {
      require: '?ngModel',
      restrict: 'EA',
      scope: {
        ngModel: "=", // 被选中的数据存在这里
        treeId: "@", // 树模型ID
        treeModel: "=", // 展示的树模型
        type: "@", // radio or checkbox
        nodeName: "@", // 节点数据保存节点名称的属性名称
        children: "@", // 节点数据中保存子节点数据的属性名称
        checked: "@", // 节点数据中保存 check 状态的属性名称
        topLevelUrl: "@", // 异步加载的顶级URL
        url: "@" // 异步加载的URL
      },
      template: '<div>' +
      '  <input id="{{treeId}}Sel" type="text" readonly ng-click="showMenu();" style="width: 100%"/>' +
      '  <div id="{{treeId}}Content">' +
      '    <ul id="{{treeId}}" class="ztree" style="position:absolute;margin-top:0; width:280px; height: 300px;z-index: 9"></ul>' +
      '  </div>' +
      '</div>',
      link: function (scope, element, attrs) {

        scope.filter = function (treeId, parentNode, childNodes) {
          if (!childNodes) {
            return null;
          }
          // 顶级节点
          else if (parentNode === undefined && childNodes instanceof Object) {
            childNodes.isParent = true;
            childNodes.icon = packageIconPath(childNodes.icon);
          }
          // 二级节点及以下
          else if (childNodes instanceof Array) {
            for (var i = 0, l = childNodes.length; i < l; i++) {
              if (childNodes[i].type && childNodes[i].type.toUpperCase() != 'Resource'.toUpperCase()) {
                childNodes[i].isParent = true;
                childNodes[i].icon = packageIconPath(childNodes[i].icon);
              }
            }
          }
          return childNodes;
        };

        // icon路径封装
        var packageIconPath = function (iconName){
          if(iconName===undefined || iconName===null){
            return null;
          }
          return "assets/sys_icons/"+iconName+"/16x16.png";
        };

        scope.beforeClick = function (treeId, treeNode) {
          var zTree = $.fn.zTree.getZTreeObj(scope.treeId);
          zTree.checkNode(treeNode, !treeNode.checked, null, true);
          return false;
        };

        scope.onCheck = function (e, treeId, treeNode) {
          var nodes = scope.getSelectedValue();
          scope.$apply(function () {
            scope.ngModel = nodes;
          });
        };

        scope.getSelectedValue = function () {
          var zTree = $.fn.zTree.getZTreeObj(scope.treeId);
          var nodes = [];
          if(zTree!=null){
            nodes = zTree.getCheckedNodes(true);
          }

          var names = [];
          if(scope.nodeName===undefined||scope.nodeName===null){
            scope.nodeName = "name";
          }
          for (var i = 0; i < nodes.length; i++) {
            names.push(nodes[i][scope.nodeName]);
          }
          $("#"+scope.treeId+"Sel").val(names.join());
          return nodes;
        };

        scope.showMenu = function () {
          var cityObj = $("#"+scope.treeId+"Sel");
          var cityOffset = cityObj.offset();
          $("#"+scope.treeId+"Content").css({
            left: cityOffset.left + "px",
            top: cityOffset.top + cityObj.outerHeight() + "px"
          }).slideDown("fast");

          $("body").bind("mousedown", scope.onBodyDown);
        };

        scope.hideMenu = function () {
          $("#"+scope.treeId+"Content").fadeOut("fast");
          $("body").unbind("mousedown", scope.onBodyDown);
        };

        scope.onBodyDown = function (event) {
          if (!(event.target.id == "menuBtn" || event.target.id == ""+scope.treeId+"Sel" || event.target.id == ""+scope.treeId+"Content" || $(event.target).parents("#"+scope.treeId+"Content").length > 0)) {
            scope.hideMenu();
          }
        };

        function getAsyncUrl(treeId, treeNode) {
          if ((treeNode === undefined || treeNode === null )&& scope.topLevelUrl !== undefined) {
            return scope.topLevelUrl;
          }
          if (treeNode === undefined || treeNode === null ){
            return scope.url;
          }
          return scope.url + treeNode.path;
        }

        var setting = {
          view: {
            selectedMulti: false
          },
          check: {
            enable: true,
            chkStyle: scope.type,
            radioType: "all"
          },
          async: {
            enable: true,
            url: getAsyncUrl,
            dataFilter: scope.filter,
            type: "get"
          },
          callback: {
            beforeClick: scope.beforeClick,
            onCheck: scope.onCheck
          },
          data: {
            key: {
              name: scope.nodeName,
              children: scope.children,
              checked: scope.checked
            }
          }
        };

        scope.$watch('treeModel', function () {

          if (angular.isDefined(scope.treeModel)) {
            delete scope.treeModel.$promise;
            delete scope.treeModel.$resolved;
          }
          $.fn.zTree.init($("#"+scope.treeId), setting, scope.treeModel);

          scope.ngModel = scope.getSelectedValue();

        }, true);
      }
    };
  });

