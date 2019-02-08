package homework_13;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
 
public class Request_queueTest {
	
	private Request_queue queue; 


	@Before
	public void setUp() throws Exception {
		queue = new Request_queue();
	}

	@After
	public void tearDown() throws Exception {
		queue = null;
	}

	@Test
	public void testRequest_queue() {
		assertNotNull(queue.request_q);
	}

	@Test
	public void testRepOK() {
		queue.request_q = null;
		assertFalse(queue.repOK());
		queue.request_q = new LinkedList<>();
		assertTrue(queue.repOK());
	}

	@Test
	public void testRmSpecified() { 
		Request testre0 = new Request("FR", "(FR,1,UP,0)", 0);
		Request testre1 = new Request("ER", "(ER,8,0)", 1);
		Request testre2 = new Request("ER", "(ER,6,3)", 2);
		Request testre3 = new Request("ER", "(ER,6,3)", 3);
		queue.request_q.addFirst(testre0);
		queue.request_q.addFirst(testre1);
		queue.request_q.addFirst(testre2);
		assertEquals(queue.request_q.size(), 3);
		queue.rmSpecified(testre1);
		assertEquals(0, queue.request_q.get(1).getNO());
		queue.rmSpecified(testre3);
		
	}

}
