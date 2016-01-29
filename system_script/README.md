系统调用模块设计说明
==================

1. 整体结构
-----------

```
                             +-----------------+
                             | ExecutorService |
                            /+-----------------+
                           /
   +---------------------+/   +-----------------------------+
   | SystemInvokeService |----| SystemInvocationListener(s) |
   +---------------------+\   +-----------------------------+
                           \ 
                            \ +---------------+            +------------------+
                             \| SystemInvoker |--------->  | SystemInvocation |
                              +---------------+ <invoke>   +--------^---------+   
                                   |                               |
                                   |                               |<extends>
                                   |            +------------------+--------------+
                                   |            |                                 |
                                   |     +----------------+             +------------------+
                                   |<----| LocalInvocation|             | RemoteInvocation |
                                   |     +----------------+             +------------------+ 
                                   |
                                   |convert invocation
                                   |
                                   |                      +------------------+         +---------+ 
                                   |                      | Abstract Process | -impl-> | Process |
                                   |                      +--------^---------+         +---------+
                                   |                               |
                                   |as local or remote process     |<extends>
                                   |            +------------------+--------------+
                                   |            |                                 |
                                   |      +--------------+               +----------------+
                                   +----->| LocalProcess |               | RemoteProcess  |
                                          +--------------+               +----------------+ 
```

1. SystemInvokeService支持以后台任务的形式进行系统调用（通过ExecutorService）
2. SystemInvokeService支持外部观察某个调用的执行情况(SystemInvocationListener)
3. SystemInvokeService通过SystemInvoker实际执行系统调用(SystemInvocation)
4. SystemInvocation分为两种，一种是在本地执行的LocalInvocation，另外一种是 通过SSH在远程主机上执行的
5. SystemInvocation本身可以像链条一样串起来
6. 本地或远程执行的System Infocation会被SystemInvoker翻译成为LocalProcess或者Remote Process
7. Abstract Process内部实际上通过java.lang.ProcessBuilder构建java.lang.Process实现本地或者远程调用