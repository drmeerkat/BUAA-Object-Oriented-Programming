package homework_7;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;




public class TaxiSystem implements Runnable {
	private Taxi [] taxis;
	private LinkedList<Request> request_queue;
	private long clock;
	protected BlockedRequestQueue blockq;
	private TaxiGUI gui;
	private HashMap<String, Integer> stateToint;
	protected static boolean endflag = false;
	private EditYourCode editYourCode;
	
	public TaxiSystem(BlockedRequestQueue bq, int map[][], TaxiGUI gui) {
		stateToint = new HashMap<>();
		stateToint.put("stop", 0);
		stateToint.put("serving", 1);
		stateToint.put("waiting", 2);
		stateToint.put("order_taking", 3);
		blockq = bq;
		taxis = new Taxi[100];
		request_queue = new LinkedList<>();
		clock = 0;
		this.gui = gui;
		for (int i = 0;i<100;i++){
			taxis[i] = new Taxi(i+1, map); //id 1-100
			gui.SetTaxiStatus(i, taxis[i].getGuiPoint(), stateToint.get(taxis[i].getState()));
		}
		editYourCode = new EditYourCode(this);
	}
	
	public void scheduler(){
		int i;
		Request temp_re;
		Iterator<Request> it = request_queue.iterator();
		while (it.hasNext()){
			temp_re = it.next();
			
			if (temp_re.getStart_time() == clock){
//				start to broadcast & refresh the car state
				temp_re.setAiring(true);
				temp_re.addInfo(clock + ": Order-taking timewidow starts!");
				for (i = 0;i<100;i++){
					 if (temp_re.isInrage(taxis[i].getmyPoint()) && taxis[i].getState().equals("waiting")){
						 temp_re.addStart(taxis[i].getId());
						 temp_re.addTaken(taxis[i].getId());
						 System.out.println(temp_re+" is taken by "+taxis[i]+" at the very beginning");
						 temp_re.addInfo(temp_re+" Taken by "+taxis[i]+" at the very beginning");
						 taxis[i].addCredit(1);
					 }
				}
				temp_re.addInfo("Start order list(Taxi id): "+temp_re.WTFstart());
			}
			else if (temp_re.getStart_time() < clock){
//				refresh & allocate & delete allocated re & output the request
				boolean needAlloc = temp_re.refresh();
				if (needAlloc && temp_re.isTakenEmpty()){
					temp_re.addInfo("Order-taking timewindow closes!");
					System.out.println("Nobody took NO." + temp_re.getId()+" order, your request will be discarded");
					temp_re.addInfo("Nobody took the order, this will be discarded");
//					输出被丢弃的信息，该请求响应结束
					it.remove();
					outRequest(temp_re.getInfo(),temp_re.getId());
					continue;
				}
				int allocID = 0;
				for (i = 0;i<100;i++){
					 if (!needAlloc 
							 && temp_re.isInrage(taxis[i].getmyPoint()) 
							 && !temp_re.isTaken(taxis[i].getId())
							 && taxis[i].getState().equals("waiting")){
						 temp_re.addTaken(taxis[i].getId());
						 taxis[i].addCredit(1);
						 System.out.println(temp_re+" is taken by "+taxis[i]);
						 temp_re.addInfo("Taken by "+taxis[i]);
					 }
					 if (needAlloc){
						 if (temp_re.isTaken(taxis[i].getId()) && !taxis[i].isRunning()){
							 if (allocID == 0) allocID = taxis[i].getId();
							 if (taxis[i].getCredit() > taxis[allocID-1].getCredit()
									 ||(taxis[i].getCredit() == taxis[allocID-1].getCredit() 
									 && taxis[i].howfar(temp_re.getDst()) <  taxis[allocID-1].howfar(temp_re.getDst())) ){
								 temp_re.addInfo("Last Alloc to taxi NO."+allocID);
								 temp_re.addInfo("Old distance: "+taxis[allocID-1].howfar(temp_re.getDst())+" New distance: "+taxis[i].howfar(temp_re.getDst()));
								 System.out.println("Last Alloc to taxi NO."+allocID);
								 System.out.println("Old distance: "+taxis[allocID-1].howfar(temp_re.getDst())+" New distance: "+taxis[i].howfar(temp_re.getDst()));
								 allocID = taxis[i].getId();
								 System.out.println(temp_re+" Newly alloc to "+taxis[i]);
								 temp_re.addInfo("Newly alloc to "+taxis[i]);
							 }
						 }
					 }
				}
				if (needAlloc){
					temp_re.addInfo(clock + ": Order-taking timewindow closes!");
					temp_re.addInfo("Taken order list(Taxi id): "+temp_re.WTFtaken());
					temp_re.addInfo("Allocted to "+taxis[allocID-1]);
					taxis[allocID-1].setTask(temp_re);
//					状态转换和设置路径？
					taxis[allocID-1].setLast_state("waiting");
					taxis[allocID-1].setState("order_taking");
					taxis[allocID-1].addCredit(3);
//					需要输出分配信息吗？
					System.out.println(temp_re+" allocted to "+taxis[allocID-1]);
					System.out.println(temp_re.WTFtaken());
					it.remove();
				}
			}
			else break;
		}
//		测试每次分配完请求request queue是否空了
//		System.out.println("Is emepty? "+request_queue.isEmpty()+" Remain "+request_queue.size());
		
		for (i = 0;i<100;i++){
			taxis[i].setCurrent_clock(clock+100);
			taxis[i].refreshState();
			if (taxis[i].isTaskfinshed()){
				System.out.println("Task NO."+taxis[i].getTask().getId()+" finished at "+(taxis[i].getCurrent_clock()-100));
				taxis[i].getTask().addInfo("With taxi NO."+taxis[i].getId()+"'s serve ended, the request finished at "+(taxis[i].getCurrent_clock()-100));
				outRequest(taxis[i].getTask().getInfo(),taxis[i].getTask().getId());
			}
			if (!taxis[i].isHalf()){
				taxis[i].addInfo(taxis[i].getCurrent_clock()+": "+taxis[i].toString());
			}
			gui.SetTaxiStatus(i, new Point(taxis[i].getmyPoint().getRow(), taxis[i].getmyPoint().getCol()), 
					stateToint.get(taxis[i].getState()));
		}
	}
	
	
	private void outRequest(LinkedList<String> info, int id){
		try{
			Iterator<String> iterator = info.iterator();
			String temp;
			File file = new File("Request_log/"+id+".txt");
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
	
	@Override
	public void run() {
		Request temp_re;
		try{
			while (!Thread.interrupted()){
				editYourCode.test();
				while (!blockq.isEmpty()){
					temp_re = blockq.take();
					temp_re.setStart_time(clock);
					request_queue.addLast(temp_re);
					temp_re.addInfo(temp_re.toString());
					gui.RequestTaxi(temp_re.getGuiSrc(), temp_re.getGuiDst());
				}
				scheduler();
				if (isFreetogo()){
					endflag = true;
				}
				
				clock += 100;
				TimeUnit.MILLISECONDS.sleep(100);
				
//				System.out.println("clock "+clock);
			}
		}catch (InterruptedException e){
			System.out.println("Taxi scheduler system interrupted.");
		}
	}



	public boolean isFreetogo(){
		int i;
		if (Readin.endflag && request_queue.isEmpty()){
			for (i = 0;i<100;i++){
				if (taxis[i].isRunning())
					return false;
			}
			outTaxislog();
			editYourCode.testOperation.outLog();
			return true;
		}
		return false;
	}

	private void outTaxislog(){
		try{
			LinkedList<String> info;
			for (int i=0;i<100;i++){
				info = taxis[i].getInfo();
				Iterator<String> iterator = info.iterator();
				String temp;
				File file = new File("Taxi_log/"+taxis[i].getId()+".txt");
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
			}
			
		}catch (Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}



	public long getClock() {
		return clock;
	}
	
	public Taxi getTaxi(int id){
		if (id<=100&&id>0)
			return taxis[id-1];
		else 
			return null;
	}

	public void setClock(long clock) {
		this.clock = clock;
	}
	
}
