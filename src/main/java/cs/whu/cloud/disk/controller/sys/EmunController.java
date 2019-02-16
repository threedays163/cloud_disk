package cs.whu.cloud.disk.controller.sys;

import cs.whu.cloud.disk.controller.BaseController;
import cs.whu.cloud.disk.vo.Menu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Slf4j
@Controller
@RequestMapping("/emun")
public class EmunController extends BaseController {

	@ResponseBody
	@RequestMapping("/list")
	public List<Menu> list() throws Exception {
		return db.qureyAllEmun();
	}
}
