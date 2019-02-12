package cs.whu.cloud.disk.controller.sys;

import java.util.List;

import cs.whu.cloud.disk.controller.BaseController;
import cs.whu.cloud.disk.vo.Menu;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/emun")
public class EmunController extends BaseController {

	@ResponseBody
	@RequestMapping("/list")
	public List<Menu> list() throws Exception {
		return db.qureyAllEmun();
	}
}
