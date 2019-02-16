package cs.whu.cloud.disk.controller;

import cs.whu.cloud.disk.db.HbaseDB;
import cs.whu.cloud.disk.db.HdfsDB;

public class BaseController {

	public static String LOGIN_URL="redirect:/login.jsp";//"";

	public static String split="/";

	//public static HbaseDB db = HbaseDB.getInstance();
	//public static HdfsDB hdfsDB = HdfsDB.getInstance();
	public static HbaseDB db = null;
	public static HdfsDB hdfsDB = null;
}
