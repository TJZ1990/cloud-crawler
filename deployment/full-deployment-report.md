## 前期准备
可以参考“阿里云ECS购买和连接”文档  
1. 购买云服务器  
2. 创建实例  
3. 配置安全组规则使得各实例间可以互相联通  

## 搭建环境
### 搭建FTP服务
* 安装vsftp  
`$ apt-get install vsftpd -y`  
-y表示对所有的询问都以y回答且不显示提示  
* 找到nologin的路径--通常在`/usr/sbin/nologin`或者`/sbin/nologin`下  
* 创建ftp的用户  
`$ useradd -d 用户的主目录 -s nologin的路径 用户名`  
比如  
`$ useradd -d /usr/rootftp -s /usr/sbin/nologin rootftp`  
注意要先把该主目录建好  
* 创建该用户的密码  
`$ passwd rootftp`  
* 将ftp目录的所有者交给ftp用户(使用户获取目录的写权限)  
`$ chown -R rootftp:rootftp /usr/rootftp`  
-R表示递归--也就是说该目录下所有子目录也归ftp用户所有  
如果不想让该用户上传就跳过这一步(这样该用户就没有写权限--自然不能上传)  
*  配置vsftp  
`$ vi /etc/vsftpd.conf`  
->设置anonymous_enable=NO  
->如果允许用户上传则去掉write_enable=YES前的注释符号  
->如果要限制用户查看除其子目录外的其他目录则要配置chroot_local_user、chroot_list_enable、chroot_list_file几项  
* 修改shell配置  
`$ vi /etc/shells`  
将nologin的路径追加进去  
* 重启vsftp服务并测试登录  
`$ service vsftpd restart`  

### 安装java运行环境
* 从[oracle官网](http://www.oracle.com/technetwork/java/javase/downloads/index.html)上下载jdk并上传至服务器  
* 解压--下面示例以jdk-8u73为例  
`$ tar -zxvf jdk-8u73-linux-x64.tar.gz  -C /usr/local/src/`  
-C指定解压到的目录  
* 给root用户设置java环境变量  
`$ vi /root/.profile`  
加入以下内容  
```
JAVA_HOME=/usr/local/src/jdk1.8.0_73  
export JAVA_HOME  
PATH=$PATH:$JAVA_HOME/bin  
export PATH  
CLASSPATH=.:$JAVA_HOME/lib  
export CLASSPATH  
```
* 使环境变量生效  
`$ source /root/.profile`  
* 验证java环境  
`$ java -version`  

### 安装配置Redis(仅master需要)
* 安装redis-server  
`$ apt-get install redis-server`  
* 配置Redis  
`$ vi /etc/redis/redis.conf`  
如果需要设置访问密码--将下面一行去掉注释并设置密码

> requirepass XXX

如果要允许远程访问Redis--将所有的bind设置都注释掉  
之后可用如下命令访问  
`$ redis-cli -h IP -p PORT -a CODE`  

### 安装配置MySQL(仅master需要)
* 安装  
`$ apt-get install mysql-server mysql-client`  
安装过程中会提示设置root用户的密码  
之后登陆mysql进行测试(-p后不能有空格)  
`$ mysql -h localhost -uroot -p密码`  
* 配置  
`$ vi /etc/mysql/my.cnf`  
->修改字符集为utf8以支持中文--  
在[client]下追加  
`default-character-set=utf8`  
在[mysqld]下追加  
`character-set-server=utf8`  
在[mysql]下追加  
`default-character-set=utf8`  
之后在mysql中用status查看字符集情况  
->如果要允许远程用户访问MySQL--注释掉下面这行

> bind-address = 127.0.0.1  
> 或 skip-networking (某些MySQL版本)

->修改其他需要的配置后重启MySQL服务  
`$ service mysql restart`  
* root登陆MySQL后可以限制各用户的访问权限(相当于修改mysql下的user表)  
`GRANT 操作 ON 数据库.表 TO 用户名@登录主机 IDENTIFIED BY "密码";`  
例如  
`GRANT select,insert,update,delete ON test.* TO user@'%' IDENTIFIED BY '';`  
`GRANT ALL PRIVILEGES ON *.* TO root@'%' IDENTIFIED BY '654321';`  
修改后刷新一下  
`FLUSH PRIVILEGES;`

##部署应用
* 将工程完成后的完整jar包(包含所有依赖)上传至服务器  
* 将jar包移动到指定位置  
`$ mv -iuv /usr/rootftp/cloud-crawler-jar-with-dependencies.jar /usr/crawler`  
* 从工程目录deployment/shell/目录下找到启动脚本"cloud-crawler.sh"后上传至服务器  
* 运行启动脚本  
`$ chmod +x cloud-crawler.sh`  
`$ ./cloud-crawler.sh -m 或 ./cloud-crawler.sh -s`  
->参数-m表示启动master服务  
->参数-s表示启动slave服务  
->可以用--help查看具体用法  
启动salve服务时可以加上master的IP、PORT、CODE  
`$ ./cloud-crawler.sh -s IP PORT CODE`  
不加时使用程序内默认值

