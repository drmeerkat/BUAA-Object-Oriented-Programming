package homework_7;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class Taxi {
	private int id;
	private myPoint point;
	private myPoint next_point;
	private boolean half;
	private String last_state;
	private String state;//waiting  serving  stop  order_taking 
	private long credit;
	private Request task;
	private LinkedList<myPoint> route;
	private long watch;
	private Findpath gps;
	private Random random;
	private LinkedList<String> info;
	private long current_clock;
	
	public Taxi(int id, int map[][]){
		this.random = new Random();
		this.setId(id);
		this.setState("waiting");
		this.setLast_state("");
		this.credit = 0;
		this.point = new myPoint(random.nextInt(Readin.getMapSize()), random.nextInt(Readin.getMapSize()));
		watch = 20000;//20s
		route = new LinkedList<>();
		this.gps = new Findpath(map);
		LinkedList<myPoint> nei = gps.getmyPointNeighbours(point);
		int i = random.nextInt(nei.size());
		setNextmyPoint(nei.get(i));
		info = new LinkedList<>();
		addInfo(this.toString());
		current_clock = 0;
	}
	
	public boolean isRunning(){
		return !state.equals("waiting");
	}
	
	public void addInfo(String e){
		info.add(e);
	}
	
	public LinkedList<String> getInfo(){
		return info;
	}
	
	@Override
	public String toString(){
		return "Taxi NO."+id+" Credit: "+credit+" State: "+state+" Current location: "+point;
	}
	
	public void refreshState(){
		
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
//					计算出route同时注意ontheway标记以及当前路径走完了没
					setRoute(gps.search(point, task.getDst()));
					task.addInfo("The taxi NO."+id+" now is heading to "+ task.getDst());
					task.addInfo("Route: "+ routeTostring());
					this.addInfo(current_clock+" On the way to the destination: "+task.getDst());
					this.addInfo("Route: "+ routeTostring());
					half = true;
					runTaxi();
				}
				else{
					System.out.println("No such state");
					System.exit(0);
				}
			}
			break;
		case "order_taking":
			setRoute(gps.search(point, task.getSrc()));
			if (last_state.equals("waiting")){
				task.addInfo("The taxi NO."+id+"in "+point+" is going to pick up the custom in "+task.getDst());
				task.addInfo("Pick up route: "+ routeTostring());
				this.addInfo(current_clock+" On the way to pick up the custom in "+task.getDst()+ "from"+point);
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
//				注意ontheway标记以及当前路径走完了没
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
		return state.equals("stop") && last_state.equals("serving") && watch == 900;
	}
	
	public void setRoute(LinkedList<myPoint> r){
//		找路
		this.route = r;
		route.removeFirst();
		setNextmyPoint(route.removeFirst());
	}
	
	public void runTaxi(){
		if (half){
			half = false;
			setmyPoint(next_point);
			if (state.equals("waiting")){
//				获取一个随机点
				LinkedList<myPoint> nei = gps.getmyPointNeighbours(point);
				int i = random.nextInt(nei.size());
				setNextmyPoint(nei.get(i));
			}
			else {
				if (!route.isEmpty()){
					setNextmyPoint(route.removeFirst());
				}
			}
			
		}
		else{
			half = true;
		}
		
	}
	
	public int howfar(myPoint goal){
		return gps.search(point, goal).size();
	}
	
	public void setmyPoint(myPoint next){
		point = next;
	}

	public void setNextmyPoint(myPoint temp){
		next_point = temp;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getCredit() {
		return credit;
	}

	public void addCredit(int i) {
		this.credit += i;
	}

	public Request getTask() {
		return task;
	}

	public void setTask(Request task) {
		this.task = task;
	}

	
	public myPoint getmyPoint(){
		return point;
	}
	
	public Point getGuiPoint() {
		return new Point(point.getRow(), point.getCol());
	}

	public String getLast_state() {
		return last_state;
	}

	public void setLast_state(String last_state) {
		this.last_state = last_state;
	}
	
	public boolean isHalf() {
		return half;
	}

	public long getCurrent_clock() {
		return current_clock;
	}

	public void setCurrent_clock(long current_clock) {
		this.current_clock = current_clock;
	}
}
