package cs.whu.cloud.disk.controller.cloud;

import cs.whu.cloud.disk.controller.BaseController;
import cs.whu.cloud.disk.util.*;
import cs.whu.cloud.disk.vo.FileSystemVo;
import cs.whu.cloud.disk.vo.Menu;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Slf4j
@Controller
@RequestMapping("/cloud")
public class CloudController extends BaseController {

	@RequestMapping("/list")
	public String list(String name,HttpSession session,Model model) throws Exception {
		String userName = (String) session.getAttribute("username");
		if(StringUtils.isEmpty(userName)){
			return LOGIN_URL;
		}

		if (StringUtils.isEmpty(name)) {
			name = "/"+userName;
		}else{
			name=FileUtils.getStandardPath("/"+name);
		}
		//model.addAttribute("fs", db.getFile(name));
		model.addAttribute("fs", hdfsDB.queryAll(name));
		model.addAttribute("dir", name);
		model.addAttribute("url", BaseUtils.getUrl(name));
		return "/cloud/list";
	}
	/**
	 * 创建目录
	 * @param mkdir
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/mkdir")
	public Json mkdir(String mkdir, String dirName, HttpSession session) {
		Json json = new Json();
		/*if(!BaseUtils.isNotEmpty(mkdir)){
			json.setMsg("空值无效");
			return json;
		}*/
		if(StringUtils.isEmpty(dirName)){
			json.setMsg("文件夹名字不能为空");
			return json;
		}
		String name = (String) session.getAttribute("username");
		if (name==null) {
			json.setMsg("用户已注销，请重新登陆");
			return json;
		}
		try {
			String dir = split+dirName;;
			//在该用户下创建目录
			if(hdfsDB.checkExists(dir)){
				hdfsDB.mkdir(dir+split+mkdir);
			}

			/*long id = db.getGid();
			db.add("filesystem", id, "files", "name", mkdir);
			db.add("filesystem", id, "files", "dir", dir);
			db.add("filesystem", id, "files", "pdir", dir.substring(0, dir.lastIndexOf(split)));
			db.add("filesystem", id, "files", "type", "D");*/
			
			FileSystemVo fs = new FileSystemVo();
//			fs.setId(id);
			fs.setName(mkdir);
			fs.setType("D");
			fs.setDate(DateUtil.DateToString("yyyy-MM-dd HH:mm", new Date()));
			
			json.setObj(fs);
			json.setMsg("创建成功");
			json.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			json.setMsg("创建失败");
		}
		return json;
	}
	
	/**
	 * 上传文件
	 * @param dir
	 * @param session
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/upload")
	public Json upload(String dir,HttpSession session,HttpServletRequest request) throws Exception {
		Json json = new Json();
		String name = (String) session.getAttribute("username");
		if (name==null) {
			json.setMsg("用户已注销，请重新登陆");
			return json;
		}
		//获取用户目录
		if(StringUtils.isEmpty(dir)){
			dir = FileUtils.getStandardPath(split+name);
		}else{
			dir = FileUtils.getStandardPath(split+dir);
		}

		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getServletContext());
		if (multipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			Map<String, MultipartFile> fms = multipartRequest.getFileMap();
			for (Map.Entry<String, MultipartFile> entity : fms.entrySet()) {
				MultipartFile mf = entity.getValue();
				InputStream in = mf.getInputStream();
				String destination=dir+ split+mf.getOriginalFilename();
				hdfsDB.upload(in, destination);
				long id = db.getGid("filesystem");
				db.add("filesystem", id, "files", "name", mf.getOriginalFilename());
				db.add("filesystem", id, "files", "dir", dir);
				/*System.out.println(dir);
				System.out.println(dir.substring(0, dir.lastIndexOf(split)));*/
				db.add("filesystem", id, "files", "pdir", dir.substring(0, dir.lastIndexOf(split)));
				db.add("filesystem", id, "files", "type", "F");
				db.add("filesystem", id, "files", "size", BaseUtils.FormetFileSize(mf.getSize()));

				in.close();
				json.setSuccess(true);
				log.info("用户:{}，上传文件:\"{}\"到\"{}\"成功,文件大小:{}kb",name,mf.getOriginalFilename(),destination,mf.getSize()*1.0/1024);
			}
		}
		return json;
	}
	/**
	 * 删除文件及文件夹
	 * @param ids
	 * @param dir
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/delete")
	public Json delete(String ids,String dir) throws Exception {
		Json json = new Json();
		try {
			String[] ns = ids.split(",");
			for (int i = 0; i < ns.length; i++) {
				hdfsDB.delete(dir+split+ns[i]);
			}
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			json.setMsg("删除失败");
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 复制文件及文件夹
	 * @param ids
	 * @param dir
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/copy")
	public Json copy(String ids,String dir,String dst,boolean flag) throws Exception {
		Json json = new Json();
		String[] ns = ids.split(",");
		for (int i = 0; i < ns.length; i++) {
			ns[i] = dir+split+ns[i];
		}
		try {
			hdfsDB.copy(ns, dst, flag);
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			json.setMsg("删除失败");
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 重命名文件及文件夹
	 * @param dir
	 * @param name
	 * @param rename
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/rename")
	public Json rename(String dir,String name,String rename,String type) throws Exception {
		Json json = new Json();
		try {
			if(type.equals("F")){
				hdfsDB.rename(dir+split+name, dir+split+rename+name.substring(name.lastIndexOf(".")));
			}else if (type.equals("D")) {
				hdfsDB.rename(dir+split+name, dir+split+rename);
			}
			json.setSuccess(true);
			json.setMsg("重命名成功");
		} catch (Exception e) {
			json.setMsg("删除失败");
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 查看文档
	 * @param dir
	 * @param name
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/view")
	public String view(String dir,String name,HttpServletRequest request,Model model) throws Exception {
		String local = request.getServletContext().getRealPath("/test");
		String swfFile = FileUtils.getFilePrefix(local+"\\"+name)+".swf";
		File outFile = new File(swfFile);
		if(outFile.exists()){
			model.addAttribute("local", "test/"+name.substring(0,name.lastIndexOf("."))+".swf");
			return "/cloud/view";
		}
		String pdfFile = FileUtils.getFilePrefix(local+"\\"+name)+".pdf";
		File outFile01 = new File(pdfFile);
		if(!outFile01.exists()){
			hdfsDB.downLoad(dir+split+name, local+"\\"+name);
			OfficeToSwf.convert2PDF(local+"\\"+name);
		}else{
			OfficeToSwf.pdftoswf(pdfFile);
		}
		model.addAttribute("local", "test/"+name.substring(0,name.lastIndexOf("."))+".swf");
		return "/cloud/view";
	}
	@RequestMapping("/download")
	public String download(String dir,String name,HttpServletRequest request,Model model) throws Exception {
		String local = request.getServletContext().getRealPath("/test");
		File f = new File(local+"\\"+name);
		if (!f.exists()) {
			hdfsDB.downLoad(dir+split+name, local+File.separator+name);
		}
		model.addAttribute("name", name);
		return "/cloud/down";
	}
	/**
	 * 分享
	 * @param dir
	 * @param names
	 * @param usernames
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/share")
	public Json share(String dir,String names,String usernames,String types,HttpSession session) throws Exception {
		Json json = new Json();
		String name = (String) session.getAttribute("username");
		if (name==null) {
			json.setMsg("用户已注销，请重新登陆");
			return json;
		}
		if(StringUtils.isEmpty(names)||StringUtils.isEmpty(usernames)){
		    if(StringUtils.isEmpty(names)){
                json.setMsg("请选择要分享的文件！");
            }else{
                json.setMsg("请选择要分享的用户！");
            }
		    return json;
        }
		String[] n = names.split(",");
		String[] t = types.split(",");
		String[] u = usernames.split(",");
		for (int i = 0; i < u.length; i++) {
			try {
				db.share(dir, name, n, t, u[i]);
			} catch (Exception e) {
				json.setMsg("分享失败");
				e.printStackTrace();
				return json;
			}
		}
		json.setSuccess(true);
		return json;
	}
	/**
	 * 获取分享
	 * @param session
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getshare")
	public String getshare(HttpSession session,Model model) throws Exception {
		String name = (String) session.getAttribute("username");
		if (name==null) {
			return LOGIN_URL;
		}
		model.addAttribute("shares", db.getshare(name));
		return "/cloud/share";
	}
	/**
	 * 获取被分享
	 * @param session
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getshareed")
	public String getshareed(HttpSession session,Model model) throws Exception {
		String name = (String) session.getAttribute("username");
		if (name==null) {
			return LOGIN_URL;
		}
		model.addAttribute("shares", db.getshareed(name));
		return "/cloud/shareed";
	}
	/**
	 * 查询被分享
	 * @param dir
	 * @param path
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/shareedList")
	public String shareedList(String dir,String path,Model model) throws Exception {
		if (dir==null) {
			dir=path;
		}else{
			dir=dir+split+path;
		}
		List<FileSystemVo> list = hdfsDB.queryAll(dir);
		model.addAttribute("shares", list);
		model.addAttribute("dir", dir);
		return "/cloud/shareed_list";
	}
	/**
	 * 记事本列表
	 * @param session
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/booklist")
	public String booklist(HttpSession session,Model model) throws Exception {
		String name = (String) session.getAttribute("username");
		if (name==null) {
			return LOGIN_URL;
		}
		model.addAttribute("books", db.listbook(name));
		return "/cloud/book";
	}
	/**
	 * 新增记事本
	 * @param content
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/bookadd")
	public Json bookadd(String content,HttpSession session) throws Exception {
		Json json = new Json();
		String name = (String) session.getAttribute("username");
		if (name==null) {
			json.setMsg("用户已注销，请重新登陆");
			return json;
		}
		try {
			db.addbook(name, content);
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("记事本添加失败");
			e.printStackTrace();
		}
		return json;
	}
	@ResponseBody
	@RequestMapping("/tree")
	public List<Menu> tree(String id, HttpSession session) throws Exception {
		List<Menu> menus = null;
		if (BaseUtils.isNotEmpty(id)) {
			menus = hdfsDB.tree(id);
		}else{
			String name = (String) session.getAttribute("username");
			if (name==null) {
				return null;
			}
			menus = hdfsDB.tree(split+name);
		}
		return menus;
	}
}
