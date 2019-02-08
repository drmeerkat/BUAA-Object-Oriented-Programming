package homework_6;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedList;

public class Summary {
	private LinkedList<logdata> log;
	private class logdata{
		private int trigger_num;
		private int task;
		public logdata(int task) {
			trigger_num = 1;
			this.task = task;
		}
		@Override
		public String toString(){
			return String.valueOf(task)+" Triggerd time: "+String.valueOf(trigger_num);
		}
		public void add(){
			trigger_num += 1;
		}
		public int get(){
			return trigger_num;
		}
	}
	
	
	
	public Summary(){
		log = new LinkedList<>();
	}
	
	public void record(int taskNO, String trigger){
		if (log.get(taskNO) != null){
			log.get(taskNO).add();
		}
		else {
			log.add(new logdata(taskNO));
		}
	}
	
	public void output(){
		try{
			File file = new File("D:/Summary.txt");
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
