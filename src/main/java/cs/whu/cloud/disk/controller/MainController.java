package cs.whu.cloud.disk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//@RequestMapping("/")
@Controller
public class MainController {

    @RequestMapping("/")
    public String index(){
        return "index";
    }

/*    @RequestMapping("test")
    public String test(){
        return "test";
    }*/
}
