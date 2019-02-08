package homework_5;

import java.util.concurrent.TimeUnit;

public class Multi_scheduler extends New_scheduler implements Runnable{
	private BlockedRequest_queue block_queue;
	private Request temp_re;
	private Elevator_System elevator_sys;
	protected static long now_time;
	private Request_queue remain_fr;

	public Multi_scheduler(BlockedRequest_queue bq) {
		block_queue = bq;
		elevator_sys = new Elevator_System(3, this);
		now_time = 0;
		remain_fr = new Request_queue(0);
	}
	
	public Elevator_System getElesys(){
		return elevator_sys;
	}
	
	@Override
	public void run() {
		try {
			while (!Thread.interrupted()){
				synchronized(this) {
					while (Elevator_System.time < now_time){
						wait();
					}
				}
				synchronized (elevator_sys) {
					while (!block_queue.isEmpty()){
						temp_re = block_queue.Get();
						if (!elevator_sys.addRequest(temp_re)){
							remain_fr.add(temp_re);
						}
					}
					if (!remain_fr.isEmpty()){
						int i;
						for (i = 0;i<remain_fr.getSize();i++){
							if (elevator_sys.addRequest(remain_fr.getKth(i))){
								remain_fr.rmKth(i);
							}
						}
					}
					
					TimeUnit.MILLISECONDS.sleep(100);
					now_time += 100;
					elevator_sys.notifyAll();
				}
					
			}
		}catch (InterruptedException e){
			System.out.println("Elevator running is interrupted");
		}
	
	}
	
}
