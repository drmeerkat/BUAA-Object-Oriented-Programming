package homework_5;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main {
	protected static LinkedBlockingQueue<String> output;
	protected ExecutorService exec;
	protected BlockedRequest_queue block_rq;
	protected Readin readin;
	protected Multi_scheduler multi_scheduler;
	protected Elevator_System elevator_system;
	
	public Main(){
		output = new LinkedBlockingQueue<String>();
		exec = Executors.newCachedThreadPool();
		block_rq = new BlockedRequest_queue();
		readin = new Readin(block_rq);
		multi_scheduler = new Multi_scheduler(block_rq);
		elevator_system = multi_scheduler.getElesys();
	}
	
	public static void main(String[] args) {
		Main test =  new Main();
		test.exec.execute(test.readin);
		test.exec.execute(test.elevator_system);
		test.exec.execute(test.multi_scheduler);
		
		try {
			TimeUnit.MILLISECONDS.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		
		while (true){
			try {
				TimeUnit.MILLISECONDS.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if (test.elevator_system.isEnd() && test.readin.endflag){
				test.exec.shutdownNow();
				try{
					File file = new File("D:/result.txt");
					FileWriter fw = null;
					BufferedWriter bw = null;
					if (file.exists() && file.isFile())
						file.delete();
					fw = new FileWriter(file);
					bw = new BufferedWriter(fw);
					while (!output.isEmpty()){
//					System.out.println(System.getProperty("user.dir"));
						bw.write(output.take());
						bw.newLine();
						bw.flush();
					}
					bw.close();
		            fw.close();
					break;
				}catch (Exception e){
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
	}

}
