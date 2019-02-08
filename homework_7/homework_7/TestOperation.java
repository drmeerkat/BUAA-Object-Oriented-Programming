package homework_7;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedList;

public class TestOperation {
	private TaxiSystem testSys;
	private LinkedList<String> testlog;
	private int mission_num;
	
	public TestOperation(TaxiSystem sys) {
		this.testSys = sys;
		testlog = new LinkedList<>();
		mission_num = 0;
	}
	public long checkTime(){
		testlog.add("Now "+String.valueOf(testSys.getClock()));
		return testSys.getClock();
	}
	
	public void checkState(String state,long time){
		long temp = time/100*100;
		if (testSys.getClock() == temp){
			int i;
			mission_num++;
			testlog.add("-----------------------------------------");
			testlog.add(time+": "+mission_num+" checkState: "+state);
			for (i = 0;i<100;i++){
				if (testSys.getTaxi(i+1).getState().equals(state)){
					testlog.add(testSys.getTaxi(i+1).toString());
				}
			}
		}
	}
 	
	public void checkTaxi(int NO,long time){
//		将时间直接截断到100ms 查询100ms为单位的状态
		long temp = time/100*100;
		if (testSys.getClock() == temp){
			mission_num++;
			testlog.add("-----------------------------------------");
			testlog.add(time+": "+mission_num+" checkTaxi");
			testlog.add(testSys.getTaxi(NO).toString());
		}
	}
	
	public void feedRequest(String re,long time){
		long temp = time/100*100;
		if (testSys.getClock() == temp){
			mission_num++;
			testlog.add("-----------------------------------------");
			testlog.add(time+": "+mission_num+" feedRequest");
//			添加请求，记录请求编号
			if (Readin.cr_request(re)){
				Request temp_re = new Request(Readin.request_count,re,temp);
				Readin.request_count++;
				testSys.blockq.put(temp_re);
				testlog.add("Add request succeed. NO."+temp_re.getId());
				testlog.add("You can check ./Request_log/"+temp_re.getId()+".txt");
				System.out.println("Add success");
			}
			else{
				testlog.add("Invalid request");
			}
		}
	}
	
	public void outLog(){
		try{
			LinkedList<String> info;
			info = testlog;
			Iterator<String> iterator = info.iterator();
			String temp;
			File file = new File("testLog.txt");
			FileWriter fw = null;
			BufferedWriter bw = null;
			if (file.exists() && file.isFile())
				file.delete();
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			while (iterator.hasNext()){
				temp = iterator.next();
				bw.write(temp);
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
