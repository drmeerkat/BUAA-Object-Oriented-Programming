package homework_3;
import java.util.regex.*;

import java.util.Scanner;


public class Readin {
	

	public static void main(String[] args) {
		int request_count = 0;
		Scanner scan = new Scanner(System.in);
		String line = "";
		String char_pattern = "[^\\dEFRDOWNUP,\\(\\)\\+]";
		Pattern find_char = Pattern.compile(char_pattern);
		Matcher match_char;
		Scheduler scheduler = new New_scheduler();
		Elevator elevator = new Elevator();
		Floor floor = new Floor();
		Request_queue request_queue = new Request_queue();
		Request temp_request;
		try {
			System.out.println("Please input your request");
			while (true) {
				line = scan.nextLine().replace(" ", "");
				if (line.equals("start")) break;
				match_char = find_char.matcher(line);
				if (match_char.find()){
					System.out.println("your input has illegal characters or you have input the wrong spell of 'start' and will be skipped");
					System.out.println("INVALID " + line);
					continue;
				}
				if (elevator.ele_request(line)){
//					System.out.println("ele test success");
					temp_request = new Request("ER", line, request_count);
				}
				else if (floor.flr_request(line)){
//					System.out.println("floor test success");
					temp_request = new Request("FR", line, request_count);
				}
				else {
					System.out.println("Match failed, your input will be skipped");
					System.out.println("INVALID " + line);
					continue;
				}
				if (!request_queue.isEmpty() && temp_request.getTime() < request_queue.getFirst().getTime()){
					System.out.println("Newly coming request's time should be no less than the previous ones' and your input will be skipped");
					System.out.println("INVALID " + line);
					continue;
				}
				request_count++;
				if (request_count == 1 && !temp_request.toString().equals("[FR,1,UP,0]")){
					System.out.println("The first request must be (FR,1,UP,0)");
					System.out.println("INVALID " + line);
					request_count = 0;
					continue;
				}
				request_queue.add(temp_request);
			}
		}
		catch (Exception e){
			System.out.println("FATAL_ERROR: maybe the input is too long, check the virtual space assigned to your JVM");
			System.out.println("INVALID " + line);
			System.exit(0);
		}
		finally {
			scan.close();
		}
//		start to schedule	
		try{
			System.out.println("The elevator starts to run!");
			scheduler.scan_queue(request_queue, elevator, floor);
			System.out.println("The elevator finished running!");
		}
		catch (Exception e) {
			System.out.println("Unknown ERROR: please check your input or the virtual space assigned to your JVM");
			System.exit(0);
		}
		
		
		

	}

}
