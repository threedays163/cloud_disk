package cs.whu.cloud.disk;

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
        //消除在windows上开发时，报错
        System.setProperty("hadoop.home.dir", "E:\\Program Files\\hadoop-2.7.0");

        HbaseDB hbaseDB=HbaseDB.getInstance();
        if(hbaseDB==null){
            System.out.println("hbaseDb初始化失败");
        }else{
            System.out.println("hbaseDb初始化完毕");
        }
        HdfsDB hdfsDB=HdfsDB.getInstance();
        if(hdfsDB==null){
            System.out.println("hdfsDb初始化失败");
        }else{
            System.out.println("hdfsDb初始化完毕");
        }

        System.out.println("init end");
    }
}
