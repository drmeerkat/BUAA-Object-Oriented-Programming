package homework_14;

import java.util.LinkedList;

public class Button {
	
	public static void refreshButton(){
		double now = New_scheduler.clock;
		int i;  
		Request temp_re;
		@SuppressWarnings("unchecked")
		LinkedList<Request> temp_list = (LinkedList<Request>)New_scheduler.queue.request_q.clone();
		for (i = 0;i < temp_list.size();i++){
			temp_re = temp_list.get(i);
			if (temp_re.getTime() != now) 
				continue;   
			if (checkButton(temp_re) ){
				if ((temp_re.getCat().equals("ER"))){
					if ( (New_scheduler.run_dir.equals("UP") 
							&& temp_re.getFlr() > New_scheduler.main_request.getFlr()) 
					  || (New_scheduler.run_dir.equals("DOWN") 
							  && temp_re.getFlr() < New_scheduler.main_request.getFlr()) ){
						temp_re.setPiggyback();
					}
				}
				lock_button(temp_re);
			}
		}
	}
	
	public static boolean checkButton(Request run_request){
		if (run_request.getCat().equals("ER")) {
			if (New_scheduler.elevator.button[run_request.getFlr()-1] == 1){
				System.out.println("The button has been pressed and is not finished yet");
				System.out.println("SAME "+run_request);
				New_scheduler.queue.rmSpecified(run_request);
				return false;
			}
		}
			
		else{
			if (New_scheduler.floor.getLock(run_request.getFlr(), run_request.getDR_dir())){
				System.out.println("The button has been pressed and is not finished yet");
				System.out.println("SAME "+run_request);
				New_scheduler.queue.rmSpecified(run_request);
				return false;
			}
		}
		
		
		return true;
	}
	


	
	public static void lock_button(Request run_request){
		if (run_request.getCat().equals("ER")) {
			New_scheduler.elevator.pressButton(run_request);
		}
			
		else{
			if (run_request.getDR_dir().equals("UP")) 
				New_scheduler.floor.setUplock(run_request.getFlr(), run_request);
			else 
				New_scheduler.floor.setDownlock(run_request.getFlr(), run_request);

		}
		
	}
	
	public static void unlock_button(Request run_request){
		if (run_request.getCat().equals("ER")) 
			New_scheduler.elevator.unlock(run_request.getFlr());
		else{ 
			if (run_request.getDR_dir().equals("UP")) 
				New_scheduler.floor.unlockUp(run_request.getFlr());
			else 
				New_scheduler.floor.unlockDown(run_request.getFlr());
		}
	}
}
