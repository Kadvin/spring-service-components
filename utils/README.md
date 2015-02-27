程序基础包
==========

本组件以提供静态工具类为主，
但合并了原先的Config组件，主要是负责将 ${app.home}/config/*.properties加载到spring环境以及系统属性中
另外提供了一个hint=system的TaskScheduler服务
以后可能会提供更多其他系统服务，例如于各种系统Execution Service

--
2013/12/11