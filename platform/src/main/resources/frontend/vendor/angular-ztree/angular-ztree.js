angular.module('ng-ztree', ['ng'])

  .directive('ngZtree', function () {
    return {
      require: '?ngModel',
      restrict: 'EA',
      replace: true,
      scope: {
        treeId: "@", // 树模型ID
        ngModel: "=", // 被选中的数据存在这里
        treeModel: "=", // 展示的树模型
        type: "@", // radio or checkbox
        nodeName: "@", // 节点数据保存节点名称的属性名称
        children: "@", // 节点数据中保存子节点数据的属性名称
        checked: "@" // 节点数据中保存 check 状态的属性名称
      },
      template: '<div>' +
        '  <input id="{{treeId}}Sel" type="text" readonly ng-click="showMenu();" style="width: 100%"/>' +
        '  <div id="{{treeId}}Content">' +
        '    <ul id="{{treeId}}" class="ztree" style="margin-top:0; width:280px; height: 300px;"></ul>' +
        '  </div>' +
        '</div>',
      link: function (scope, element, attrs) {
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
          var nodes = zTree.getCheckedNodes(true);

          var names = [];
          for (var i = 0; i < nodes.length; i++) {
            names.push(nodes[i].name);
          }
          $("#"+scope.treeId+"Sel").val(names.join());
          return nodes;
        };

        scope.showMenu = function () {
          var cityObj = $("#"+scope.treeId+"Sel");
          var cityOffset = $("#"+scope.treeId+"Sel").offset();
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
          if (!(event.target.id == "menuBtn" || event.target.id == scope.treeId+"Sel" || event.target.id == scope.treeId+"Content" || $(event.target).parents("#"+scope.treeId+"Content").length > 0)) {
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
          $.fn.zTree.init($("#"+scope.treeId), setting, scope.treeModel);

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
        type: "@", // radio or checkbox
        nodeName: "@", // 节点数据保存节点名称的属性名称
        children: "@", // 节点数据中保存子节点数据的属性名称
        checked: "@", // 节点数据中保存 check 状态的属性名称
        topLevelUrl: "@", // 异步加载的顶级URL
        url: "@" // 异步加载的URL
      },
      template: '<div>' +
        '  <input id="{{treeId}}Sel" type="text" readonly ng-click="showMenu();" ng-init="showMenu();" style="width: 100%"/>' +
        '  <div id="{{treeId}}Content">' +
        '    <ul id="{{treeId}}" class="ztree" style="position:absolute;margin-top:0; width:280px; height: 300px;z-index: 9"></ul>' +
        '  </div>' +
        '</div>',
      link: function (scope, element, attrs) {

        scope.type = scope.type ? scope.type : "radio";

        scope.filter = function (treeId, parentNode, responseData) {
          if (!responseData) {
            return null;
          }
          // 顶级节点
          else if (parentNode === undefined) {
            responseData.isParent = true;
            responseData.icon = warpIconPath(responseData.icon);
          }
          // 二级节点及以下
          else {
            for (var i = 0, l = responseData.length; i < l; i++) {
              if (responseData[i].type && responseData[i].type.toUpperCase() != 'Resource'.toUpperCase()) {
                responseData[i].isParent = true;
                responseData[i].icon = warpIconPath(responseData[i].icon);
              }
            }
          }
          return responseData;
        };

        // icon路径封装
        var warpIconPath = function (iconName) {
          if (iconName === undefined || iconName === null) {
            return null;
          }
          return "assets/sys_icons/" + iconName + "/16x16.png";
        };

        scope.beforeClick = function (treeId, treeNode) {
          zTree.checkNode(treeNode, !treeNode.checked, null, true);
          return false;
        };

        scope.onCheck = function (e, treeId, treeNode) {
          var nodes = scope.getSelectedValue();
          scope.$apply(function () {
            scope.ngModel = nodes;
          });
          scope.hideMenu();
        };

        scope.getSelectedValue = function () {
          var nodes = [];
          var names = [];
          if (!scope.nodeName) {
            scope.nodeName = "name";
          }
          if (zTree != null) {
            nodes = zTree.getCheckedNodes(true);
            if (scope.type == 'radio') {
              if (nodes && nodes.length > 0) {
                $("#" + scope.treeId + "Sel").val(nodes[0][scope.nodeName]);
                return nodes[0];
              } else {
                return null;
              }
            } else {
              for (var i = 0; i < nodes.length; i++) {
                names.push(nodes[i][scope.nodeName]);
              }
              $("#" + scope.treeId + "Sel").val(names.join());
              return nodes;
            }
          }
        };

        scope.showMenu = function () {
          var cityObj = $("#" + scope.treeId + "Sel");
          var cityOffset = cityObj.offset();
          $("#" + scope.treeId + "Content").css({
            left: cityOffset.left + "px",
            top: cityOffset.top + cityObj.outerHeight() + "px"
          }).slideDown("fast");

          $("body").bind("mousedown", scope.onBodyDown);

          var node = zTree.getNodes();
          zTree.reAsyncChildNodes(node[0], "refresh");
        };

        scope.hideMenu = function () {
          $("#" + scope.treeId + "Content").fadeOut("fast");
          $("body").unbind("mousedown", scope.onBodyDown);
        };

        scope.onBodyDown = function (event) {
          if (!(event.target.id == "menuBtn" || event.target.id == "" + scope.treeId + "Sel" || event.target.id == "" + scope.treeId + "Content" || $(event.target).parents("#" + scope.treeId + "Content").length > 0)) {
            scope.hideMenu();
          }
        };

        function getAsyncUrl(treeId, treeNode) {
          if ((treeNode === undefined || treeNode === null ) && scope.topLevelUrl !== undefined) {
            return scope.topLevelUrl;
          }
          if (treeNode === undefined || treeNode === null) {
            return scope.url;
          }
          var urls = scope.url.split("?");
          if(urls.length>1) {
            return urls[0] + treeNode.path + "?" + urls[1];
          }else{
            return scope.url + treeNode.path;
          }
        }

        function beforeAsync() {
          curAsyncCount++;
        }

        function onAsyncSuccess(event, treeId, treeNode, msg) {
          curAsyncCount--;
          if (treeNode) {
            asyncNodes(treeNode.children);
          } else {
            asyncNodes(treeNode);
          }
          if (curAsyncCount <= 0) {
            if (curStatus != "init" && curStatus != "") {
              asyncForAll = true;
              scope.$apply(function () {
                if(scope.ngModel){
                  console.log("scope.ngModel.path:"+scope.ngModel.path);
                  var nodes = zTree.getNodes();
                  console.log("nodes:"+nodes);
                  var node = zTree.getNodeByParam("path", scope.ngModel.path, null);
                  if(node){
                    zTree.selectNode(node);
                    node.checked = true;
                    zTree.updateNode(node);
                    scope.getSelectedValue();
                  }
                }
              });
            }
            curStatus = "";
          }
        }

        function asyncNodes(nodes) {
          if (!nodes) return;
          curStatus = "async";
          for (var i = 0, l = nodes.length; i < l; i++) {
            if (nodes[i].isParent && nodes[i].zAsync) {
              asyncNodes(nodes[i].children);
            } else {
              goAsync = true;
              zTree.reAsyncChildNodes(nodes[i], "refresh", true);
            }
          }
        }

        var zTree,curStatus = "init", curAsyncCount = 0, asyncForAll = false, goAsync = false, path="";

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
            beforeAsync: beforeAsync,
            onAsyncSuccess: onAsyncSuccess,
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

        scope.$watch('ngModel', function () {
          $.fn.zTree.init($("#" + scope.treeId), setting, null);
          zTree = $.fn.zTree.getZTreeObj(scope.treeId);
        }, true);
      }
    };
  });

