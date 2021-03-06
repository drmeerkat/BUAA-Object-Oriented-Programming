package homework_9;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;





public class TaxiSystem implements Runnable {
	private Taxi [] taxis;
	private LinkedList<Request> request_queue;
	private long clock;
	protected BlockedRequestQueue blockq;
	private TaxiGUI gui;
	private HashMap<String, Integer> stateToint;
	protected static boolean endflag = false;
	private EditYourCode editYourCode;
	protected static Findpath gps;
	protected static HashMap<String,Integer> flow;
	
	public TaxiSystem(BlockedRequestQueue bq, int map[][], TaxiGUI gui) {
		/*@REQUIRES:(\all int[][]; map != null)
		 *@MODIFIES:
		 *			taxis
		 *			request_queue
		 *			block_kq
		 *			gui
		 *			statepoint
		 *			endflag
		 *			gps
		 *			flow
		 *@EFFECTS: 初始化各种类内属性和变量。
		 *			包括初始化100个出租车
		 *			初始化出租车公用的寻路GPS
		 *			设置gui显示出租车
		 */
		gps = new Findpath(map);
		flow = new HashMap<String,Integer>();
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
			taxis[i] = new Taxi(i+1); //id 1-100
			gui.SetTaxiStatus(i, taxis[i].getGuiPoint(), stateToint.get(taxis[i].getState()));
		}
		editYourCode = new EditYourCode(this);
	}
	
	public int mysetRoadStatus(Point p1, Point p2, int status) {// status 0关闭 1打开
		/*@REQUIRES:(\all Point p1,p2;
		 * 			p1 is in the map && p2 is in the map;
		 * 			\all int status; status == 0|| status == 1);
		 *@MODIFIES:map
		 *@EFFECTS: 计算并修改指定的地图路径。0代表断开p1,p2之间的路径，1代表链接p1p2
		 *			正常情况下返回0
		 *			异常情况下返回负数错误代码，交给上层调用者处理。
		 *			-1表示两点之间无连接
		 *			-2表示该道路已经关闭
		 *          -3表示该道路已经打开
		 */
		int di = p1.x - p2.x;
		int dj = p1.y - p2.y;
		Point p = null;
		if (di == 0) {// 在同一水平线上
			if (dj == 1) {// p2-p1
				p = p2;
			} else if (dj == -1) {// p1-p2
				p = p1;
			} else {
				return -1;//两个点不临接
			}
			if (status == 0) {// 关闭
				hasCar(p1, p2);
				if (Readin.map[p.x][p.y] == 3) {
					Readin.map[p.x][p.y] = 2;
				} else if (Readin.map[p.x][p.y] == 1) {
					Readin.map[p.x][p.y] = 0;
				}
				else {
					return -2;//两个点之间已经关闭
				}
			} else if (status == 1) {// 打开
				if (Readin.map[p.x][p.y] == 2) {
					Readin.map[p.x][p.y] = 3;
				} else if (Readin.map[p.x][p.y] == 0) {
					Readin.map[p.x][p.y] = 1;
				}
				else{
					return -3;//两个点之间已经打开
				}
			}
		} else if (dj == 0) {// 在同一竖直线上
			if (di == 1) {// p2-p1
				p = p2;
			} else if (di == -1) {// p1-p2
				p = p1;
			} else {
				return -1;
			}
			if (status == 0) {// 关闭
				hasCar(p1, p2);
				if (Readin.map[p.x][p.y] == 3) {
					Readin.map[p.x][p.y] = 1;
				} else if (Readin.map[p.x][p.y] == 2) {
					Readin.map[p.x][p.y] = 0;
				}
				else {
					return -2;
				}
			} else if (status == 1) {// 打开
				if (Readin.map[p.x][p.y] == 1) {
					Readin.map[p.x][p.y] = 3;
				} else if (Readin.map[p.x][p.y] == 0) {
					Readin.map[p.x][p.y] = 2;
				}
				else {
					return -3;
				}
			}
		}
		else {
			return -1;
		}
		return 0;
	}
	
	private boolean hasCar(Point p1, Point p2){
		/*@REQUIRES:(\all Point p1,p2;
		 * 			p1 is in the map && p2 is in the map;
		 * 			\all int status; status == 0|| status == 1);
		 *@MODIFIES:None
		 *@EFFECTS: 返回当前是否有车在以p1 p2为端点的这条路上。有的话返回true，否则返回false
		 */
		int i;
		myPoint pa = new myPoint(p1.x, p1.y);
		myPoint pb = new myPoint(p2.x, p2.y);
		for (i = 0;i<100;i++){
			if ( (taxis[i].getmyPoint().equals(pa)&&taxis[i].getmynextPoint().equals(pb)&&taxis[i].isHalf())
		       ||(taxis[i].getmyPoint().equals(pb)&&taxis[i].getmynextPoint().equals(pa)&&taxis[i].isHalf()) )
				return true;
		}
		return false;
	}
	
	private void refreshGps(){
		/*@REQUIRES:None
		 *@MODIFIES:Readin.block_setroad
		 *			gps
		 *@EFFECTS: 从阻塞队列中取出修路请求并调用mysetRoadStatus对路线进行处理，处理后根据返回值输出响应信息，并更新gps
		 */
		Pattern find_roadup = Pattern.compile("\\[\\((\\d\\d?),(\\d\\d?)\\),\\((\\d\\d?),(\\d\\d?)\\),([01])\\]");
		Matcher match_roadup;
		boolean mapchange = false;
		while (!Readin.block_setroad.isEmpty()){
			String roadup = Readin.block_setroad.poll();
			match_roadup = find_roadup.matcher(roadup);
			match_roadup.matches();
			Point aPoint = new Point(Integer.parseInt(match_roadup.group(1)), Integer.parseInt(match_roadup.group(2)));
			Point bPoint = new Point(Integer.parseInt(match_roadup.group(3)), Integer.parseInt(match_roadup.group(4)));
			int state = mysetRoadStatus(aPoint, bPoint, Integer.parseInt(match_roadup.group(5)));
			if (state == 0){
				gui.SetRoadStatus(aPoint, bPoint, Integer.parseInt(match_roadup.group(5)));
				System.out.println("Road change success!");
				mapchange = true;
			}
			else if (state == -1){
				System.out.println(roadup+": The two points are not adjacent");
			}
			else if (state == -2){
				System.out.println(roadup+": Already closed!");
			}
			else if (state == -3){
				System.out.println(roadup+": Already opend!");
			}
			else{
				System.out.println(roadup+": Unknown road error");
			}
		}
		if (mapchange){
			gps = new Findpath(Readin.map);
		}
	}
	
	public void scheduler(){
		/*@REQUIRES:(\forall variables; variable is initialized)
		 *@MODIFIES:gps
		 *			flow
		 *			taxis[]
		 *			request_queue
		 *			block_kq
		 *@EFFECTS: 更新道路流量；
		 *			更新地图；
		 *			分配请求，开启请求时间窗，统计抢单车辆，分配请求给车辆；
		 *			调度车辆；
		 *			记录请求被分配和车辆行驶信息；
		 *			
		 */
		int i;
		int j;
		refreshGps();
		flow.clear();
		for (i = 0;i<80;i++){
			for (j = 0;j<80;j++){
				flow.put(""+i+","+j+","+i+","+(j+1), guigv.GetFlow(i, j, i, j+1));
				flow.put(""+i+","+(j+1)+","+i+","+j, guigv.GetFlow(i, j, i, j+1));
				flow.put(""+i+","+j+","+(i+1)+","+j, guigv.GetFlow(i, j, i, j+1));
				flow.put(""+(i+1)+","+j+","+i+","+j, guigv.GetFlow(i, j, i, j+1));
			}
		}
		
		
		
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
						 System.out.println(temp_re+" is taken by "+taxis[i]+" at the very beginning"+"(credit before)");
						 temp_re.addInfo(temp_re+" Taken by "+taxis[i]+" at the very beginning"+"(credit before)");
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
						 System.out.println(temp_re+" is taken by "+taxis[i]+"(credit before)");
						 temp_re.addInfo("Taken by "+taxis[i]+"(credit before)");
						 taxis[i].addCredit(1);
					 }
					 if (needAlloc){
						 if (temp_re.isTaken(taxis[i].getId()) && !taxis[i].isRunning()){
							 if (allocID == 0) allocID = taxis[i].getId();
							 if (taxis[i].getCredit() > taxis[allocID-1].getCredit()
									 ||(taxis[i].getCredit() == taxis[allocID-1].getCredit() 
									 && taxis[i].howfar(temp_re.getDst()) <  taxis[allocID-1].howfar(temp_re.getDst())) ){
//								 temp_re.addInfo("Last Alloc to taxi NO."+allocID);
//								 temp_re.addInfo("Old distance: "+taxis[allocID-1].howfar(temp_re.getDst())+" New distance: "+taxis[i].howfar(temp_re.getDst()));
//								 System.out.println("Last Alloc to taxi NO."+allocID);
//								 System.out.println("Old distance: "+taxis[allocID-1].howfar(temp_re.getDst())+" New distance: "+taxis[i].howfar(temp_re.getDst()));
								 allocID = taxis[i].getId();
//								 System.out.println(temp_re+" Newly alloc to "+taxis[i]);
//								 temp_re.addInfo("Newly alloc to "+taxis[i]);
							 }
						 }
					 }
				}
				if (needAlloc&&allocID == 0){
					temp_re.addInfo("No taxis avaliable!");
					System.out.println("Nobody will serve NO." + temp_re.getId()+" order, your request will be discarded");
					temp_re.addInfo("No taxis avaliable, this will be discarded");
					it.remove();
					outRequest(temp_re.getInfo(),temp_re.getId());
					continue;
				}
				if (needAlloc){
					temp_re.addInfo(clock + ": Order-taking timewindow closes!");
					temp_re.addInfo("Taken order list(Taxi id): "+temp_re.WTFtaken());
					temp_re.addInfo("Allocted to "+taxis[allocID-1]);
					taxis[allocID-1].setTask(temp_re);

					taxis[allocID-1].setLast_state("waiting");
					taxis[allocID-1].setState("order_taking");
					taxis[allocID-1].addCredit(3);

					System.out.println(temp_re+" allocted to "+taxis[allocID-1]);
					System.out.println("Taken list: "+temp_re.WTFtaken());
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
	
	
	private void outRequest(LinkedList<String> info, int i){
		/*@REQUIRES:(\all Linkedlist<String>; info != null;
		 * 			 \all int; i != null)
		 *@MODIFIES:File("Request_log/"+i+".txt");
		 *@EFFECTS: 将记录的请求处理记录输出到按照请求编号命名的log文件中
		 */
		try{
			Iterator<String> iterator = info.iterator();
			String temp;
			File file = new File("Request_log/"+i+".txt");
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
		/*@REQUIRES:None
		 *@MODIFIES:gps
		 *			flow
		 *			taxis[]
		 *			request_queue
		 *			block_kq
		 *			clock
		 *			
		 *@EFFECTS: 将分配请求，刷新地图和流量，刷新出租车状态的方法组合成一个线程并进行包装；
		 *			判断当前控制台输入是否已经关闭，当输入关闭并且请求也全部处理完，车都处于waiting状态时，结束运行。
		 *			(if (isfreeTogo),then this.endflag = true)
		 */
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
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Unknown error");
		}
	}



	public boolean isFreetogo(){
		/*@REQUIRES:None
		 *@MODIFIES:endflag
		 *@EFFECTS: if (Readin.endflag&&request_queue.isEmpty()),
		 *				then if \forall Taxi; taxis[i].isRunning == false,
		 *					then this.endflag = true
		 *					输出出租车log文件
		 */
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
		/*@REQUIRES:None
		 *@MODIFIES:new File("Taxi_log/"+taxis[i].getId()+".txt")
		 *@EFFECTS: 输出出租车记录，按照出租车编号顺序命名
		 */
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
		/*@REQUIRES:None
		 *@MODIFIES:None
		 *@EFFECTS: 返回当前系统时间
		 */
		return clock;
	}
	
	public Taxi getTaxi(int id){
		/*@REQUIRES:None
		 *@MODIFIES:None
		 *@EFFECTS: 
		 *			if (id<=100&&id>0),
		 *				then 返回指定id的出租车
		 *			else
		 *				return null
		 *			
		 */
		if (id<=100&&id>0)
			return taxis[id-1];
		else 
			return null;
	}
	
}
