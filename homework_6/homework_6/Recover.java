package homework_6;

import java.io.File;

public class Recover {
	
	public boolean recover(TreeNode last_time, TreeNode now){
		File file = new File(now.getPath());
		if (file.exists()){
			if (!now.getName().equals(last_time.getName())){
				String path = now.getParentNode().getPath();
				File oldfile=new File(path+"/"+now.getName()); 
	            File newfile=new File(path+"/"+last_time.getName()); 
	            if(!oldfile.exists()){
	                return false;//重命名文件不存在
	            }
	            if(newfile.exists()){//若在该目录下已经有一个文件和新文件名相同，则不允许重命名 
	                System.out.println(newfile.getAbsolutePath()+" 已经存在！");
	            	return false;
	            }
	            else{ 
	                oldfile.renameTo(newfile);
	                return true;
	            }
			}
			else if (!now.getPath().equals(last_time.getPath())){
				String path = now.getParentNode().getPath();
				File oldfile=new File(path+"/"+now.getName()); 
	            File newfile=new File(last_time.getPath()); 
	            if(!oldfile.exists()){
	                return false;//重命名文件不存在
	            }
	            if(newfile.exists()){//若在该目录下已经有一个文件和新文件名相同，则不允许重命名 
	                System.out.println(newfile.getAbsolutePath()+" 已经存在！");
	            	return false;
	            }
	            else{ 
	                oldfile.renameTo(newfile);
	                return true;
	            }
			}
			return false;
		}
		
		else {
			return false;
		}
	}
}
