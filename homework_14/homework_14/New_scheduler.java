package homework_14;


public class New_scheduler extends Scheduler{
	/* Overview
	 * 管理电梯，请求队列，楼层按钮三个类的实例，
	 * 并根据请求队列和当前运行时间进行调度，模拟电梯运行，调用父类方法输出运行信息
	 * 抽象函数：AF(c)=AF(super)&&(clock, main_request, run_dir) where clock is c.clock, 
	 * main_request is c.main_request, run_dir is c.run_dir
	 */
	protected static double clock;
	protected static Request main_request;
	protected static String run_dir;

	
	public New_scheduler(Request_queue queue, Elevator elevator, Floor floor) {
		/*
		 * @REQUIRES:	super.REQUIRES
		 * @MODIFIES:	clock
		 * 				main_request
		 * 				run_dir
		 * 				this
		 * @EFFECTS:    初始化父类调度器和子类调度器, 设置起始时刻为0
		 */
		super(queue,elevator,floor);
		clock = 0;
		main_request = null;
		run_dir = "";
	}
	
	public boolean repOK(){
		/*
		 * @REQUIRES:	None;
		 * @MODIFIES:	None;
		 * @EFFECTS:	Checks if "this" is legal.
		 */
		if (run_dir == null||!super.repOK())
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
		Button.refreshButton(); 
		while (main_request != null||!queue.request_q.isEmpty()){ 
			if (main_request == null){
				main_request = getMain();
				if (main_request.getTime() > clock) {
					clock = main_request.getTime();
					Button.lock_button(main_request);
					Button.refreshButton();
				}
				run_dir = (elevator.current_fr < main_request.getFlr())? "UP":(elevator.current_fr == main_request.getFlr())?"STILL":"DOWN";
				
				if (run_dir == "STILL") {
					clock += 0.5;
					Button.refreshButton();
					clock += 0.5;
					Button.refreshButton();
					elevator.state = run_dir;
					elevator.time = clock;
					run_ele(main_request);
					Button.unlock_button(main_request);
					main_request = null;
					continue;
				}
			} 
			clock += 0.5;
			elevator.setCurrentfr(run_dir);
			Button.refreshButton();
 

			int door = 0, upflag = 0, downflag = 0, eleflag = 0;  
			if ((floor.getUplock(elevator.current_fr) == 1 && run_dir.equals("UP")) ){
				elevator.state = run_dir;
				elevator.time = clock;
				run_ele(floor.getUprequest(elevator.current_fr));
				queue.rmSpecified(floor.getUprequest(elevator.current_fr ));
				door = 1;
				upflag = 1;  
			} 
			if ((floor.getDownlock(elevator.current_fr) == 1 && run_dir.equals("DOWN")) ){
				elevator.state = run_dir;
				elevator.time = clock;
				run_ele(floor.getDownrequest(elevator.current_fr));
				queue.rmSpecified(floor.getDownrequest(elevator.current_fr));
				door = 1;
				downflag = 1;
			} 
			if (elevator.button[elevator.current_fr-1] == 1 ){
				elevator.state = run_dir;
				elevator.time = clock;
				run_ele(elevator.re_button[elevator.current_fr-1]);
				queue.rmSpecified(elevator.re_button[elevator.current_fr-1]);	
				door = 1;
				eleflag = 1;
			}
			if (door == 1){
				clock += 0.5;
				Button.refreshButton();
				clock += 0.5;
				Button.refreshButton();
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
	
}
