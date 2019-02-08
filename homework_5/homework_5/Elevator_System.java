package homework_5;

import java.text.DecimalFormat;

public class Elevator_System implements Runnable{
	private Elevator [] elevators;
	private Floor [] floors;
	private Request_queue [] request_queues;
	private Request [] main_re;
	private Task [] current_task;
	private String [] state;
	protected static long time;
	private Multi_scheduler multi_scheduler;
	private int ele_num;
	
	
	public Elevator_System(int n, Multi_scheduler multi_scheduler){
		time = 0;
		int i;
		ele_num = n;
		this.multi_scheduler = multi_scheduler;
		elevators = new Elevator[n];
		floors = new Floor[n];
		request_queues = new Request_queue[n];
		main_re = new Request[n];
		state = new String[n];
		current_task = new Task[n];
		for (i = 1;i < n+1;i++){
			elevators[i-1] = new Elevator(i);
			floors[i-1] = new Floor(i);
			request_queues[i-1] = new Request_queue(i);
			main_re[i-1] = null;
			state[i-1] = "";
			current_task[i-1] = null;
//			the id is 123
		}
	}
	
	public boolean isEnd(){
		int i;
		for (i = 0;i<ele_num;i++)
			if (!request_queues[i].isEmpty() || main_re[i] != null || current_task[i] != null)
				return false;
		return true;
	}
	
	public boolean addRequest(Request temp_re){
		int id,i,min = -1,min_nomain = -1;
		if (temp_re.getCat().equals("ER")){
			id = temp_re.getEle_id();
			if (!checkERButton(temp_re, elevators[id-1],request_queues[id-1])) return true;
			if ( (state[id-1].equals("UP") && temp_re.getFlr() > main_re[id-1].getFlr()) 
					  || (state[id-1].equals("DOWN") && temp_re.getFlr() < main_re[id-1].getFlr()) ){
						temp_re.setPiggyback();
					}
		}
		
		else {
			for (i = 0;i < ele_num;i++){
				if (!checkFRButton(temp_re, floors[i], request_queues[i])) return true;
			}
			for (i = 0;i < ele_num;i++){
				if (main_re[i] == null) {
					if (min_nomain == -1) min_nomain = i;
					else if (elevators[i].getDistance() < elevators[min_nomain].getDistance()) min_nomain = i;
					continue;
				}
				if (state[i].equals(temp_re.getDR_dir())){
					if (state[i].equals("UP")){
						if (temp_re.getFlr() <= main_re[i].getFlr() && temp_re.getFlr() > elevators[i].getCurrentfr()){
							if (min == -1) min = i;
							else if (elevators[min].getDistance() > elevators[i].getDistance()) min = i;
						}
					}
					else{
						if (temp_re.getFlr() >= main_re[i].getFlr() && temp_re.getFlr() < elevators[i].getCurrentfr()){
							if (min == -1) min = i;
							else if (elevators[min].getDistance() > elevators[i].getDistance()) min = i;
						}
					}
				}
			}
			
			id = (min != -1)?min+1:(min_nomain != -1)?min_nomain+1 : -1;
			if (id == -1) return false;
			
		}
		lock_button(temp_re, elevators[id-1], floors[id-1]);
		request_queues[id-1].add(temp_re);
		return true;
	}
	
	public void lock_button(Request run_request, Elevator elevator, Floor floor){
		if (run_request.getCat().equals("ER")) {
			elevator.pressButton(run_request.getFlr(), run_request);
		}
			
		else if (run_request.getCat().equals("FR")){
			if (run_request.getDR_dir().equals("UP")) 
				floor.setUplock(run_request.getFlr(), run_request);
			else if (run_request.getDR_dir().equals("DOWN")) 
				floor.setDownlock(run_request.getFlr(), run_request);
			else {
//				error for debug
				System.out.println("FATAL ERROR1/"+run_request.getCat()+"/");
				System.exit(0);
			}
		}
		else{
//			error for debug
			System.out.println("FATAL ERROR2");
			System.exit(0);
		}
		
	}
	
	
	public boolean checkERButton(Request run_request, Elevator elevator,Request_queue que){
		
		if (elevator.getButton(run_request.getFlr()) == 1){
			try {
				Main.output.put(System.currentTimeMillis() + ":SAME [" + run_request + "]");
			}catch (InterruptedException e){}
			que.rmSpecified(run_request);
			return false;
		}		
		return true;
	}
	
	public boolean checkFRButton(Request run_request, Floor floor, Request_queue que){
		
		if (floor.getLock(run_request.getFlr(), run_request.getDR_dir())){
			try {
				Main.output.put(System.currentTimeMillis() + ":SAME [" + run_request + "]");
			}catch (InterruptedException e){}
			que.rmSpecified(run_request);
			return false;
		}
		
		return true;
	}
	
	public Request getMain(Request_queue queue){
		int i;
		for (i = queue.getSize()-1;i >= 0;i--){
			if (queue.getKth(i).getPiggyback()) {
				return queue.rmKth(i);
			}
		}
		
		return queue.getLast();
	}
	
	public void currentOutput(Request rq, int id, int flr, String state, int dis, long t){
		long temp = t;
		if (state.equals("STILL") && t < rq.getTime()+6000)
			temp = rq.getTime()+6000;
		else if ((state.equals("UP")||state.equals("DOWN"))&&t<rq.getTime()+3000)
			temp = rq.getTime()+3000;
		DecimalFormat df = new DecimalFormat("#.0");
		try {
			Main.output.put(System.currentTimeMillis() + ": [" + rq + "] / "+"("+ "#"+ id +", "+ flr+", " + state +", "+ dis +", "+ df.format((double)temp/1000) +")");
		}catch (InterruptedException e){}
	}
	
	@Override
	public void run() {
		try {
			while (!Thread.interrupted()){
				synchronized (this) {
					while (time == Multi_scheduler.now_time){
						wait();
					}
				}
				synchronized (multi_scheduler) {
					time = Multi_scheduler.now_time;
					int i;
					for (i = 0;i < ele_num;i++){
						if (main_re[i] == null && !request_queues[i].isEmpty()){
							
							main_re[i] = getMain(request_queues[i]);
							
							state[i] = (elevators[i].getCurrentfr() < main_re[i].getFlr())? "UP":(elevators[i].getCurrentfr() == main_re[i].getFlr())?"STILL":"DOWN";
							if (state[i].equals("UP")||state[i].equals("DOWN")){
								current_task[i] = new Task(time+3000, state[i], 1);
							}
							if (state[i].equals("STILL")) {
								current_task[i] = new Task(time+6000, state[i], 0);
							}
							
						}
						if (current_task[i] == null) continue;
						if (time == current_task[i].getFinishtime()){
//							System.out.println("hit");
							if (current_task[i].getFlr_change() != 0){
								elevators[i].setCurrentfr(current_task[i].getThings());
								elevators[i].addDistace();
								current_task[i] = null;
							}
							if (state[i].equals("UP")||state[i].equals("DOWN")){
								current_task[i] = new Task(time+3000, state[i], 1);
							}
							if (state[i].equals("STILL")){
		
								currentOutput(main_re[i], i+1, elevators[i].getCurrentfr(), "STILL", elevators[i].getDistance(), time);
							}
							
							int door = 0, upflag = 0, downflag = 0, eleflag = 0;
							Floor floor = floors[i];
							Elevator elevator = elevators[i];
							Request main_request = main_re[i];
							String run_dir = state[i];
							
							if (floor.getUplock(elevator.getCurrentfr()) == 1 && ( run_dir.equals("UP")
									|| (main_request != null &&!run_dir.equals("STILL")&& main_request.getCat().equals("FR") && elevator.getCurrentfr() == main_request.getFlr() && main_request.getDR_dir().equals("UP"))) ){
								currentOutput(floor.getUprequest(elevator.getCurrentfr()), i+1, elevators[i].getCurrentfr(), "UP", elevators[i].getDistance(), time);
								request_queues[i].rmSpecified(floor.getUprequest(elevator.getCurrentfr()));
								door = 1;
								upflag = 1;
							}
							if (floor.getDownlock(elevator.getCurrentfr()) == 1 && ( run_dir.equals("DOWN")
									|| (main_request != null &&!run_dir.equals("STILL")&& main_request.getCat().equals("FR") && elevator.getCurrentfr() == main_request.getFlr() && main_request.getDR_dir().equals("DOWN"))) ){
								currentOutput(floor.getDownrequest(elevator.getCurrentfr()), i+1, elevators[i].getCurrentfr(), "DOWN", elevators[i].getDistance(), time);
								request_queues[i].rmSpecified(floor.getDownrequest(elevator.getCurrentfr()));
								door = 1;
								downflag = 1;
							}
							if ((elevator.getButton(elevator.getCurrentfr()) == 1)&&!run_dir.equals("STILL")){
								currentOutput(elevator.getRequest(elevator.getCurrentfr()), i+1, elevators[i].getCurrentfr(), run_dir, elevators[i].getDistance(), time);
								request_queues[i].rmSpecified(elevator.getRequest(elevator.getCurrentfr()));	
								door = 1;
								eleflag = 1;
							}
						
							
							if (upflag == 1)
								floor.unlockUp(elevator.getCurrentfr());
							if (downflag == 1)
								floor.unlockDown(elevator.getCurrentfr());
							if (eleflag == 1)
								elevator.unlock(elevator.getCurrentfr());
							if (door == 1){
								current_task[i] = new Task(time+6000, state[i], 0);
								continue;
							}
							if (main_request != null && elevator.getCurrentfr() == main_request.getFlr()) {
								if (request_queues[i].isEmpty()) {
									main_re[i] = null;
									state[i] = "";
									current_task[i] = null;
									continue;
								}
								main_re[i] = getMain(request_queues[i]);
								
								state[i] = (elevators[i].getCurrentfr() < main_re[i].getFlr())? "UP":(elevators[i].getCurrentfr() == main_re[i].getFlr())?"STILL":"DOWN";
								if (state[i].equals("UP")||state[i].equals("DOWN")){
									current_task[i] = new Task(time+3000, state[i], 1);
								}
								if (state[i].equals("STILL")) {
									current_task[i] = new Task(time+6000, state[i], 0);
								}
							}
						}
					}
					
					multi_scheduler.notifyAll();
				}
			}
		}catch (InterruptedException e){
			System.out.println("ELevator system Interrupted");
		}
		
		
		
	}
	
}
