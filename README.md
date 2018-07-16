# SimpleLauncher
a simple launcher for android

>工程如何使用？
 1. 下载代码:    
    git clone https://github.com/wangxcgg/SimpleLauncher.git 
 2. Android Studio3.0 中直接打开或者导入Project,编译运行即可。    

> demo如何使用?    

 1. 自启动功能，系统开机启动到Launcher会检查哪个应用配置了开机自启并打开该应用。    
    
 2. 定时关机，注册了两个receiver，在时间到来时收到广播并实现关机功能。关机功能目前只适用比android6.0版本低的系统，同时
 需要reboot权限，因此需要对apk添加系统签名。
 
 3.系统设置，快捷调用系统的设置页面。
 
 4.所有应用，展示所有已安装的应用，不包括系统应用。
 
