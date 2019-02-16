package cs.whu.cloud.disk;

import cs.whu.cloud.disk.controller.BaseController;
import cs.whu.cloud.disk.db.HbaseDB;
import cs.whu.cloud.disk.db.HdfsDB;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiskApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DiskApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        //消除在windows上开发时，报错.参考 http://www.cnblogs.com/hyl8218/p/5492450.html
        if(System.getProperties().getProperty("os.name").toLowerCase().startsWith("windows")) {
            System.setProperty("hadoop.home.dir", "E:\\Program Files\\hadoop-2.7.0");
        }

        //设置hadoop操作用户为集群有权限账号
        System.setProperty("HADOOP_USER_NAME", "root");

        HbaseDB hbaseDB=HbaseDB.getInstance();
        if(hbaseDB==null){
            System.out.println("hbaseDb初始化失败");
        }else{
            BaseController.db=hbaseDB;
            System.out.println("hbaseDb初始化完毕");
        }
        HdfsDB hdfsDB=HdfsDB.getInstance();
        if(hdfsDB==null){
            System.out.println("hdfsDb初始化失败");
        }else{
            BaseController.hdfsDB=hdfsDB;
            System.out.println("hdfsDb初始化完毕");
        }
        System.out.println("init end");
    }
}
