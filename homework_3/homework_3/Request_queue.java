package homework_3;

import java.util.LinkedList;

public class Request_queue {
	private LinkedList<Request> request_q;
	public Request_queue() {
		request_q = new LinkedList<Request>();
	}
	
	public void add(Request r){
		request_q.addFirst(r);
	}
	
	public Request getFirst (){
		return request_q.getFirst();
	}
	
	public Request getLast(){
		return request_q.removeLast();
	}
	
	public Request getKth(int k){
		return request_q.get(k);
	}
	
	public boolean isEmpty(){
		return request_q.isEmpty();
	}
	
	public int getSize(){
		return request_q.size();
	}
	
	public Request rmKth(int k){
		return request_q.remove(k);
	}
	
	public void rmSpecified(Request re){
		int i;
		Request temp;
		for (i = 0;i < this.getSize();i++){
			temp = this.getKth(i);
			if (temp.getNO() == re.getNO()){
				this.rmKth(i);
//				test message
//				System.out.println("remove success "+re.getNO());
				break;
			}
		}
	}
}
