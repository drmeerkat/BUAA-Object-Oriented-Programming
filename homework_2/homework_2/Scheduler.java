package homework_2;

public class Scheduler {
	private Request temp_request;
	private Request run_request;
	private double ele_time;
	private int current_fr;
	
	public Scheduler(){}
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


			if (run_request.getCat().equals("ER")) 
				elevator.pressButton(run_request.getFlr());
			else if (run_request.getCat().equals("FR")){
				if (run_request.getDR_dir().equals("UP")) 
					floor.setUplock(run_request.getFlr());
				else if (run_request.getDR_dir().equals("DOWN")) 
					floor.setDownlock(run_request.getFlr());
				else {
//					error for debug
					
 					System.out.println("FATAL ERROR1/"+run_request.getCat()+"/");
					System.exit(0);
				}
			}
			else{
//				error for debug
				System.out.println("FATAL ERROR2");
				System.exit(0);
			}


			for (i = queue.getSize() - 1;i >= 0;i--){
				temp_request = queue.getKth(i);
				int temp_flr = temp_request.getFlr();
				if (temp_request.getTime() > run_time + ele_time) break;
				if ((temp_request.getCat().equals("ER") && elevator.getButton(temp_flr) == 1)
						|| (temp_request.getCat().equals("FR") && floor.getLock(temp_flr, temp_request.getDR_dir())) ){
					queue.rmKth(i);
				}
			}
			
//			feed the elevator with the request
			elevator.run(run_time, run_dir, run_request.getFlr());
			if (run_request.getCat().equals("ER")) 
				elevator.unlock(run_request.getFlr());
			else if (run_request.getCat().equals("FR")){
				if (run_request.getDR_dir().equals("UP")) 
					floor.unlockUp(run_request.getFlr());
				else if (run_request.getDR_dir().equals("DOWN")) 
					floor.unlockDown(run_request.getFlr());
			}
		}
		System.out.println("Elevator running finished");
	}
}
