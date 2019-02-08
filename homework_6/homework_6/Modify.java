package homework_6;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

public class Modify implements Runnable{
	private LinkedList<Integer> taskNO;
	private Snapshot snap;
	
	
	public Modify(Snapshot snap){
		this.taskNO = new LinkedList<>();
		this.snap = snap;
	}
	
	public void addTaskNO(int no) {
		this.taskNO.add(no);
	}
	
	public void trigger(){
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
			scanChange(temp_pardir, temp_workdir,NO);
//			触发任务！!!!!!!
			if (temp_ift.getTask().equals("1")){}
			else if (temp_ift.getTask().equals("2")){}
			else if (temp_ift.getTask().equals("3")){}
			else System.out.println("Wrong task number");
		}
	}
	
	public void scanChange(String pardir, String workdir, int NO){
//		此处默认pardir是目录，workdir为文件，或者非顶层被监控目录
		File file = new File(workdir);
		if (file.isDirectory()){
			for (File temp_file:file.listFiles()){
				scanChange(pardir, temp_file.getAbsolutePath(), NO);
			}
		}
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
		for (TreeNode t:par_node.getChildList()) {
			if (t.getName().equals(last_node.getName()) && t.getLast_time() != last_node.getLast_time()){
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
