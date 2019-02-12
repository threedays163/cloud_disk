package cs.whu.cloud.disk.controller;

import cs.whu.cloud.disk.db.HbaseDB;
import cs.whu.cloud.disk.db.HdfsDB;

public class BaseController {

//	public HbaseDB db = HbaseDB.getInstance();
//	public HdfsDB hdfsDB = HdfsDB.getInstance();
	public HbaseDB db = null;
	public HdfsDB hdfsDB = null;
}
