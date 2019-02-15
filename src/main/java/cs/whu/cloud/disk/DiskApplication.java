package cs.whu.cloud.disk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class DiskApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiskApplication.class, args);
    }

}
