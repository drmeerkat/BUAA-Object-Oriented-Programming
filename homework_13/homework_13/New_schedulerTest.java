package homework_13;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
 
public class New_schedulerTest { 

	New_scheduler scheduler;
	Elevator elevator = new Elevator();
	Floor floor = new Floor();
	Request_queue queue = new Request_queue();

	@Before
	public void setUp() throws Exception {
		scheduler = new New_scheduler(queue, elevator, floor);
	}

	@After
	public void tearDown() throws Exception {
		scheduler = null;
	}

	@Test
	public void testNew_scheduler() {
		scheduler = new New_scheduler(null, elevator, floor);
		assertNull(scheduler.run_dir);
		scheduler = new New_scheduler(queue, null, floor);
		assertNull(scheduler.run_dir);
		scheduler = new New_scheduler(queue, elevator, null);
		assertNull(scheduler.run_dir);
		scheduler = new New_scheduler(queue, elevator, floor);
		assertNull(scheduler.main_request);
		assertEquals("", scheduler.run_dir);
		assertEquals(0, scheduler.clock, 0.0001F);
		assertEquals(scheduler.elevator, elevator);
		assertEquals(queue, scheduler.queue);
		assertEquals(floor, scheduler.floor);
		
	}

	@Test
	public void testRepOK() {
		assertTrue(scheduler.repOK());
		scheduler.run_dir = null;
		assertFalse(scheduler.repOK());
		scheduler = new New_scheduler(queue, elevator, floor);
		scheduler.queue = null;
		assertFalse(scheduler.repOK());
		scheduler = new New_scheduler(queue, elevator, floor);
		scheduler.elevator = null;
		assertFalse(scheduler.repOK());
		scheduler = new New_scheduler(queue, elevator, floor);
		scheduler.floor = null;
		assertFalse(scheduler.repOK());
	}
 
	@Test
	public void testGetMain() {
		Request testre0 = new Request("FR", "(FR,1,UP,0)", 0);
		Request testre1 = new Request("ER", "(ER,8,2)", 1);
		Request testre2 = new Request("ER", "(ER,6,3)", 2);
		Request testre3 = new Request("ER", "(ER,5,4)", 3);
		Request testre4 = new Request("ER", "(ER,8,6)", 4);
		queue.request_q.add(testre0);
		queue.request_q.add(testre1);
		queue.request_q.add(testre2);
		queue.request_q.add(testre3);
		queue.request_q.add(testre4);
		testre3.setPiggyback();
		scheduler.main_request = scheduler.getMain();
		assertEquals(testre3.getNO(),scheduler.main_request.getNO());
		scheduler.main_request = scheduler.getMain();
		assertEquals(testre0.getNO(),scheduler.main_request.getNO());
	}

	@Test
	public void testScan_queue() {
		queue = new Request_queue();
		elevator = new Elevator();
		floor = new Floor();
		scheduler = new New_scheduler(queue, elevator, floor);
		try {
			setUp();
		} catch (Exception e) {
			e.printStackTrace(); 
		} 
		Request testre = new Request("FR", "(FR,1,UP,1)", 0);
		Request testre0 = new Request("ER", "(ER,1,1)", 1);
		Request testre1 = new Request("ER", "(ER,8,2)", 2);
		Request testre2 = new Request("ER", "(ER,6,3)", 3);
		Request testre3 = new Request("ER", "(ER,5,4)", 4);
		Request testre4 = new Request("ER", "(ER,8,6)", 5);
		Request testre5 = new Request("ER", "(ER,8,6)", 5);
		Request testre6 = new Request("ER", "(ER,10,7)", 6);
		Request testre7 = new Request("ER", "(ER,1,8)", 7);
		Request testre8 = new Request("FR", "(FR,5,DOWN,9)", 8);
		Request testre9 = new Request("ER", "(ER,10,10)", 9);
		Request testre10 = new Request("FR", "(FR,3,DOWN,11)", 10);
		Request testre11 = new Request("FR", "(FR,8,UP,11)", 11);
		Request testre12 = new Request("FR", "(FR,3,UP,12)", 12);
		 
		Request testre13 = new Request("FR", "(FR,5,DOWN,20)", 13); 
		Request testre14 = new Request("FR", "(FR,6,UP,30)", 14); 
		Request testre15 = new Request("FR", "(FR,7,DOWN,40)", 15); 
		  
		queue.request_q.add(testre); 
		queue.request_q.add(testre0);
		queue.request_q.add(testre1);
		queue.request_q.add(testre2);
		queue.request_q.add(testre3);
		queue.request_q.add(testre4);
		queue.request_q.add(testre5);
		queue.request_q.add(testre6);
		queue.request_q.add(testre7);
		queue.request_q.add(testre8);
		queue.request_q.add(testre9);
		queue.request_q.add(testre10);
		queue.request_q.add(testre11); 
		queue.request_q.add(testre12);
		queue.request_q.add(testre13);
		queue.request_q.add(testre14);
		queue.request_q.add(testre15);
		scheduler.scan_queue();
	}


	@Test
	public void testRefreshButton() {
		queue = new Request_queue();
		elevator = new Elevator();
		try {
			setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}  
		Request testre0 = new Request("FR", "(FR,1,UP,0)", 0);
		Request testre1 = new Request("ER", "(ER,8,2)", 1);
		Request testre2 = new Request("ER", "(ER,6,3)", 2);
		Request testre3 = new Request("ER", "(ER,5,4)", 3);
		Request testre4 = new Request("ER", "(ER,9,6)", 4);
		queue.request_q.addFirst(testre0);
		queue.request_q.addFirst(testre1);
		queue.request_q.addFirst(testre2);
		queue.request_q.addFirst(testre3);
		 
		scheduler.clock = 3;
		scheduler.run_dir = "UP";
		scheduler.main_request = new Request("ER", "(ER,4,0)", 4);
		scheduler.refreshButton();
		assertTrue(testre2.getPiggyback());
		assertFalse(scheduler.checkButton(testre2));
		
		scheduler.clock = 4;
		scheduler.run_dir = "DOWN";
		scheduler.main_request = new Request("ER", "(ER,8,0)", 4);
		scheduler.refreshButton();
		assertTrue(testre3.getPiggyback());
		assertFalse(scheduler.checkButton(testre3));
		
		queue = new Request_queue();
		elevator = new Elevator();
		try {
			setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
 
		queue.request_q.addFirst(testre0);
		queue.request_q.addFirst(testre1);
		queue.request_q.addFirst(testre2);
		queue.request_q.addFirst(testre3);
		scheduler.clock = 2;
		scheduler.run_dir = "UP";
		scheduler.main_request = new Request("ER", "(ER,10,0)", 4);
		scheduler.refreshButton();
		assertFalse(testre1.getPiggyback());
		assertFalse(scheduler.checkButton(testre1));
		
		
		queue = new Request_queue();
		elevator = new Elevator();
		try {
			setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		queue.request_q.addFirst(testre0); 
		queue.request_q.addFirst(testre1);
		queue.request_q.addFirst(testre2);
		queue.request_q.addFirst(testre3);
		scheduler.clock = 6; 
		scheduler.run_dir = "DOWN"; 
		scheduler.main_request = new Request("ER", "(ER,5,10)", 4);
		scheduler.refreshButton();
		assertFalse(testre4.getPiggyback()); 
		assertTrue(scheduler.checkButton(testre4));  
		
		scheduler.clock = 0; 
		scheduler.run_dir = "UP"; 
		scheduler.main_request = new Request("ER", "(ER,5,10)", 4);
		scheduler.refreshButton(); 
		assertFalse(testre1.getPiggyback()); 
		assertTrue(scheduler.checkButton(testre1)); 
		
		queue.request_q.addFirst(testre0); 
		queue.request_q.addFirst(testre0);
		floor.setUplock(1, testre0);
		scheduler.clock = 0; 
		scheduler.run_dir = "UP";  
		scheduler.main_request = new Request("ER", "(ER,5,10)", 4);
		scheduler.refreshButton();
		assertFalse(testre1.getPiggyback()); 
		assertTrue(scheduler.checkButton(testre1)); 
		 
		queue = new Request_queue();
		elevator = new Elevator();
		floor = new Floor();
		scheduler = new New_scheduler(queue, elevator, floor);
		queue.request_q.addFirst(testre3);
		scheduler.clock = 4;
		scheduler.run_dir = "DOWN";
		scheduler.main_request = new Request("ER", "(ER,4,0)", 4);
		scheduler.refreshButton();
		assertTrue(testre3.getPiggyback());
		assertFalse(scheduler.checkButton(testre3));
	}



	@Test
	public void testCheckButton() {
		Request testre0 = new Request("FR", "(FR,1,UP,0)", 0);
		Request testre1 = new Request("ER", "(ER,8,0)", 1);
		Request testre2 = new Request("ER", "(ER,6,3)", 2);
		queue.request_q.addFirst(testre0);
		queue.request_q.addFirst(testre1);
		queue.request_q.addFirst(testre2);
		scheduler.lock_button(testre0); 
		assertFalse(scheduler.checkButton(testre0));
		assertTrue(scheduler.checkButton(testre1));
		assertTrue(scheduler.checkButton(testre2));
		scheduler.unlock_button(testre0);
		scheduler.lock_button(testre1);
		assertTrue(scheduler.checkButton(testre0));
		assertFalse(scheduler.checkButton(testre1));
		assertTrue(scheduler.checkButton(testre2));
		scheduler.unlock_button(testre1);
		scheduler.lock_button(testre2);
		assertTrue(scheduler.checkButton(testre0));
		assertTrue(scheduler.checkButton(testre1));
		assertFalse(scheduler.checkButton(testre2));
	}

	@Test
	public void testRun_ele() {
		double now = 10;
		String run_dir = "UP";
		Request button_re1 = new Request("ER", "(ER,5,1)", 2); 
		elevator.state = "DOWN"; 
		elevator.current_fr = 8;
		elevator.time = 100;
		String temp =  scheduler.run_ele(now, run_dir, button_re1);
		assertEquals(10, elevator.time, 0.0001F);
		assertEquals(8, elevator.current_fr);
		assertEquals("UP", elevator.state);
		assertEquals(button_re1+"/"+elevator, temp);
	}

	@Test
	public void testLock_button() {
		Request button_re1 = new Request("ER", "(ER,5,1)", 2); 
		Request button_re2 = new Request("FR", "(FR,2,UP,3)", 3);
		Request button_re3 = new Request("FR", "(FR,5,DOWN,3)", 3);
		scheduler.lock_button(button_re1);
		assertEquals(1, elevator.button[4]);
		assertEquals(button_re1, elevator.re_button[4]); 
		scheduler.lock_button(button_re2);
		assertEquals(1, floor.getUplock(2));
		assertEquals(button_re2,floor.getUprequest(2)); 
		scheduler.lock_button(button_re3);
		assertEquals(1, floor.getDownlock(5));
		assertEquals(button_re3,floor.getDownrequest(5));
	}

	@Test
	public void testUnlock_button() {
		Request button_re1 = new Request("ER", "(ER,5,1)", 2);
		Request button_re2 = new Request("FR", "(FR,2,UP,3)", 3);
		Request button_re3 = new Request("FR", "(FR,5,DOWN,3)", 3);
		elevator.button[4] = 1;
		elevator.re_button[4] = button_re1;
		floor.setUplock(2, button_re2);
		floor.setDownlock(5, button_re3);
		scheduler.unlock_button(button_re1);
		assertEquals(0, elevator.button[4]);
		assertEquals(null, elevator.re_button[4]); 
		scheduler.unlock_button(button_re2);
		assertEquals(0, floor.getUplock(2));
		assertNull(floor.getUprequest(2));
		scheduler.unlock_button(button_re3);
		assertEquals(0, floor.getDownlock(5));
		assertNull(floor.getDownrequest(5));
	}

}
