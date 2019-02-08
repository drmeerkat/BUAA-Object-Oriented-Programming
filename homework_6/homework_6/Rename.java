package homework_6;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import javax.xml.transform.Templates;

public class Rename implements Runnable{
	private LinkedList<Integer> taskNO;
	private Snapshot snap;
	
	
	public Rename(Snapshot snap){
		this.taskNO = new LinkedList<>();
		this.snap = snap;
	}
	
	public void addTaskNO(int no) {
		this.taskNO.add(no);
	}
	
	public synchronized void trigger(){
		Iterator<Integer> iterator = taskNO.iterator();
		int NO;
		IFT temp_ift;
		String temp_workdir;
		String temp_pardir;
		File file;
		
		while(iterator.hasNext()){
//			按照任务编号循环
			NO = (int)iterator.next();
			temp_ift = Readin.ifts.get(NO);
			temp_workdir = temp_ift.getRoute();
			file = new File(temp_workdir);
			temp_pardir = file.getParent();
			if (temp_pardir.equals(null)){
				temp_pardir = temp_workdir;
			}
			
			TreeNode last_root = null;
			TreeNode direct_parent;
			for (TreeNode t:snap.getLastTrees()){
				if (t.getPath().equals(temp_pardir)){
					last_root = t;
					break;
				}
			}
			if (last_root != null){
				direct_parent = last_root.findTreeNodeByPath(temp_pardir);
				scanChange(temp_pardir, temp_workdir, NO, direct_parent);
			}
			else {
				System.out.println("监控区为空，当前监视器将停止运行");
				taskNO.remove(NO);
			}
		}
	}
	
	public void scanChange(String pardir, String workdir, int NO, TreeNode direct_parent){
//		此处默认pardir是目录，workdir为文件
		
		TreeNode par_node = null, wor_node = null, last_node = null, last_root = null;		
		boolean deleteFlag = true;
		for (TreeNode t:snap.getLastTrees()){
			if (t.getPath().equals(pardir)){
				last_root = t;
				break;
			}
		}
		last_node = last_root.findTreeNodeByPath(workdir);
		
		for (TreeNode t:snap.getTrees()){
			if (t.getPath().equals(pardir)){
				par_node = t;
				break;
			}
		}
		if (par_node == null){
			System.out.println("父目录不存在，该监视器将结束监视");
//			此处处理删除任务和结束某个监视器
			return;
		}
		
		
		
		if (!last_node.isFile){
			for (TreeNode t:last_node.getJuniors()){
				scanChange(pardir, workdir, NO, t.getParentNode());
			}
		}
		
		for (TreeNode t:direct_parent.getChildList()){//新快照中父目录的子目录是不是有符合条件的文件
			if (t.equals(last_node)) return;//老文件还在
			if (t.getLast_time() == last_node.getLast_time() && t.getSize() == last_node.getSize()){
				Readin.routes.add(t.getPath());
				Readin.ifts.get(NO).setRoute(t.getPath());
//				触发任务???????
				if (Readin.ifts.get(NO).getTask().equals("1")){
					snap.summary.record(NO, Readin.ifts.get(NO).getTrigger());
				}
				else if (Readin.ifts.get(NO).getTask().equals("2")){
					snap.detail.record(NO, Readin.ifts.get(NO).getTrigger(), last_node, t);
				}
				else if (Readin.ifts.get(NO).getTask().equals("3")){
					snap.recover.recover(last_node, t);
				}
				else System.out.println("Wrong task number");
				return;
			}
		}
		return;
	}
	
	@Override
	public void run() {
		// TODO 自动生成的方法存根
		try {
			while (!Thread.interrupted()){
				synchronized (snap){
					while (!snap.newsnap){
						wait();
					}
					trigger();
					snap.exec_count += 1;
					if (snap.exec_count == 4) snap.newsnap = false;
				}
			
				snap.notifyAll();
			}
		}catch (InterruptedException e){
		
		}
		
	}











}
