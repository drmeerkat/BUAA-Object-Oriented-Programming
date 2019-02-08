package homework_13;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ElevatorTest { 
	Elevator elevator;
	

	@Before
	public void setUp(){
		elevator = new Elevator();  
		elevator.state = "UP";
		elevator.current_fr = 8;
		elevator.time = 100;
	}

	@After
	public void tearDown() throws Exception {
		elevator = null;
	} 

	@Test
	public void testElevator() {
		elevator = new Elevator(); 
		assertEquals(0,elevator.time,0.0001F);
		assertEquals("",elevator.state);
		assertNotNull(elevator.re_button);
		assertEquals(10, elevator.re_button.length);
		assertEquals(10, elevator.button.length);
		for (int i=0;i<10;i++){
			assertEquals(0, elevator.button[i]);
		}
	}

	@Test
	public void testRepOK() {
		elevator.state=null;
		assertFalse(elevator.repOK());
		setUp();
		assertTrue(elevator.repOK());
		elevator.re_button=null;
		assertFalse(elevator.repOK());
		setUp();
		elevator.button=null;
		assertFalse(elevator.repOK());
		setUp();
		elevator.current_fr=11;
		assertFalse(elevator.repOK());
		setUp();
		elevator.current_fr=0;
		assertFalse(elevator.repOK());
	}

	@Test
	public void testToString() {
		assertEquals("(8,UP,100.0)", elevator.toString());
	}

	@Test
	public void testEle_request() {
		String line1 = "sdkhfks"; 
		String line2 = "(ER,+4,2)";
		String line3 = "(ER,0,2)";
		String line4 = "(ER,11,2)"; 
		String line5 = "(ER,4,214748364900)";
		assertTrue(elevator.ele_request(line2));
		assertFalse(elevator.ele_request(line1));
		assertFalse(elevator.ele_request(line3));
		assertFalse(elevator.ele_request(line4));
		assertFalse(elevator.ele_request(line5));
	}

	@Test
	public void testAddTime() {
		elevator.addTime(1);
		assertEquals(101, elevator.time,0.0001F);
	}

	@Test
	public void testSetCurrentfr() {
		elevator.setCurrentfr("UP");
		elevator.setCurrentfr("UP");
		assertEquals(10, elevator.current_fr);
		elevator.setCurrentfr("DOWN");
		assertEquals(9, elevator.current_fr);
		elevator.setCurrentfr("??");
	}

	@Test
	public void testPressButton() {
		assertFalse(elevator.pressButton(null));
		Request button_re = new Request("ER", "(ER,10,1)", 4);
		assertTrue(elevator.pressButton(button_re));
		assertEquals(1, elevator.button[9]);
		assertEquals(button_re, elevator.re_button[9]);
	}

	@Test
	public void testUnlock() {
		Request button_re = new Request("ER", "(ER,5,1)", 5);
		assertTrue(elevator.pressButton(button_re));
		assertTrue(elevator.unlock(5));
		assertFalse(elevator.unlock(-1));
		assertFalse(elevator.unlock(11));
		assertFalse(elevator.unlock(5));
	}

}
