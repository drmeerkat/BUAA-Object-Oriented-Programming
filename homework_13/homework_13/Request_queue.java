package homework_13;

import java.util.LinkedList;

public class Request_queue {
	/* Overview
	 * 请求队列类，记录保存输入的请求，提供方法删除指定编号的请求
	 */
	protected LinkedList<Request> request_q;
	public Request_queue() {
		/*
		 * @REQUIRES:	None
		 * @MODIFIES:	None
		 * 				
		 * @EFFECTS:	初始化请求队列
		 */
		request_q = new LinkedList<Request>();
	}
	
	public boolean repOK(){
		/*
		 * @REQUIRES:	None;
		 * @MODIFIES:	None;
		 * @EFFECTS:	Checks if "this" is legal.
		 */
		if (request_q == null)
			return false;
		return true;
	}
	
	public void rmSpecified(Request re){
		/*
		 * @REQUIRES:	Request re;re!=null
		 * @MODIFIES:	requeset_q
		 * 				
		 * @EFFECTS:	从请求队列中删除指定编号的请求。如果不存在这个编号，则不做处理
		 */
		int i;  
		Request temp;
		for (i = 0;i < request_q.size();i++){
			temp = request_q.get(i);
			if (temp.getNO() == re.getNO()){
				request_q.remove(i);
				break;
			}
		}
	}
}
