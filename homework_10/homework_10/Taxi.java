package homework_10;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/*Overview
 * 这个类实现了模拟出租车运行的各种基本功能，包括模拟运动，寻路，状态转换，输出记录信息，提供接口供外界检查状态等。
 */

public class Taxi{
	private int id;
	private myPoint point;
	private myPoint next_point;
	private myPoint current_dst;
	private boolean half;
	private String last_state;
	private String state;//waiting  serving  stop  order_taking 
	private long credit;
	private Request task;
	private LinkedList<myPoint> route;
	private long watch;
	private Random random;
	private LinkedList<String> info;
	private long current_clock;
	protected int clock_count;
	
	public boolean repOK(){
		/*
		 * @REQUIRES:	None;
		 * @MODIFIES:	None;
		 * @EFFECTS:	Checks if "this" is legal.
		 */
		if (point == null||next_point == null||current_dst == null||last_state == null
				||info == null)
			return false;
		return true;
	}
	
	
	public Taxi(int id){
		/*@REQUIRES: (\all int id;0 <= id <= INTMAX)
		 *@MODIFIES:random
		 *			id
		 *			state
		 *			last_state
		 *			credit
		 *			point
		 *			next_point
		 *			current_dst
		 *			watch
		 *			route
		 *			info
		 *			current_clock
		 *			taxisys
		 *@EFFECTS: normal_behaviour:
		 *			变量和属性初始化
		 */
		this.random = new Random();
		this.id = id;
		this.setState("waiting");
		this.setLast_state("");
		this.credit = 0;
		this.point = new myPoint(random.nextInt(Readin.getMapSize()), random.nextInt(Readin.getMapSize()));
		watch = 20000;//20s
		route = new LinkedList<>();
		LinkedList<myPoint> nei = TaxiSystem.gps.getmyPointNeighbours(point);
		int i = random.nextInt(nei.size());
		setNextmyPoint(nei.get(i));
		info = new LinkedList<>();
		addInfo(this.toString());
		current_clock = 0;
		current_dst = new myPoint(0, 0);
	}
	

	public boolean isRunning(){
		/*
		 *@EFFECTS: normal_behaviour:
		 *			只要出租车当前不是等待服务状态就返回false
		 */
		return !state.equals("waiting");
	}
	
	public void addInfo(String e){
		/*@REQUIRES: (\all String e;e!=null)
		 *@MODIFIES:info
		 *@EFFECTS: normal_behaviour:
		 *			添加一条出租车的运行信息e到Info链表中
		 */
		info.add(e);
	}
	
	public LinkedList<String> getInfo(){
		/*
		 *@EFFECTS: normal_behaviour:
		 *			获得记录出租车运行信息的info链表
		 */
		return info;
	}
	
	@Override
	public String toString(){
		/*
		 *@EFFECTS: normal_behaviour:
		 *			返回出租车详细信息的字符串形式
		 */
		return "Taxi NO."+id+" Credit: "+credit+" State: "+state+" Current location: "+point;
	}
	
	public void refreshState(){
		/*@REQUIRES: None
		 *@MODIFIES: state
		 *			 route
		 *			 watch
		 *			 last_state
		 *			 current_dst
		 *			 info
		 *@EFFECTS: normal_behaviour:
		 			switch(出租车状态)：
		 				if (waiting):
		 					if (停表时间到)
		 						设置当前状态为stop
		 						停表更新为1000
		 					else
		 						继续倒计时
		 				if (serving):
		 				   if (到达任务目的地)
		 				   		停止运行并设置停表
		 				   else
		 				   		继续走
		 				if (stop):
		 					if (停表为0)
		 						根据上一个状态判断执行的操作
		 					else
		 						继续倒计时
		 				if (order_taking):
		 					根据上一个任务决定当前操作
		 */
		switch (state) {
		case "waiting":
			if (watch != 0){
				runTaxi();
			}
			else {
				setState("stop");
				setLast_state("waiting");
				watch = 1000;//1s
			}
			break;
		case "serving":
			if (getmyPoint().equals(task.getDst())){
				setState("stop");
				setLast_state("serving");
				watch = 1000;
				this.addInfo((current_clock-100)+" Get to the destination and task finished:"+task.getDst());
			}
			else{
				runTaxi();
			}
			break;
		case "stop":
			if (watch == 0){
				if (getLast_state().equals("serving")||getLast_state().equals("waiting")){
					setLast_state("stop");
					setState("waiting");
					addInfo((getCurrent_clock())-100+": "+this);
					watch = 20000;
					half = true;
					runTaxi();
				}
				else if(getLast_state().equals("order_taking")){
					setState("serving");
					setLast_state("stop");
					setRoute(TaxiSystem.gps.search(point, task.getDst()));
					current_dst = task.getDst();
					task.addInfo("The taxi NO."+id+" now is heading to "+ task.getDst());
					task.addInfo("Route: "+ routeTostring());
					this.addInfo((current_clock-100)+" On the way to the destination: "+task.getDst());
					this.addInfo("Route: "+ routeTostring());
					half = true;
				}
				else{
					System.out.println("No such state");
					System.exit(0);
				}
			}
			break;
		case "order_taking":
			setRoute(TaxiSystem.gps.search(point, task.getSrc()));
			current_dst = task.getSrc();
			if (last_state.equals("waiting")){
				task.addInfo("The taxi NO."+id+"in "+point+" is going to pick up the custom in "+task.getDst());
				task.addInfo("Pick up route: "+ routeTostring());
				this.addInfo((current_clock-100)+" On the way to pick up the custom in "+task.getSrc()+ "from"+point);
				this.addInfo("Current task: "+task);
				this.addInfo("Pick up route: "+ routeTostring());
				setLast_state("order_taking");
			}
			if (getmyPoint().equals(task.getSrc())){
				setState("stop");
				setLast_state("order_taking");
				watch = 1000;
			}
			else{
				runTaxi();
			}
			break;			
		default:
			break;
		}
		if (state.equals("waiting")||state.equals("stop"))
			watch -= 100;
	}
	
	private String routeTostring(){
		/*
		 *@EFFECTS: normal_behaviour:
		 *			返回出租车当前存储的运行路径的字符串形式
		 */
		String out = "";
		int i = 2;
		Iterator<myPoint> iterator = route.iterator();
		out += point;
		out += "->";
		out += next_point;
		out += "->";
		while (iterator.hasNext()){
			out += iterator.next();
			out += "->";
			i++;
			if (i%5 == 0){
				out += "\n";
			} 
		}
		if (i%5 == 0)
			out = out.substring(0, out.length()-3);
		else
			out = out.substring(0, out.length()-2);
		return out;
	}
	
	public boolean isTaskfinshed(){
		/*
		 *@EFFECTS: normal_behaviour:
		 *			if (出租车停止并且上一个状态是服务，当前倒计时是900)
		 *				then return true说明当前出租车刚完成任务
		 */
		return state.equals("stop") && last_state.equals("serving") && watch == 900;
	}
	
	public void setRoute(LinkedList<myPoint> r){
		/*@REQUIRES: (\all LinkedList<myPoint> r;r != null)
		 *@MODIFIES: route
		 *			 next_point
		 *@EFFECTS: normal_behaviour:
		 *			将出租车即将运行的路径设置为r并且将下一个目标点设置为新路径上的第二个点。（第一个点就是目前地点）
		 */
		this.route = r;
		route.removeFirst();
		next_point = route.removeFirst();
	}
	
	public void runTaxi(){
		/*@REQUIRES:None
		 *@MODIFIES:next_point
		 *			half
		 *@EFFECTS: normal_behaviour:
		 *			if (车在半路上)
		 *				then 车到了节点
		 *				then 设置下一个目标节点
		 *				if (车处于等待状态)
		 *					获得随机最少流量道路点作为下一个目标
		 *				else
		 *					如果路径没走完就从中取下一个节点目标
		 *			else
		 *				then 车到了半路上
		 */
		if (half){
			half = false;
			myPoint lastpoint = point;
			setmyPoint(next_point);
			if (state.equals("waiting")){
//				获取一个随机点
//				应该选择随机最小流量
				LinkedList<myPoint> nei = TaxiSystem.gps.getmyPointNeighbours(point);
				int i,min = random.nextInt(nei.size());
				LinkedList<Integer> minNO = new LinkedList<>();
				minNO.add(min);
				for (i = 0;i<nei.size();i++){
					if (TaxiSystem.flow.get(""+nei.get(i).getRow()+","+nei.get(i).getCol()+","+point.getRow()+","+point.getCol()) 
							== TaxiSystem.flow.get(""+nei.get(min).getRow()+","+nei.get(min).getCol()+","+point.getRow()+","+point.getCol()) ){
						minNO.add(i);
					}
					if (TaxiSystem.flow.get(""+nei.get(i).getRow()+","+nei.get(i).getCol()+","+point.getRow()+","+point.getCol()) 
							< TaxiSystem.flow.get(""+nei.get(min).getRow()+","+nei.get(min).getCol()+","+point.getRow()+","+point.getCol()) ){
						min = i;
						minNO.clear();
						minNO.add(min);
					}
				}
				min = minNO.get(random.nextInt(minNO.size()));
				setNextmyPoint(nei.get(min));
			}
			else {
				if (!route.isEmpty()){
					setNextmyPoint(route.removeFirst());
				}
			}
			int light = Light.getTimeleft();
			switch (light) {
			case 1:
			//north sorth red
				if (lastpoint.getCol() == next_point.getCol()
				  ||(lastpoint.getRow()+1 == next_point.getRow()&&lastpoint.getCol()-1 == next_point.getCol())
				  ||(lastpoint.getRow()-1 == next_point.getRow()&&lastpoint.getCol()+1 == next_point.getCol())){
					this.clock_count = -1*Light.getTimeleft();
				}
				break;
			case 2:
			//west east red
				if (lastpoint.getRow() == next_point.getRow()
				  ||(lastpoint.getRow()+1 == next_point.getRow()&&lastpoint.getCol()+1 == next_point.getCol())
				  ||(lastpoint.getRow()-1 == next_point.getRow()&&lastpoint.getCol()-1 == next_point.getCol())){
					this.clock_count = -1*Light.getTimeleft();
				}
				break;
			default:
				break;
			}
//			如果不可以走，设置计时器
		}
		else{
			half = true;
		}
		
	}
	
	public int howfar(myPoint goal){
		/*@REQUIRES: (\all myPoint goal;goal is in the map)
		 *@MODIFIES: None
		 *@EFFECTS: normal_behaviour:
		 *			返回输入的点距离车当前位置的距离
		 */
		return TaxiSystem.gps.search(point, goal).size();
	}
	
	public void setmyPoint(myPoint next){
		/*@REQUIRES:(\all myPoint next;next is in the map)
		 *@MODIFIES:point
		 *@EFFECTS: normal_behaviour:
		 *			更新当前所在位置为原先的目标节点，表示到达了新的节点位置
		 */
		point = next;
	}

	public boolean setNextmyPoint(myPoint temp){
		/*@REQUIRES:(\all myPoint temp;temp is in the map)
		 *@MODIFIES: route
		 *			 next_point
		 *@EFFECTS: normal_behaviour:
		 *			if (要设置的下一个节点和当前结点之间没路了)
		 *				then 重新找路并设置下一个节点
		 *				then return false
		 *			else
		 *				then 设置下一个节点为输入的temp
		 *				then return true
		 */
		if (!TaxiSystem.gps.isNeighbours(temp, point)){
			System.out.println(point+" "+temp+TaxiSystem.gps.getmyPointNeighbours(point).contains(temp));
			setRoute(TaxiSystem.gps.search(point, current_dst));
			System.out.println("Oops, road was destroyed and path reworking success!");
			this.addInfo("Oops, road was destroyed and path reworking success!");
			this.addInfo("The remaining new routes: "+ routeTostring());
			task.addInfo("Oops, road was destroyed and path reworking success!");
			task.addInfo("The remaining new routes: "+ routeTostring());
			return false;
		}
		else{
			next_point = temp;
			return true;
		}
	}
	
	public String getState() {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			获得出租车的当前状态
		 */
		return state;
	}

	public void setState(String state) {
		/*@REQUIRES: (\all String state;state == waiting||state == stop||state == serving||state == order_taking)
		 *@MODIFIES: this.state
		 *@EFFECTS: normal_behaviour:
		 *			设置出租车的当前状态
		 */
		this.state = state;
	}

	public int getId() {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			获得出租车的编号
		 */
		return id;
	}


	public long getCredit() {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			获得出租车当前信用
		 */
		return credit;
	}

	public void addCredit(int i) {
		/*@REQUIRES: (\all int i; 0<= i <= INTMAX)
		 *@MODIFIES: credit
		 *@EFFECTS: normal_behaviour:
		 *			给出租车增加信用i
		 */
		this.credit += i;
	}

	public Request getTask() {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			获得出租车当前正在执行的任务
		 */
		return task;
	}

	public void setTask(Request task) {
		/*@REQUIRES: (\all Request task;task != null)
		 *@MODIFIES: this.task;
		 *@EFFECTS: normal_behaviour:
		 *			更新出租车的任务
		 */
		this.task = task;
	}

	
	public myPoint getmyPoint(){
		/*
		 *@EFFECTS: normal_behaviour:
		 *			返回出租车所在的当前结点
		 */
		return point;
	}
	
	public myPoint getmynextPoint(){
		/*
		 *@EFFECTS: normal_behaviour:
		 *			返回出租车下一个要到达的地图节点
		 */
		return next_point;
	}
	
	public Point getGuiPoint() {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			返回符合GUI格式的出租车当前坐标
		 */
		return new Point(point.getRow(), point.getCol());
	}

	public String getLast_state() {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			返回出租车的上一个状态
		 */
		return last_state;
	}

	public void setLast_state(String last_state) {
		/*@REQUIRES: (\all String last_state;last_state == waiting||last_state == stop||last_state == serving||last_state == order_taking)
		 *@MODIFIES: this.last_state
		 *@EFFECTS: normal_behaviour:
		 *			设置出租车的上一个状态
		 */
		this.last_state = last_state;
	}
	
	public boolean isHalf() {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			返回出租车当前是否在某条路中央
		 */
		return half;
	}

	public long getCurrent_clock() {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			获得出租车当前内部时间
		 */
		return current_clock;
	}

	public void setCurrent_clock(long current_clock) {
		/*@REQUIRES:(\all long current_clock;0<=current_clock<=LONGMAX)
		 *@MODIFIES:this.current_clock
		 *@EFFECTS: normal_behaviour: 
		 *			设置出租车内部当前时钟时间为输入的current_clock
		 *
		 */
		this.current_clock = current_clock;
	}



}
