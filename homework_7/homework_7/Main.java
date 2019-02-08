package homework_7;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
class mapInfo{
	int[][] map=new int[80][80];
	public void readmap(String path){//读入地图信息
		//Requires:String类型的地图路径,System.in
		//Modifies:System.out,map[][]
		//Effects:从文件中读入地图信息，储存在map[][]中
		Scanner scan=null;
		File file=new File(path);
		if(file.exists()==false){
			System.out.println("地图文件不存在,程序退出");
			System.exit(1);
			return;
		}
		try {
			scan = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			
		}
		for(int i=0;i<80;i++){
			String[] strArray = null;
			try{
				strArray=scan.nextLine().split("");
			}catch(Exception e){
				System.out.println("地图文件信息有误，程序退出");
				System.exit(1);
			}
			for(int j=0;j<80;j++){
				try{
					this.map[i][j]=Integer.parseInt(strArray[j]);
				}catch(Exception e){
					System.out.println("地图文件信息有误，程序退出");
					System.exit(1);
				}
			}
		}
		scan.close();
	}
}
public class Main {
	protected static void deleteFile(File file){
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(int i=0; i<files.length; i++){
				deleteFile(files[i]);
			}
		}
		file.delete();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TaxiGUI gui=new TaxiGUI();
		mapInfo mi=new mapInfo();
		mi.readmap("mapgui.txt");
		gui.LoadMap(mi.map, 80);
		BlockedRequestQueue bq = new BlockedRequestQueue();
		Readin readin = new Readin(bq);
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
			TimeUnit.MILLISECONDS.sleep(1000);
			TaxiSystem taxiSystem = new TaxiSystem(bq, readin.map, gui);
			ExecutorService executor = Executors.newCachedThreadPool();
			executor.execute(readin);
			TimeUnit.MILLISECONDS.sleep(1000);
			executor.execute(taxiSystem);
			
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
			// TODO: handle exception
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
