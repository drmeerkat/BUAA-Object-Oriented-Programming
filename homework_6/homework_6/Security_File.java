package homework_6;

import java.io.File;

public class Security_File {
//	此类为线程安全的file类
	
	private File file;
	public Security_File(File file) {
		// TODO 自动生成的构造函数存根
		this.file = file;
	}
	
	public synchronized void renameTo(File dest){
		file.renameTo(dest);
	}
	
	public synchronized String getName(){
		return file.getName();
	}
	
	public synchronized String getAbsolutePath(){
		return file.getAbsolutePath();
	}
	
	public synchronized boolean exists(){
		return file.exists();
	}
	
	public synchronized boolean isFile(){
		return file.isFile();
	}
	
	public synchronized boolean isDirector(){
		return file.isDirectory();
	}
	
	public synchronized long length(){
		return file.length();
	}
	
	public synchronized long lastModified(){
		return file.lastModified();
	}
	
}
