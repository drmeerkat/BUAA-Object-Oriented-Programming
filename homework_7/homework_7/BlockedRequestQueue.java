package homework_7;

import java.util.LinkedList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockedRequestQueue {
	private LinkedBlockingQueue<Request> bq;
	
	public BlockedRequestQueue() {
		bq = new LinkedBlockingQueue<>();
	}

	public int getSize(){
		return bq.size();
	}
	
	public boolean isEmpty(){
		return bq.isEmpty();
	}
	
	public void put(Request re){
		try {
			bq.put(re);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void addAll(LinkedList<Request> temp){
		bq.addAll(temp);
	}
	
	public Request take(){
		try {
			return bq.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Stop getting requests");
			return null;
		}
	}
	
	public long checkTime(){
		return bq.peek().getStart_time();
	}

	
	
}
