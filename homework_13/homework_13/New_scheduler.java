package homework_13;

import java.util.LinkedList;

public class New_scheduler{
	/* Overview
	 * 管理电梯，请求队列，楼层按钮三个类的实例，
	 * 并根据请求队列和当前运行时间进行调度，模拟电梯运行，输出信息
	 */
	
	protected double clock;
	protected Request main_request;
	protected String run_dir;
	protected Request_queue queue; 
	protected Elevator elevator;  
	protected Floor floor;
	
	
	public New_scheduler(Request_queue queue, Elevator elevator, Floor floor) {
		/*
		 * @REQUIRES:	/all Request_queue queue, Elevator elevator, Floor floor;
		 * 				queue!=null , elevator!=null, floor!=null
		 * @MODIFIES:	clock
		 * 				main_request
		 * 				run_dir
		 * 				this
		 * @EFFECTS:    初始化调度器, 设置起始时刻为0.如果输入的不符合require，构造函数报错并退出
		 */
		if (queue==null||elevator==null||floor==null){
			System.out.println("Wrong input");
		}
		else{
			clock = 0;
			main_request = null;
			run_dir = "";
			this.elevator = elevator;
			this.floor = floor;
			this.queue = queue;
		}
	}
	
	public boolean repOK(){
		/*
		 * @REQUIRES:	None;
		 * @MODIFIES:	None;
		 * @EFFECTS:	Checks if "this" is legal.
		 */
		if (run_dir == null||queue == null||elevator == null||floor == null)
			return false;
		return true;
	}
	
	public Request getMain(){
		/*
		 * @REQUIRES:	None
		 * @MODIFIES:	requeset_q
		 * 				
		 * @EFFECTS:	从请求队列中获取下一个要执行的主请求
		 */
		int i;
		for (i = 0;i <= queue.request_q.size()-1;i++){
			if (queue.request_q.get(i).getPiggyback()) {
				return queue.request_q.remove(i);
			}
		}
		
		return queue.request_q.removeFirst();
	}
	
	 
	public void scan_queue(){
		/*
		 * @REQUIRES:	None
		 * @MODIFIES:	elevator
		 * 				floor
		 * 				queue
		 * 				this
		 * @EFFECTS:	扫描请求队列发现所有可做为主请求或捎带的请求，刷新按钮状态和请求队列状态
		 * 				并根据请求的执行情况开关门，更新运动方向，增加运行时间
		 */
//		check if time 0 has requests
		refreshButton(); 
		while (main_request != null||!queue.request_q.isEmpty()){ 
			if (main_request == null){
				main_request = getMain();
				if (main_request.getTime() > clock) {
					clock = main_request.getTime();
					lock_button(main_request);
					refreshButton();
				}
				run_dir = (elevator.current_fr < main_request.getFlr())? "UP":(elevator.current_fr == main_request.getFlr())?"STILL":"DOWN";
				
				if (run_dir == "STILL") {
					clock += 0.5;
					refreshButton();
					clock += 0.5;
					refreshButton();
					run_ele(clock, run_dir, main_request);
					unlock_button(main_request);
					main_request = null;
					continue;
				}
			} 
			clock += 0.5;
			elevator.setCurrentfr(run_dir);
//				elevator.setState(run_dir);
//				elevator.setTime(clock);
//				System.out.println(main_request+" "+elevator+", at "+clock);
			refreshButton();
 

			int door = 0, upflag = 0, downflag = 0, eleflag = 0;  
			if ((floor.getUplock(elevator.current_fr) == 1 && run_dir.equals("UP")) ){
				run_ele(clock, run_dir, floor.getUprequest(elevator.current_fr));
				queue.rmSpecified(floor.getUprequest(elevator.current_fr ));
				door = 1;
				upflag = 1;  
			} 
			if ((floor.getDownlock(elevator.current_fr) == 1 && run_dir.equals("DOWN")) ){
				run_ele(clock, run_dir, floor.getDownrequest(elevator.current_fr));
				queue.rmSpecified(floor.getDownrequest(elevator.current_fr));
				door = 1;
				downflag = 1;
			} 
			if (elevator.button[elevator.current_fr-1] == 1 ){
				run_ele(clock, run_dir, elevator.re_button[elevator.current_fr-1]);
				queue.rmSpecified(elevator.re_button[elevator.current_fr-1]);	
				door = 1;
				eleflag = 1;
			}
			if (door == 1){
				clock += 0.5;
				refreshButton();
				clock += 0.5;
				refreshButton();
			} 

			if (upflag == 1)
				floor.unlockUp(elevator.current_fr);
			if (downflag == 1)
				floor.unlockDown(elevator.current_fr);
			if (eleflag == 1)
				elevator.unlock(elevator.current_fr);
			if (elevator.current_fr == main_request.getFlr()) main_request = null;
		}
	}

//	public void addClock(){
//		/*
//		 * @REQUIRES:	None
//		 * @MODIFIES:	clock
//		 * 				this
//		 * @EFFECTS:	增加时钟计数，并根据更新后的时间刷新按钮状态
//		 */
//		clock += 0.5;
//		refreshButton();
//	} 
	
	public void refreshButton(){
		/*
		 * @REQUIRES:	None
		 * @MODIFIES:	this
		 * @EFFECTS:	根据更新后的时间扫描请求队列将当前时间节点的请求对应的按钮点亮；
		 * 				并判断当前有没有可以捎带的电梯类请求
		 */ 
		double now = clock;
		int i;  
		Request temp_re;
		@SuppressWarnings("unchecked")
		LinkedList<Request> temp_list = (LinkedList<Request>)queue.request_q.clone();
		for (i = 0;i < temp_list.size();i++){
			temp_re = temp_list.get(i);
			if (temp_re.getTime() != now) 
				continue;   
			if (checkButton(temp_re) ){
				if ((temp_re.getCat().equals("ER"))){
					if ( (run_dir.equals("UP") 
							&& temp_re.getFlr() > main_request.getFlr()) 
					  || (run_dir.equals("DOWN") 
							  && temp_re.getFlr() < main_request.getFlr()) ){
						temp_re.setPiggyback();
					}
				}
				lock_button(temp_re);
			}
		}
	}
	
	public boolean checkButton(Request run_request){
		/*
		 * @REQUIRES:	Request run_request;run_request!=null
		 * @MODIFIES:	this
		 * 				
		 * @EFFECTS:	if 输入请求属于ER类请求
		 * 					if 对应电梯按钮已经点亮
		 * 						从请求队列中删除这个请求并输出重复信息
		 * 						return false
		 *				else 
		 *					//输入请求属于FR类请求
		 *					if 对应的楼层上对应方向按钮已经点亮
		 *						从请求队列中删除这个请求并输出重复信息
		 *						return false
		 *				return true
		 *				
		 */
		if (run_request.getCat().equals("ER")) {
			if (elevator.button[run_request.getFlr()-1] == 1){
				System.out.println("The button has been pressed and is not finished yet");
				System.out.println("SAME "+run_request);
				queue.rmSpecified(run_request);
				return false;
			}
		}
			
		else{
			if (floor.getLock(run_request.getFlr(), run_request.getDR_dir())){
				System.out.println("The button has been pressed and is not finished yet");
				System.out.println("SAME "+run_request);
				queue.rmSpecified(run_request);
				return false;
			}
		}
		
		
		return true;
	}
	

	public String run_ele(double now, String run_dir, Request re){
		/*
		 * @REQUIRES:	Request re;re!=null
		 * @MODIFIES:	elevator
		 * @EFFECTS:	根据输入的时刻和运动方向更新电梯状态
		 * 				根据输入的当前执行完成的请求输出执行信息
		 */
		elevator.state = run_dir;
		elevator.time = now;
		System.out.println(re + "/" + elevator);
		return re + "/" + elevator;
	}
	
	public void lock_button(Request run_request){
		/*
		 * @REQUIRES:	Request run_request;run_request!=null
		 * @MODIFIES:	this
		 * @EFFECTS:	if 输入请求属于ER类请求
		 * 					点亮对应的电梯按钮
		 *				else if 输入请求属于FR类请求
		 *					if 请求方向是向上的
		 *						点亮对应楼层的上行按钮
		 *					else if 请求方向是向下的
		 *						点亮对应楼层的下行按钮
		 */
		if (run_request.getCat().equals("ER")) {
			elevator.pressButton(run_request);
		}
			
		else{
			if (run_request.getDR_dir().equals("UP")) 
				floor.setUplock(run_request.getFlr(), run_request);
			else 
				floor.setDownlock(run_request.getFlr(), run_request);

		}
		
	}
	
	public void unlock_button(Request run_request){
		/*
		 * @REQUIRES:	Request run_request;run_request!=null
		 * @MODIFIES:	this
		 * @EFFECTS:	根据输入的请求判断是电梯请求还是楼层请求，并将对应的按钮熄灭
		 */
		if (run_request.getCat().equals("ER")) 
			elevator.unlock(run_request.getFlr());
		else{ 
			if (run_request.getDR_dir().equals("UP")) 
				floor.unlockUp(run_request.getFlr());
			else 
				floor.unlockDown(run_request.getFlr());
		}
	}
	

}
