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
      '    <ul id="treeDemo" class="ztree" style="margin-top:0; width:180px; height: 300px;"></ul>' +
      '  </div>' +
      '</div>',
      link: function (scope, element, attrs) {
        scope.beforeClick = function (treeId, treeNode) {
          var zTree = $.fn.zTree.getZTreeObj("treeDemo");
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
          var zTree = $.fn.zTree.getZTreeObj("treeDemo");
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
          $.fn.zTree.init($("#treeDemo"), setting, scope.treeModel);

          var nodes = scope.getSelectedValue();
          scope.ngModel = nodes;

        }, true);
      }
    };
  })

  .directive('ngZtreeMenu', function () {
    return {
      require: '?ngModel',
      restrict: 'EA',
      replace: true,
      scope: {
        nodeName: "@", // 节点数据保存节点名称的属性名称
        topLevelUrl: "@", // 顶层异步加载的URL
        url: "@" // 异步加载的URL
      },
      template: '<div>' +
      '  <div class="zTreeDemoBackground left">' +
      '    <ul id="treeDemo" class="ztree"></ul>' +
      '  </div>' +
      '  <div id="rMenu">' +
      '    <ul>' +
      '      <li id="m_add" ng-click="addTreeNode();">增加节点</li>' +
      '      <li id="m_del" ng-click="removeTreeNode();">删除节点</li>' +
      '      <li id="m_check" ng-click="checkTreeNode(true);">Check节点</li>' +
      '      <li id="m_unCheck" ng-click="checkTreeNode(false);">unCheck节点</li>' +
      '      <li id="m_reset" ng-click="resetTree();">恢复zTree</li>' +
      '    </ul>' +
      '  </div>' +
      '  <style type="text/css">' +
      '    div#rMenu {position:absolute; visibility:hidden; top:0; background-color: #555;text-align: left;padding: 2px;z-index: 9}' +
      '    div#rMenu ul li{' +
      '    margin: 1px 0;' +
      '    padding: 0 5px;' +
      '    cursor: pointer;' +
      '    list-style: none outside none;' +
      '    background-color: #DFDFDF;' +
      '    z-index: 9;' +
      '    }' +
      '  </style>' +
      '</div>',
      link: function (scope, element, attrs) {

        scope.filter = function (treeId, parentNode, childNodes) {
          if (!childNodes) {
            return null;
          } else if (childNodes instanceof Array) {
            for (var i = 0, l = childNodes.length; i < l; i++) {
              if (childNodes[i].type && childNodes[i].type.toUpperCase() != 'Resource'.toUpperCase()) {
                childNodes[i].isParent = true;
              }
            }
          } else if (childNodes instanceof Object) {
            childNodes.isParent = true;
          }
          return childNodes;
        };

        scope.beforeClick = function beforeClick(treeId, treeNode) {
        };

        var className = "dark";
        scope.beforeAsync = function (treeId, treeNode) {
          className = (className === "dark" ? "" : "dark");
          return true;
        };

        scope.onAsyncError = function (event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
        };

        scope.onAsyncSuccess = function (event, treeId, treeNode, msg) {
          zTree = $.fn.zTree.getZTreeObj("treeDemo");
          var nodes = zTree.getNodes();
          if (nodes.length>0) {
            zTree.expandNode(nodes[0], true);
          }
        };

        scope.onRightClick = function (event, treeId, treeNode) {
          if (!treeNode && event.target.tagName.toLowerCase() != "button" && $(event.target).parents("a").length == 0) {
            zTree.cancelSelectedNode();
            scope.showRMenu("root", event.clientX, event.clientY);
          } else if (treeNode && !treeNode.noR) {
            zTree.selectNode(treeNode);
            scope.showRMenu("node", event.clientX, event.clientY);
          }
        };

        scope.showRMenu = function (type, x, y) {
          $("#rMenu ul").show();
          if (type == "root") {
            $("#m_del").hide();
            $("#m_check").hide();
            $("#m_unCheck").hide();
          } else {
            $("#m_del").show();
            $("#m_check").show();
            $("#m_unCheck").show();
          }
          rMenu.css({"top": y + "px", "left": x + "px", "visibility": "visible"});

          $("body").bind("mousedown", scope.onBodyMouseDown);
        };
        scope.hideRMenu = function () {
          if (rMenu) rMenu.css({"visibility": "hidden"});
          $("body").unbind("mousedown", scope.onBodyMouseDown);
        };
        scope.onBodyMouseDown = function (event) {
          if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length > 0)) {
            rMenu.css({"visibility": "hidden"});
          }
        };

        function getAsyncUrl(treeId, treeNode) {
          if ((treeNode === undefined || treeNode === null )&& scope.topLevelUrl != undefined) {
            return scope.topLevelUrl;
          }
          return scope.url + treeNode.path;
        }

        var setting = {
          view: {
            selectedMulti: false
          },
          async: {
            enable: true,
            url: getAsyncUrl,
            dataFilter: scope.filter,
            type: "get"
          },
          callback: {
            beforeClick: scope.beforeClick,
            beforeAsync: scope.beforeAsync,
            onAsyncError: scope.onAsyncError,
            onAsyncSuccess: scope.onAsyncSuccess
            //onRightClick: scope.onRightClick
          },
          data: {
            key: {
              name: scope.nodeName,
              children: scope.children,
              checked: scope.checked
            }
          }
        };

        var zTree, rMenu;
        $.fn.zTree.init($("#treeDemo"), setting);
        zTree = $.fn.zTree.getZTreeObj("treeDemo");
        var nodes = zTree.getNodes();
        if (nodes.length>0) {
          zTree.reAsyncChildNodes(nodes[0], "refresh");
        }
        rMenu = $("#rMenu");
      }
    };
  });