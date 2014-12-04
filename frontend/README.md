# Itsnow 前端开发指南

基于 [AngularJS](http://angularjs.org) 的WEB前端项目.

***

## 1.快速使用

本项目基于 git上开源的 [ng-boilerplate](https://github.com/joshdmiller/ng-boilerplate) 进行二次定制

在进行前端开发之前，开发者需要先安装好 nodejs，保证nodejs的bin目录在当前path下，而后执行：

```sh
$ npm -g install grunt-cli karma bower
$ npm install 
$ bower install
```
以上第一句 `npm -g ...` 是要求/保证你的开发机器的node js全局库里面有grunt等基本工具
以上第二句 `npm install` 是为frontend当前目录下生成 `node_modules` 目录，其中存储 `package.json` 里面定义的各种依赖，这些依赖的东西，一般都是在开发期使用，并不参与到前端系统的运行。

  由于个人操作系统不同(windows, mac, linux)，而nodejs的实际module可能有本地依赖，所以`node_modules`目录被git ignore掉，要求各自进行初始化
 
以上第三句 `bower install` 是为当前frontend项目根据`bower.json`安装app运行所需要用到的第三方js/css库到`vendor`目录，这些依赖项目，一般会被组装到最终应用中，在系统运行期被使用。

本项目包括Itsnow的MSC/MSU/MSP三个子系统的前端部分，可以分别build，有两种方式进行build：

### 方式A： 直接使用命令行进行build

在build之前，开发者需要先为grunt build设定`.target`内容，如为MSC build前端：

```JSON
{
    "name":    "msc",
    "title":   "Msc",
    "version": "0.1.6",
    "index":   "msc-index-0.1.6",
    "login":   "msc-login-0.1.6"
}
```
```sh
$ grunt build|compile|default --verbose
```
build出来的结果存储在 {msc|msu|msp}/{build|deploy}目录中

### 方式B： 使用MAVEN进行build，build的任务信息已经在pom.xml中定义

```sh
$mvn package -Pdnt,with-itsnow-msc
$mvn package -Pdnt,with-itsnow-msp
$mvn package -Pdnt,with-itsnow-msu
```

build出来的结果不仅会被存储在当前目录下的 {msc|msu|msp}/{build|deploy}中，还同时被copy到itsnow相应的release路径下

## 2. 项目结构

### 2.1 原始结构
```
itsnow/frontend/
  |- grunt-tasks/
  |- node_modules/ 
  |  |- <npm install之后NodeJs会根据package.json将依赖的local node module存在此目录>
  |- karma/
  |  |- karma-unit.tpl.js <karma单元测试的配置文件模板>
  |- vendor/
  |  |- <各种第三方代码，所有子系统的所有应用都会引入它们>
  |  |- angular-bootstrap/
  |  |- bootstrap/
  |  |- placeholders/
  |- src/
  |  |- lib/
  |  |  |- <在 index和login两个Single Page Application(SPA)之间复用的基础代码>
  |  |- index/
  |  |  |- <index app的基础代码>
  |  |- login/
  |  |  |- <login app的基础代码>
  |  |- msc/
  |  |  |- index/
  |  |  |   |- <msc主应用的最终代码>
  |  |  |- login/
  |  |  |   |- <msc登录应用的最终代码>
  |  |- msp/
  |  |  |- index/
  |  |  |   |- <msp主应用的最终代码>
  |  |  |- login/
  |  |  |   |- <msp登录应用的最终代码>
  |  |- msu/
  |  |  |- index/
  |  |  |   |- <msu主应用的最终代码>
  |  |  |- login/
  |  |  |   |- <msu登录应用的最终代码>
  |  |- assets/
  |  |  |- <各种静态文件>
  |  |- less/
  |  |  |- login.less
  |  |  |- index.less
  |  |  |- variables.less
  |- index.jade <主应用框架>
  |- login.jade <Login应用框架>
  |- .bowerrc
  |- bower.json
  |- build.config.js
  |- Gruntfile.js
  |- module.prefix
  |- module.suffix
  |- package.json
```

- `karma/`  - 前端单元测试模板文件.
- `src/`    - 前端程序代码. [Read more &raquo;](src/README.md)
- `vendor/` - 第三方库. [Bower](http://bower.io)，在执行 `bower install`时，所有第三方库都会被安装到本目录
  如果你需要增加第三方库，那么应该先在`bower.json`里面声明，而后再次执行 `bower install`
  而不是直接向其中添加
- `.bowerrc` - Bower的配置文件. 主要作用就是告知bower，配置文件名称为 `bower.json`，需要安装到vendor目录
- `bower.json` - 里面声明了项目名称(MSC|P|U统一的)，版本号，以及最重要的对第三方库的依赖关系.
- `{msc|msu|msp}.config.js` - 三个前端子系统的各自配置文件
- `Gruntfile.js` - 统一的Grunt脚本，类似于maven的pom.xml，ant的ant.xml，Rails项目的Rakefile
- `module.prefix` and `module.suffix` - 合并JS代码时的辅助脚本，能够将应用代码包装到一个延迟加载的函数中
- `package.json` - 统一的项目信息
- `changelog.tpl`, `CHANGELOG.MD`: 发布说明
- `.target`： 用于指导grunt到底build哪个前端子系统的控制文件
- `{msc|msp|msu}/build`: build之后的输出目录，主要用于开发调试，其中的js未合并，css虽然合并了，但未压缩
- `{msc|msp|msu}/deploy`: compile之后的输出目录，主要用于最终发布，其中的js已经合并且压缩，css合并且压缩

### 2.2 Build目录结构

```
itsnow/frontend/{msc|msu|msp}/build
 |- index.html <已登录用户主应用的html文件>
 |- login.html <未登录用户的主应用html文件>
 |- assets/
 |   |- <各种图片,字体,CSS文件>
 |   |- {msc|msu|msp}-index-0.1.8.css
 |   |- {msc|msu|msp}-login-0.1.8.css
 |- vendor/
 |- templates/
 |- lib/
 |- index/
 |- login/
 |- {msc|msu|msp}/index
 |- {msc|msu|msp}/login
```

- `index.html`, `login.html`: 由原始`index.jade`，`login.jade`分别编译生成，其中显性化引用了各目录下的javascripts文件，引用css文件`{msc|msu|msp}-{index|login}-{version}.css` 
- `assets/` 存放所有的图片，字体，css文件的目录，包括第三方vendor的css文件均存于此目录，而非vendor目录（*这样做的原因是，让build与deploy时css文件与其可能引用到的图片，字体位置关系一致*）
- `vendor/` 存放所有的第三方的js文件，这些js文件按照在源项目中的结构布局，并未全部放到assets目录
- `templates/` 存放源项目结构中 `*.tpl.html`, `*.tpl.jade` html2js生成的模板文件
- `lib/`, `index/`, `login/`, `{msc|msu|msp}/index/`, `{msc|msu|msp}/login/` 存放源项目结构中的js文件,包括coffee文件编译生成的js文件


### 2.3 Deploy目录结构

```
itsnow/frontend/{msc|msu|msp}/build
 |- index.html <已登录用户主应用的html文件>
 |- login.html <未登录用户的主应用html文件>
 |- assets/
 |   |- <各种图片,字体,CSS文件>
 |   |- {msc|msu|msp}-index-0.1.8.js <合并，压缩过的主应用js文件>
 |   |- {msc|msu|msp}-login-0.1.8.js <合并，压缩过的登录应用js文件>
 |   |- {msc|msu|msp}-index-0.1.8.css <合并，压缩过的主应用css文件>
 |   |- {msc|msu|msp}-login-0.1.8.css <合并，压缩过的登录应用的css文件>
```

## 3.开发规范

首先，明确本文中提到的几个词汇的具体含义：

1.  `子系统(subsystem)`： msc, msp, msu 等前端程序
2.  `应用(app,spa)`:   login, index 等前端程序的两大组成部分
3.  `login app`：未登录用户所看到的前端应用内容
4.  `index app`：已登录用户所看到的前端应用内容

### 3.1 目录作用规范

1. `vendor`目录中第三方库会被所有子系统中所有应用加载(具体文件和次序由msx.config.js指定)
2. `src/lib`下的内容也会被所有子系统所有应用加载，与vendor的差别在于这里是我们开发的内容
3. `src/index`下的内容会被所有子系统的index app所加载
4. `src/login`下的内容会被所有子系统下的login app所加载
5. `src/{msc|msu|msp}/login` 为最终应用，在这里进行相应登录应用的最终组装
6. `src/{msc|msu|msp}/index` 为最终应用，在这里进行相应主应用的最终组装

### 3.2 HTML编码规范

前端系统采用Jade进行HTML编写，具体的JADE语法，请参考 [这里](http://jade-lang.com)，每个前端开发人员应该花20分钟将其 [reference](http://jade-lang.com/reference/)通读，即可基本掌握.

另外，前端还采用了html2js技术，将html模板编译成为js文件，由用户打开页面时一次性加载，避免将模板存储于服务器端时，客户端浏览器在用户访问时多次重复加载。

jade编写的html文件一般有如下几大类：

1. 主体文件，如 `index.jade`, `login.jade`
2. 模板文件(*.tpl.jade)：这些jade文件在build期间将会被编译成为template目录下的js文件
3. 被引用/包含的文件

需要注意的是：模板jade文件在以相对路径方式include/extends其他jade文件时，存在技术限制，需要在grunt里面给予配置

jade文件/内容规划存在如下规范

1. 专门的内容区域，应该被独立成为一个独立的jade文件，并被使用者include
2. 多个相似的html内容，应该采用mixin进行复用
3. 多个相似的html文件，应该采用layout/extends机制复用

jade内容编写存在如下规范：

1. 各部分先后排布顺序为: 变量声明/赋值; mixin定义; html内容编写（参考: `index.jade` ）
2. html文件内容之前，一般应该增加文档类型声明: `doctype html`
3. tag的class一般都应该以点号`.`描述，id属性应该以井号`#`描述，默认的div tag尽量省略
4. 单一包容体系，应该尽量写在一行，如： `.page-content: .row: .col-xs-12: div(ui-view ng-controller='IndexCtrl')`
5. 由于IDE未能很好的支持script tag下的jade内容，所有需要定义在script tag下的内容，应该定义在mixin里面，并在script tag中加以调用
6. mixin中的script内容，需要以文本形式，所以，script后面需要增加点号: `.`


### 3.3 JS编码规范

#### 3.3.1 模块命名

模块是指angular模块，用于组织js代码

1. 顶层模块一般命名为 `Itsnow.$ModuleName`，如 `Itsnow.Index`,`Itsnow.MscIndex`
2. 子模块一般命名为: `$ParentModule.$ChildModule`，为了避免模块名到处充斥了 `Itsnow`， 非顶级模块可以去除 `Itsnow`，如 `Itsnow.MscIndex` 的`SLA`子模块原本应该命名为`Itsnow.MscIndex.Sla`可以简化命名为 `MscIndex.SLA`；

#### 3.3.2 服务命名

服务是指angular模块里面的controller, service等，用于实现特定功能

1. 控制器模块命名格式为 `SomeCtrl`
2. 控制器类实例化的变量命名为 `someCtrl`
3. 后端资源命名格式为 `SomeService`
4. 后端资源实例化的变量命名为 `someService`

#### 3.3.3 代码布局

1. 模块声明(module)
2. 模块配置(config)
3. 服务定义(controller/factory)
4. 模块运行(run)

#### 3.3.4 JS编码

1. 模块注册：由于前端采用了angular-ui-state进行路由/页面组织，所以每个模块都应该声明自身对应的状态和模板，另外，特别需要注意的是

    a. 状态应该与后台数据库中的菜单state对应
    b. 需要提供data.pageTitle

2. 资源声明：如果资源被多个控制器所使用，应该通过factory定义；否则只需要在使用该资源的控制器中定义，如：

```js
angular.module('Index.Menu', []).
  controller('MenuCtrl', ['$scope', '$resource', function ($scope, $resource) {
    var menuService = $resource('/api/menu_items');
    $scope.topMenuItems = menuService.query();
  }]);
```

3. 待增加(TO BE ADDED)

#### 3.3.5 JS测试规范


### 3.4 CSS编写规范

## 4. 组件使用

### 4.1 菜单组件

### 4.2 表格组件

开发中，请参考 accounts list 模块

### 4.3 表单组件

开发中，请参考 account 注册模块（不要忽略TODO表达的未竟事宜）

### 4.4 其他系统组件

1.Ajax指示

2.消息反馈

  angular模块：`Lib.Feedback`
  angular服务：`Feedback`

  Feedback API：

```
  feedback(message, options{level, dismiss})
  success(message, dismiss{*})
  info(message, dismiss{*})
  primary(message, dismiss{*})
  warn(message, dismiss{*})
  error(message, resp, dismiss{*})
```

使用示例：

   
```
angular.module('MscIndex.Account', ['ngTable','ngResource', 'ngSanitize','dnt.action.service', 'Lib.Feedback'])
  .controller 'AccountListCtrl',['$scope', '$location', '$timeout', '$resource', '$http', 'ngTableParams', 'ActionService', 'Feedback', \
                                ($scope, $location, $timeout, $resource, $http, ngTableParams, ActionService, Feedback)->
    $scope.approve = (account) ->
      acc = new Account(account)
      acc.$approve(->
        Feedback.success("已批准" + account.name)
        $scope.tableParams.reload()
      , (resp)->
        Feedback.error("批准" + account.name + "失败", resp)
      )

```

3.对话框交互

4.批量操作/进度对话框