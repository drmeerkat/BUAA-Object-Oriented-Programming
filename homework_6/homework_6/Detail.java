package homework_6;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedList;


public class Detail {
	private LinkedList<logdata> log;
	private class logdata{
		private int trigger_num;
		private int task;
		private String record;
		public logdata(int task, String trigger, TreeNode last, TreeNode now) {
			trigger_num = 0;
			this.task = task;
			refreshData(trigger, last, now);
		}
		
		private void refreshData(String trigger,TreeNode last, TreeNode now){
			add();
			if (trigger.equals("1")){
				record = last.getName() + " is renamed to " + now.getName();
			}
			else if (trigger.equals("2")){
				record = "last modified time: "+ String.valueOf(last.getLast_time()) + "new modified time: "+String.valueOf(now.getLast_time());
			}
			else if (trigger.equals("3")){
				record = "old path: "+last.getPath()+"  new path: "+now.getPath();
			}
			else if (trigger.equals("4")){
				record = "old size: "+last.getSize()+"  new size: "+now.getSize();
			}
		}
			
		@Override
		public String toString(){
			return String.valueOf(task)+" Triggerd time: "+String.valueOf(trigger_num)+record;
		}
		public void add(){
			trigger_num += 1;
		}
		public int get(){
			return trigger_num;
		}
	}
	
	
	
	public Detail(){
		log = new LinkedList<>();
	}
	
	public void record(int taskNO, String trigger, TreeNode last, TreeNode now){
		if (log.get(taskNO) != null){
			log.get(taskNO).refreshData(trigger, last, now);
		}
		else {
			log.add(new logdata(taskNO, trigger, last, now));
		}
	}
	
	public void output(){
		try{
			File file = new File("D:/Detail.txt");
			FileWriter fw = null;
			BufferedWriter bw = null;
			if (file.exists() && file.isFile())
				file.delete();
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			Iterator<IFT> ite = Readin.ifts.iterator();
			while (ite.hasNext()){
				IFT data = (IFT)ite.next();
				if (log.get(data.getID()) != null){
					bw.write(data.toString());
					bw.newLine();
					bw.flush();
				}
			}
			
			
			Iterator<logdata> it = log.iterator();
			while (it.hasNext()){
				logdata data = (logdata)it.next();
				bw.write(data.toString());
				bw.newLine();
				bw.flush();
			}
			bw.close();
            fw.close();
		}catch (Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}
}
