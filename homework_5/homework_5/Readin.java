package homework_5;


import java.util.Scanner;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.text.DecimalFormat;



public class Readin implements Runnable{
	private int request_count = 0;
	private Scanner scan;
	private String line;
	protected boolean endflag;
	
	private String[] data;
	
	private long start_time, now;	
	private Request temp_request;
	private BlockedRequest_queue block_rq;
	
	public Readin(BlockedRequest_queue bq) {
		request_count = 0;
		scan = new Scanner(System.in);
		line = "";
		endflag = false;
		
		start_time = System.currentTimeMillis();
		block_rq = bq;
	}
	
	public boolean ele_request(String line){
		Pattern find_request = Pattern.compile("\\(ER,#(\\d),\\+?(\\d+)\\)");
		Matcher match_request = find_request.matcher(line);
		long temp;
		int id;
		
		if (match_request.matches()) {
			temp = Long.parseLong(match_request.group(2));
			id = Integer.parseInt(match_request.group(1));
			if (temp > 20 || temp < 1 
				|| id > 3 || id < 1){
				return false;
			}
			return true;
		}
			
		else return false;
	}
	
	public boolean flr_request(String line){
		long temp;
		Pattern find_request = Pattern.compile("\\(FR,\\+?(\\d+),(UP|DOWN)\\)");
		Matcher match_request = find_request.matcher(line);
		if (match_request.matches()) {
			temp = Long.parseLong(match_request.group(1));
//			System.out.println(match_request.group(3) + match_request.group(2) + match_request.group(1));
			if (temp > 20 || temp < 1 
			  ||(temp == 20 && match_request.group(2).equals("UP"))
			  ||(temp == 1 && match_request.group(2).equals("DOWN")) ){
					return false;
				}
			return true;
		}
		else return false;
	}
	
	
	@Override
	public void run() {
		try{
			while (!Thread.interrupted()){
				long T = 0;
				DecimalFormat df = new DecimalFormat("#.0");
				try {
					System.out.println("Please input your request");
					start_time = System.currentTimeMillis();
					while (true) {
						line = scan.nextLine().replace(" ", "");
						now = System.currentTimeMillis();
						T = now-start_time;
						
						if (line.equals("end")) {
							endflag = true;
							break;
						}
						data = line.split(";");
						int i = 0;

						for (String test:data){
							if (ele_request(test)){
//								System.out.println("ele test success");
								temp_request = new Request("ER", test, request_count, T);
								request_count++;
								block_rq.Add(temp_request);
							}
							else if (flr_request(test)){
//								System.out.println("floor test success");
								temp_request = new Request("FR", test, request_count, T);
								request_count++;
								block_rq.Add(temp_request);
							}
							else {
								Main.output.put(System.currentTimeMillis() +":INVALID [" + test + ","+ df.format((double)T/1000) + "]");
								continue;
							}
							i++;
							if (request_count == 10){
								String out = "";
								for (;i<data.length;i++){
									out += ";";
									out += data[i];
								}
								Main.output.put(System.currentTimeMillis() +":INVALID [" + out + "," + df.format((double)T/1000) + "]");
								break;
							}
						}
						
						
					}
				}
				catch (Exception e){
					e.printStackTrace();
					System.out.println("FATAL_ERROR: maybe the input is too long, check the virtual space assigned to your JVM");
					Main.output.put(System.currentTimeMillis() +":INVALID [" + line + "," + df.format((double)T/1000) + "]");
					break;
				}
				finally {
					scan.close();
				}
				TimeUnit.SECONDS.sleep(1);
				break;
			}
		} catch (InterruptedException e){
			System.out.println("Exit readin");
		}
	}

}
