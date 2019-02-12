项目pdf依赖 jodconverter-core-3.0-beta-4.jar 需要安装到本地maven仓库。命令如下：

    包的路径需要自行替换。jar包位于本项目lib目录下

    mvn install:install-file -DgroupId=org.artofsolving.jodconverter -DartifactId=jodconverter-core -Dversion=3.0-beta-4 -Dfile=本机上jodconverter-core-3.0-beta-4.jar的路径 -Dpackaging=jar -DgeneratePom=true

