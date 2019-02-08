package homework_14;
import java.util.regex.*;

import java.util.Scanner;


public class Readin {
	private static long MAX_VALUE = 2147483647L;  

	public static void main(String[] args) {
		int request_count = 0;
		Scanner scan = new Scanner(System.in);
		String line = "";
		String char_pattern = "[^\\dEFRDOWNUP,\\(\\)\\+]";
		Pattern find_char = Pattern.compile(char_pattern);
		Matcher match_char;
		Elevator elevator = new Elevator();
		Floor floor = new Floor();
		Request_queue request_queue = new Request_queue();
		New_scheduler scheduler = new New_scheduler(request_queue, elevator, floor);
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
				if (ele_request(line)){
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
				if (!request_queue.request_q.isEmpty() && temp_request.getTime() < request_queue.request_q.getLast().getTime()){
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
				request_queue.request_q.add(temp_request);
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
			scheduler.scan_queue();
			System.out.println("The elevator finished running!");
		}
		catch (Exception e) {
			System.out.println("Unknown ERROR: please check your input or the virtual space assigned to your JVM");
			System.exit(0);
		}
	}
	
	public static boolean ele_request(String line){
		/*
		 * @REQUIRES:	None
		 * @MODIFIES:	None
		 * @EFFECTS:    如果输入匹配要求的正则表达式，则返回true，否则返回false
		 */
		Pattern find_request = Pattern.compile("\\(ER,(\\+?\\d+),(\\+?\\d+)\\)");
		Matcher match_request = find_request.matcher(line);
		long temp;
		 
		if (match_request.matches()) { 
			temp = Long.parseLong(match_request.group(1));
			if (temp > 10 || temp < 1 
				|| Long.parseLong(match_request.group(2)) > MAX_VALUE){
				return false;
			}
			return true;
		}
			
		else return false;
	}	

}
