package homework_5;

import java.util.concurrent.LinkedBlockingQueue;

public class BlockedRequest_queue {
	private LinkedBlockingQueue<Request> block_rq;
	public BlockedRequest_queue(){
		block_rq = new LinkedBlockingQueue<Request>();
	}
	
	public void Add(Request q){
		try{
			block_rq.put(q);
		}catch (InterruptedException e){
			System.out.println("Stop reading in");
		}
	}
	
	public boolean isEmpty(){
		return block_rq.isEmpty();
	}
	
	public Request Get(){
		try {
			return block_rq.take();
		}catch (InterruptedException e){
			System.out.println("Stop getting new request");
			System.exit(0);
			return null;
		}
	}
}
