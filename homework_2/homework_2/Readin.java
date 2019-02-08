package homework_2;
import java.util.regex.*;

import java.util.Scanner;


public class Readin {
	

	public static void main(String[] args) {
		int request_count = 0;
		Scanner scan = new Scanner(System.in);
		String line;
		String char_pattern = "[^\\dEFRDOWNUP,\\(\\)]";
		Pattern find_char = Pattern.compile(char_pattern);
		Matcher match_char;
		Scheduler scheduler = new Scheduler();
		Elevator elevator = new Elevator();
		Floor floor = new Floor();
		Request_queue request_queue = new Request_queue();
		Request temp_request;
		try {
			System.out.println("Please input your request");
			while (true) {
				line = scan.nextLine().replace(" ", "");
				if (line.length() > 40){
					System.out.println("your input is so long and will be skipped");
					continue;
				}
				if (line.equals("start")) break;
				match_char = find_char.matcher(line);
				if (match_char.matches()){
					System.out.println("your input has illegal characters or you have input the wrong spell of 'start' and will be skipped");
					continue;
				}
				if (elevator.ele_request(line)){
//					System.out.println("ele test success");
					temp_request = new Request("ER", line);
				}
				else if (floor.flr_request(line)){
//					System.out.println("floor test success");
					temp_request = new Request("FR", line);
				}
				else {
					System.out.println("Match failed, your input will be skipped");
					continue;
				}
				if (!request_queue.isEmpty() && temp_request.getTime() < request_queue.getFirst().getTime()){
					System.out.println("Newly coming request's time should be no less than the previous ones' and your input will be skipped");
					continue;
				}
				request_count++;
				if (request_count == 1 && temp_request.getTime() != 0){
					System.out.println("The first legal request should be issued at 0 and your input will be skipped");
					request_count = 0;
					continue;
				}
				request_queue.add(temp_request);
			}
		}
		catch (Exception e){
			System.out.println("FATAL_ERROR:the input is illegal");
			System.exit(0);
		}
		finally {
			scan.close();
		}
//		start to schedule	
		try{
			System.out.println("The elevator starts to run!");
			scheduler.scan_queue(request_queue, elevator, floor);
		}
		catch (Exception e) {
			System.out.println("Unknown_ERROR: please check your input or the virtual space assigned to your JVM");
			System.exit(0);
		}
		
		
		

	}

}
