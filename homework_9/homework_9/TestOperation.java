package homework_9;

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
		/*@REQUIRES: (\all TaxiSystem; sys != null)
		 *@MODIFIES: this.testSys
		 *			 testlog
		 *			 mission_num
		 *@EFFECTS: initialize all the variables
		 */
		this.testSys = sys;
		testlog = new LinkedList<>();
		mission_num = 0;
	}
	public long checkTime(){
		/*@REQUIRES: None
		 *@MODIFIES: testlog
		 *@EFFECTS:  返回当前出租车系统时间
		 *			  将这个查询时间的任务结果输出到log中
		 */
		testlog.add("Now "+String.valueOf(testSys.getClock()));
		return testSys.getClock();
	}
	
	public void checkState(String state,long time){
		/*@REQUIRES: (\all String;state == waiting || state == stop ||state == serving ||state == order_taking;
		 * 			  \all long; 0 <= time <= LONGMAX 				
		 * )
		 *@MODIFIES: testlog
		 *			 mission_num
		 *@EFFECTS: 输出指定时刻时处于指定状态的出租车列表，并将结果输出到log
		 */
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
		/*@REQUIRES: ( \all int;0<=NO<=MAXINT;
		 * 			   \all long;  0<=time<=MAXLONG
		 * )
		 *@MODIFIES: testlog
		 *			 mission_num
		 *@EFFECTS: 查询指定时刻指定出租车的状态
		 */
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
		/*@REQUIRES: (\all String;re != null;
		 *			  \all long; 0<= time <= MAXLONG                             
		 *)
		 *@MODIFIES: testlog
		 *			 mission_num
		 *			 Readin.request_count
		 *			 block_kq
		 *@EFFECTS: 添加新的CR请求到请求队列，并增加当前请求数量
		 */
		long temp = time/100*100;
		if (testSys.getClock() == temp){
			mission_num++;
			testlog.add("-----------------------------------------");
			testlog.add(time+": "+mission_num+" feedRequest");
//			添加请求，记录请求编号
			if (Readin.cr_request(re)){
				Request temp_re = new Request(Readin.request_count,re,temp);
				Readin.request_count.incrementAndGet();
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
		/*@REQUIRES: None
		 *@MODIFIES: File("testLog.txt")
		 *@EFFECTS: 输出测试log信息到testlog.txt
		 */
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
