package cs.whu.cloud.disk;

import cs.whu.cloud.disk.db.HbaseDB;
import cs.whu.cloud.disk.db.HdfsDB;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiskApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DiskApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        //HbaseDB.getInstance();

        //HdfsDB.getInstance();
    }
}
