package cs.whu.cloud.disk.db;

import cs.whu.cloud.disk.config.IConstant;
import cs.whu.cloud.disk.util.BaseUtils;
import cs.whu.cloud.disk.util.DateUtil;
import cs.whu.cloud.disk.util.FileUtils;
import cs.whu.cloud.disk.util.SiteUrl;
import cs.whu.cloud.disk.vo.FileSystemVo;
import cs.whu.cloud.disk.vo.Menu;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HdfsDB {

	private static String[] suf = {"csv","txt","doc","docx","xls","xlsx","ppt","pptx"};
	private static final String ROOT = IConstant.ROOT;
	static FileSystem fs;
	static Configuration conf;

	private static class HdfsDBInstance {
		private static final HdfsDB instance = new HdfsDB();
	}

	public static HdfsDB getInstance() {
		return HdfsDBInstance.instance;
	}

	private HdfsDB() {
		conf = new Configuration();
		conf.set("fs.defaultFS", SiteUrl.readUrl("hdfs"));
		try {
			fs = FileSystem.get(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上传文件
	 * @param filePath
	 * @param dir
	 * @throws Exception
	 */
	public void upload(String filePath, String dir) throws Exception {
		InputStream in = new BufferedInputStream(new FileInputStream(filePath));
		OutputStream out = fs.create(new Path(ROOT + dir), new Progressable() {

			@Override
			public void progress() {
				//System.out.println("ok");
			}
		});
		IOUtils.copyBytes(in, out, 4096, true);
	}
	/**
	 * 已流形式上传
	 * @param in
	 * @param dir
	 * @throws Exception
	 */
	public void upload(InputStream in, String dir) throws Exception {
		OutputStream out = fs.create(new Path(ROOT + dir), new Progressable() {
			@Override
			public void progress() {
				//System.out.println("ok");
			}
		});
		IOUtils.copyBytes(in, out, 4096, true);
	}
	/**
	 * 下载文件
	 * @param path
	 * @param local
	 * @throws Exception
	 */
	public void downLoad(String path,String local) throws Exception {
		FSDataInputStream in = fs.open(new Path(ROOT+path));
		OutputStream out = new FileOutputStream(local);
		IOUtils.copyBytes(in, out, 4096, true);
	}
	/**
	 * 重命名文件
	 * @param src
	 * @param dst
	 * @throws Exception
	 */
	public void rename(String src,String dst) throws Exception {
		fs.rename(new Path(ROOT+src), new Path(ROOT+dst));
	}

	/**
	 * 创建文件夹
	 * @param dir
	 * @throws Exception
	 */
	public void mkdir(String dir) throws Exception {
		if (!fs.exists(new Path(ROOT+dir))) {
			fs.mkdirs(new Path(ROOT+dir));
		}
	}
	/**
	 * 删除文件及文件夹
	 * @param name
	 * @throws Exception
	 */
	public void delete(String name) throws Exception {
		fs.delete(new Path(ROOT+name), true);
	}

	/**
	 * 查询文件夹
	 * @param dir
	 * @return
	 * @throws Exception
	 */
	public List<FileSystemVo> queryAll(String dir) throws Exception {
		FileStatus[] files = fs.listStatus(new Path(ROOT+dir));
		List<FileSystemVo> fileVos = new ArrayList<FileSystemVo>();
		FileSystemVo f = null;
		for (int i = 0; i < files.length; i++) {
			f = new FileSystemVo();
			if (files[i].isDirectory()) {
				f.setName(files[i].getPath().getName());
				f.setType("D");
				f.setDate(DateUtil.longToString("yyyy-MM-dd HH:mm", files[i].getModificationTime()));
				f.setNamep(files[i].getPath().getName());
			} else if (files[i].isFile()) {
				f.setName(files[i].getPath().getName());
				f.setType("F");
				f.setDate(DateUtil.longToString("yyyy-MM-dd HH:mm", files[i].getModificationTime()));
				f.setSize(BaseUtils.FormetFileSize(files[i].getLen()));
				f.setNamep(f.getName().substring(0, f.getName().lastIndexOf(".")));
				String s= FileUtils.getFileSufix(f.getName());
				for (int j = 0; j < suf.length; j++) {
					if (s.equals(suf[j])) {
						f.setViewflag("Y");
						break;
					}
				}
			}
			fileVos.add(f);
		}
		return fileVos;
	}
	/**
	 * 移动或复制文件
	 * @param path
	 * @param dst
	 * @param src true 移动文件;false 复制文件
	 * @throws Exception
	 */
	public void copy(String[] path, String dst,boolean src) throws Exception {
		Path[] paths = new Path[path.length];
		for (int i = 0; i < path.length; i++) {
			paths[i]=new Path(ROOT+path[i]);
		}
		FileUtil.copy(fs, paths, fs, new Path(dst), src, true, conf);
	}
	
	public List<Menu> tree(String dir) throws Exception {
		if(dir==null){
			return null;
		}
		Path path=null;
		if(dir.startsWith("hdfs")==false){
			path=new Path(ROOT+dir);
		}else{
			path=new Path(dir);
		}

		FileStatus[] files = fs.listStatus(path);
		List<Menu> menus = new ArrayList<Menu>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				menus.add(new Menu(files[i].getPath().toString(), files[i].getPath().getName()));
			}
		}
		return menus;
	}

	public boolean checkExists(String path){
		try {
			return fs.exists(new Path(ROOT+path));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args) throws Exception {
		HdfsDB hdfsDB = new HdfsDB();
		hdfsDB.mkdir(ROOT+"weir33/qq");

		// String path = "C://Users//Administrator//Desktop//jeeshop-jeeshop-master.zip";
		// hdfsDB.upload(path, "weir/"+"jeeshop.zip");
		// hdfsDB.queryAll(ROOT);
//		hdfsDB.visitPath("hdfs://h1:9000/weir");
//		for (Menu menu : menus) {
//			System.out.println(menu.getName());
//			System.out.println(menu.getPname());
//		}
//		hdfsDB.delete("weirqq");
//		hdfsDB.mkdir("/weirqq");
//		hdfsDB.tree("/admin");
		System.out.println("ok");
	}
}
