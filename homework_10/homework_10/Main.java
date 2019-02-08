package homework_10;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*Overview
 * 这个类主要用于初始化各种线程，并启动他们。作为整个工程的入口函数。
 * 
 */

public class Main {
	protected static void deleteFile(File file){
		/*@REQUIRES: (\all File;file.exists())
		 *@MODIFIES: file
		 *@EFFECTS: 递归删除文件夹file的所有子文件，子目录及其子目录中的内容
		 */
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(int i=0; i<files.length; i++){
				deleteFile(files[i]);
			}
		}
		file.delete();
	}
	
	
	public boolean repOK(){
		/*
		 * @REQUIRES:	None;
		 * @MODIFIES:	None;
		 * @EFFECTS:	Checks if "this" is legal.
		 */
		return true;
	}
	
	public static void main(String[] args) {
		/*@REQUIRES: None
		 *@MODIFIES: gui
		 *			 mi
		 *			 bq
		 *			 readin
		 *			 dir_re
		 *			 dir_taxis
		 *			 taxiSystem
		 *           executor
		 *@EFFECTS: normal_behaviour:初始化各个变量和进程，加入进程池并启动进程
		 *			exceptional_behaviour:打印提示信息并终止运行
		 */
		TaxiGUI gui=new TaxiGUI();
		BlockedRequestQueue bq = new BlockedRequestQueue();
		Readin readin = new Readin(bq);
		gui.LoadMap(Readin.map, 80);
		Light read_light = new Light("lightmap.txt", gui);
		try {
			File dir_re = new File("Request_log/");
			File dir_taxis = new File("Taxi_log/");
			if (dir_re.exists()){
				Main.deleteFile(dir_re);
			}
			if (dir_taxis.exists()){
				Main.deleteFile(dir_taxis);
			}
			dir_re.mkdir();
			dir_taxis.mkdir();
			TaxiSystem taxiSystem = new TaxiSystem(bq, Readin.map, gui, read_light);
			ExecutorService executor = Executors.newCachedThreadPool();
			TimeUnit.MICROSECONDS.sleep(1000);
			executor.execute(readin);
			executor.execute(taxiSystem);
			executor.execute(read_light);
			
			while(true){
				TimeUnit.MILLISECONDS.sleep(1000);
				if (Readin.endflag && TaxiSystem.endflag){
					executor.shutdownNow();
					TimeUnit.MILLISECONDS.sleep(100);
					break;
				}
			}
		} catch (InterruptedException e) {
//			e.printStackTrace();
			System.out.println("TaxiSystem closed");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to make new dirs or something bad happened");
			System.exit(0);
		}
		finally {
			System.out.println("Finished running. Please checkout the log in your working directory");
			System.exit(0);
		}
		
	}

}
