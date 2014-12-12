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
          for(var i=0; i<nodes.length; i++){
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
            key:{
              name: scope.nodeName,
              children: scope.children,
              checked: scope.checked
            }
          }
        };

        scope.$watch('treeModel', function () {

          if (angular.isDefined(scope.treeModel)) {
            delete scope.treeModel.$promise
            delete scope.treeModel.$resolved
          }
          $.fn.zTree.init($("#treeDemo"), setting, scope.treeModel);

          var nodes = scope.getSelectedValue();
          scope.ngModel = nodes;

        }, true);
      }
    };
  });