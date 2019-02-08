package homework_3;



public class New_scheduler extends Scheduler{
	private double clock;
	private Request main_request;
	private String run_dir;
	
	
	public New_scheduler() {
		clock = 0;
		main_request = null;
		run_dir = "";
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
	
	@Override
	public void scan_queue(Request_queue queue, Elevator elevator, Floor floor){
		int i;
//		check if time 0 has requests
		refreshButton(queue, elevator, floor, clock);
		while (!queue.isEmpty()||main_request != null){
			if (main_request == null){
				main_request = getMain(queue);
				if (main_request.getTime() > clock) {
					clock = main_request.getTime();
					lock_button(main_request, elevator, floor);
					refreshButton(queue, elevator, floor, clock);
				}
				run_dir = (elevator.getCurrentfr() < main_request.getFlr())? "UP":(elevator.getCurrentfr() == main_request.getFlr())?"STILL":"DOWN";
				
				if (run_dir == "STILL") {
					addClock(queue, elevator, floor);
					addClock(queue, elevator, floor);
					run_ele(clock, run_dir, main_request, elevator);
					unlock_button(main_request, elevator, floor);
					if (elevator.getCurrentfr() == main_request.getFlr()) main_request = null;
					continue;
				}
			}
			clock += 0.5;
			elevator.setCurrentfr(run_dir);
//				elevator.setState(run_dir);
//				elevator.setTime(clock);
//				System.out.println(main_request+" "+elevator+", at "+clock);
			refreshButton(queue, elevator, floor, clock);


			int door = 0, upflag = 0, downflag = 0, eleflag = 0;
			if ((floor.getUplock(elevator.getCurrentfr()) == 1 && floor.getUprequest(elevator.getCurrentfr()).getTime() < clock && run_dir.equals("UP"))
					|| (main_request.getCat().equals("FR") && elevator.getCurrentfr() == main_request.getFlr() && main_request.getDR_dir().equals("UP")) ){
				run_ele(clock, run_dir, floor.getUprequest(elevator.getCurrentfr()), elevator);
				queue.rmSpecified(floor.getUprequest(elevator.getCurrentfr()));
				door = 1;
				upflag = 1;
			}
			if ((floor.getDownlock(elevator.getCurrentfr()) == 1 && floor.getDownrequest(elevator.getCurrentfr()).getTime() < clock && run_dir.equals("DOWN")) 
					|| (main_request.getCat().equals("FR") && elevator.getCurrentfr() == main_request.getFlr() && main_request.getDR_dir().equals("DOWN")) ){
				run_ele(clock, run_dir, floor.getDownrequest(elevator.getCurrentfr()), elevator);
				queue.rmSpecified(floor.getDownrequest(elevator.getCurrentfr()));
				door = 1;
				downflag = 1;
			}
			if ((elevator.getButton(elevator.getCurrentfr()) == 1 && elevator.getRequest(elevator.getCurrentfr()).getTime() < clock )
					|| (main_request.getCat().equals("ER") && elevator.getCurrentfr() == main_request.getFlr()) ){
				run_ele(clock, run_dir, elevator.getRequest(elevator.getCurrentfr()), elevator);
				queue.rmSpecified(elevator.getRequest(elevator.getCurrentfr()));	
				door = 1;
				eleflag = 1;
			}
			if (door == 1){
				addClock(queue, elevator, floor);
				addClock(queue, elevator, floor);
			}

			if (upflag == 1)
				floor.unlockUp(elevator.getCurrentfr());
			if (downflag == 1)
				floor.unlockDown(elevator.getCurrentfr());
			if (eleflag == 1)
				elevator.unlock(elevator.getCurrentfr());
			if (elevator.getCurrentfr() == main_request.getFlr()) main_request = null;
		}
	}

	public void addClock(Request_queue queue, Elevator elevator, Floor floor){
		clock += 0.5;
		refreshButton(queue, elevator, floor, clock);
	}
	
	public void refreshButton(Request_queue queue, Elevator elevator, Floor floor, double now){
		int i;
		Request temp_re;
		for (i = 0;i < queue.getSize();i++){
			temp_re = queue.getKth(i);
			if (temp_re.getTime() != now) continue;

			if (checkButton(temp_re, elevator, floor, queue)){
				if (temp_re.getCat().equals("ER")){
					if ( (run_dir.equals("UP") && temp_re.getFlr() > main_request.getFlr()) 
					  || (run_dir.equals("DOWN") && temp_re.getFlr() < main_request.getFlr()) ){
						temp_re.setPiggyback();
					}
				}
				
			
				lock_button(temp_re, elevator, floor);
			}
		}
	}
	
	public boolean checkButton(Request run_request, Elevator elevator, Floor floor, Request_queue que){
		if (run_request.getCat().equals("ER")) {
			if (elevator.getButton(run_request.getFlr()) == 1){
				System.out.println("The button has been pressed and is not finished yet");
				System.out.println("SAME "+run_request);
				que.rmSpecified(run_request);
				return false;
			}
		}
			
		else if (run_request.getCat().equals("FR")){
			if (floor.getLock(run_request.getFlr(), run_request.getDR_dir())){
				System.out.println("The button has been pressed and is not finished yet");
				System.out.println("SAME "+run_request);
				que.rmSpecified(run_request);
				return false;
			}
		}
		else{
//			error for debug
			System.out.println("FATAL ERROR2");
			System.exit(0);
		}
		
		return true;
	}
	

	public void run_ele(double now, String run_dir, Request re, Elevator ele){
		ele.setState(run_dir);
		ele.setTime(now);
		System.out.println(re + "/" + ele);
	}
	

}
