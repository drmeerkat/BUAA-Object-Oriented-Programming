package homework_11;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.namespace.QName;

/*Overview
 * 一个阻塞请求队列，负责管理控制台以及测试线程的请求输入和调度系统取出请求，以及协助检查请求时间
 * 保证二者数据安全不产生线程上的数据竞争
 */

public class BlockedRequestQueue {
	private LinkedBlockingQueue<Request> bq;
	
	public BlockedRequestQueue() {
		/*@REQUIRES:None
		 *@MODIFIES:bq
		 *@EFFECTS: 初始化bq队列
		 */
		bq = new LinkedBlockingQueue<>();
	}
	
	public boolean repOK(){
		/*
		 * @REQUIRES:	None;
		 * @MODIFIES:	None;
		 * @EFFECTS:	Checks if "this" is legal.
		 */
		if (bq == null)
			return false;
		return true;
	}

	public int getSize(){
		/*@REQUIRES:None
		 *@MODIFIES:None
		 *@EFFECTS: 返回bq的大小
		 */
		return bq.size();
	}
	
	public boolean isEmpty(){
		/*@REQUIRES:None
		 *@MODIFIES:None
		 *@EFFECTS: return bq.isEmpty()
		 *			返回当前bq是否是空的bool值
		 */
		return bq.isEmpty();
	}
	
	public void put(Request re){
		/*@REQUIRES: \all Request;re ！= null
		 *@MODIFIES:bq
		 *@EFFECTS: normal_behaviour：将re加入请求队列bq
		 *			exceptional_behaviour(InterruptedException e):打印栈信息并退出
		 */
		try {
			bq.put(re);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void addAll(LinkedList<Request> temp){
		/*@REQUIRES:None
		 *@MODIFIES:bq
		 *@EFFECTS: 将temp请求队列全部添加到bq队列中
		 */
		bq.addAll(temp);
	}
	
	public Request take(){
		/*@REQUIRES: (\all LinkedBlockingQueue<Request>;bq != null)
		 *@MODIFIES: bq
		 *@EFFECTS: normal_behaviour:返回当前请求队列的队首请求
		 *			exceptional_behaviour(InterruptedException e):打印栈信息并退出
		 *			
		 */
		try {
			return bq.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Stop getting requests");
			return null;
		}
	}
	
	public long checkTime(){
		/*@REQUIRES:None
		 *@MODIFIES:bq
		 *@EFFECTS: 返回bq队列队首请求的开始时间
		 */
		return bq.peek().getStart_time();
	}
	
}
