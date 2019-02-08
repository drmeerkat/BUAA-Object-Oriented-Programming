package homework_3;

public class Scheduler {
	private Request temp_request;
	private Request run_request;
	private double ele_time;
	private int current_fr;
	
	public Scheduler(){}
	
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
	
	public void unlock_button(Request run_request, Elevator elevator, Floor floor){
		if (run_request.getCat().equals("ER")) 
			elevator.unlock(run_request.getFlr());
		else if (run_request.getCat().equals("FR")){
			if (run_request.getDR_dir().equals("UP")) 
				floor.unlockUp(run_request.getFlr());
			else if (run_request.getDR_dir().equals("DOWN")) 
				floor.unlockDown(run_request.getFlr());
		}
	}
	
	public void scan_queue(Request_queue queue, Elevator elevator, Floor floor){
		int i;
		double run_time;
		String run_dir;

		while (!queue.isEmpty()){
			run_request = queue.getLast();
			if (run_request.getTime() > elevator.getTime()) elevator.setTime(run_request.getTime());
			ele_time = elevator.getTime();
			current_fr = elevator.getCurrentfr();
			run_time = (double)Math.abs(current_fr - run_request.getFlr())/2 + 1;
			run_dir = (current_fr < run_request.getFlr())? "UP":(current_fr == run_request.getFlr())?"STILL":"DOWN";


			lock_button(run_request, elevator, floor);

			for (i = queue.getSize() - 1;i >= 0;i--){
				temp_request = queue.getKth(i);
				int temp_flr = temp_request.getFlr();
				if (temp_request.getTime() > run_time + ele_time) break;
				if ((temp_request.getCat().equals("ER") && elevator.getButton(temp_flr) == 1)
						|| (temp_request.getCat().equals("FR") && floor.getLock(temp_flr, temp_request.getDR_dir())) ){
					queue.rmKth(i);
				}
			}
			
			run_ele(run_time, run_dir, elevator, floor);

		}
		System.out.println("Elevator running finished");
	}
	
	
	public void run_ele(double run_time, String run_dir, Elevator elevator, Floor floor){
//		feed the elevator with the request
		elevator.run(run_time, run_dir, run_request.getFlr());
		unlock_button(run_request, elevator, floor);
	}
}
