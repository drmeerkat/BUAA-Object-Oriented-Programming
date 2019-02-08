package homework_14;

public class Scheduler {
	/*Overview
	 * 提供基本的电梯运行模拟结果输出方法，并对需要调度管理的电梯，请求队列，楼层类进行初始化
	 * 抽象函数：AF(c)=(queue,elevator,floor) where queue=c.queue, elevator=c.elevator, floor=c.floor
	 */
	protected static Request_queue queue; 
	protected static Elevator elevator;  
	protected static Floor floor;
	
	public Scheduler(Request_queue queue, Elevator elevator, Floor floor){
		/*
		 * @REQUIRES:	/all Request_queue queue, Elevator elevator, Floor floor;
		 * 				queue!=null , elevator!=null, floor!=null
		 * @MODIFIES:	this
		 * @EFFECTS:    初始化调度器，如果输入的不符合require，构造函数报错并退出程序
		 */
		if (queue==null||elevator==null||floor==null){
			System.out.println("Wrong input");
			System.exit(0);
		}
		else{
			Scheduler.elevator = elevator;
			Scheduler.floor = floor;
			Scheduler.queue = queue;
		}
	}
	
	public boolean repOK(){
		/*
		 * @REQUIRES:	None;
		 * @MODIFIES:	None;
		 * @EFFECTS:	Checks if "this" is legal.
		 */
		if (queue == null||elevator == null||floor == null)
			return false;
		return true;
	}
	

	public String run_ele(Request re){
		/*
		 * @REQUIRES:	Request re;re!=null
		 * @MODIFIES:	elevator
		 * @EFFECTS:	如果输入re为空则报错退出
		 * 				否则
		 * 				根据输入的当前执行完成的请求输出执行信息
		 */
		if (re==null){
			System.out.println("Wrong input");
			System.exit(0);
		}
		System.out.println(re + "/" + elevator);
		return re + "/" + elevator;
	}
}
