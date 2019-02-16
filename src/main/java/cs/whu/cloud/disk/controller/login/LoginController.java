package cs.whu.cloud.disk.controller.login;

import cs.whu.cloud.disk.controller.BaseController;
import cs.whu.cloud.disk.util.BaseUtils;
import cs.whu.cloud.disk.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


@Slf4j
@Controller
@RequestMapping("/")
public class LoginController extends BaseController {

	/*@RequestMapping("index")
	public String index(){
		return "index";
	}*/

	@RequestMapping("login")
	public String login(String userName,String pwd,HttpSession session) throws Exception {
		long userId = db.checkUser(userName, pwd);
		Json json = new Json();
		if (userId>0) {
			json.setSuccess(true);
			session.setAttribute("userid", userId);
			session.setAttribute("username", db.getUserNameById(userId));
		}else {
			json.setMsg("用户名或密码不正确");
		}
		return "redirect:index.jsp";
	}
	@RequestMapping("logout")
	public String logout(HttpSession session) {
		if (session!=null) {
			session.invalidate();
		}
		return LOGIN_URL;
	}

	@PostMapping("checkLogin")
	@ResponseBody
	public String checkLogin(HttpSession session){
		String name=(String)session.getAttribute("username");
		if(StringUtils.isEmpty(name)){
			return "notLogin";
		}else{
			return name;
		}
	}
	
	@RequestMapping("reg")
	public String reg(String email,String username,String password) throws Exception {
		if(BaseUtils.isNotEmpty(email) && BaseUtils.isNotEmpty(username) && BaseUtils.isNotEmpty(password)){
			long id = db.getGid("id_user");
			db.add("user_id", username, "id", "id", id);
			db.add("id_user", id, "user", "name", username);
			db.add("id_user", id, "user", "pwd", password);
			db.add("id_user", id, "user", "email", email);
			db.add("email_user", email, "user", "userid", id);
			hdfsDB.mkdir("/"+username);
		}
		return LOGIN_URL;
	}
	
	@RequestMapping("init")
	public String init() throws Exception {
		String table_gid = "gid";
		String[] fam_gid = {"gid"};
		db.createTable(table_gid, fam_gid,1);
		
		String table_id = "id_user";
		String[] fam_id = {"user"};
		db.createTable(table_id, fam_id,1);
		
		String table_user = "user_id";
		String[] fam_user = {"id"};
		db.createTable(table_user, fam_user,1);
		
		String table_email = "email_user";
		String[] fam_email = {"user"};
		db.createTable(table_email, fam_email,1);
		
		db.add(table_gid, "gid", "gid", "gid", (long)0);
		
		long id = db.getGid("id_user");
		db.add("user_id", "admin", "id", "id", id);
		db.add("id_user", id, "user", "name", "admin");
		db.add("id_user", id, "user", "pwd", "123");
		db.add("id_user", id, "user", "email", "978582067@qq.com");
		db.add("email_user", "978582067@qq.com", "user", "userid", id);

		hdfsDB.mkdir("/admin");
		
		String table_follow = "follow";
		String[] fam_follow_name = {"name"};
		db.createTable(table_follow, fam_follow_name,1);
		
		String table_followed = "followed";
		String[] fam_followed_userid = {"userid"};
		db.createTable(table_followed, fam_followed_userid,1);
		
		db.add("gid", "shareid", "gid", "shareid", (long)0);
		/*
		 * tableName:share
		 * rowkey:userid+shareid
		 * content:path,content:ts
		 */ 
		String table_share = "share";
		String[] fam_centent = {"content"};
		db.createTable(table_share, fam_centent,1);
		
		/*
		 * tableName:shareed
		 * rowkey:userid+userid+shareid
		 * shareid:
		 */ 
		String table_shareed = "shareed";
		String[] fam_shareid = {"shareid"};
		db.createTable(table_shareed, fam_shareid,1);
		
		db.add("gid", "bookid", "gid", "bookid", (long)0);
		//tableName:book
		//rowkey:userid+id
		//content:
		String table_book = "book";
		String[] fam_book_content = {"content"};
		db.createTable(table_book, fam_book_content,1);

		String fileSystem="filesystem";
		String[] fam_fileSystem={"files"};
		db.createTable(fileSystem, fam_fileSystem, 1);
		
		return LOGIN_URL;
	}
}
