package homework_7;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Readin implements Runnable{
	private String line;
	private File file;
	protected int[][] map;
	private int line_num;
	private final static int map_size = 80;
//	记得把地图大小改回来80
	private static long start_time;
	protected static boolean endflag;
	protected static int request_count;
	protected BlockedRequestQueue block_rq;
	
	public Readin(BlockedRequestQueue block_rq){
		line_num = 0;
		map = new int[map_size][map_size];
		this.block_rq = block_rq;
		readmap("map.txt");
	}
	
//	public static void main(String[] args) {
//		
//		
//		test.readrequest();
//		Findpath find_test = new Findpath(test.map);
//		LinkedList<Point> route_test = find_test.search(new Point(0, 4), new Point(1, 0));
//		Iterator<Point> iterator = route_test.iterator();
//		while (iterator.hasNext()){
//			System.out.println(iterator.next());
//		}
//	}
	
	public static int getMapSize() {
		return map_size;
	}
	
	public boolean readmap(String map_route){
		try {
			int i;
			String [] temp_data;
            String encoding="ASCII";
            file = new File(map_route);
            Pattern find_num = Pattern.compile("[0-3]");
            Matcher match_num;
            if(file.isFile() && file.exists()){
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file),encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                while((line = bufferedReader.readLine()) != null){
//                	System.out.println(line+"/");
//                	System.out.println(line.replaceAll("[\\t\\v ]+", " ")+"/");
                	temp_data = line.replaceAll("[\\t\\v ]+", " ").split(" ");
                	if (temp_data.length != map_size) break;
                	for (i = 0;i<map_size;i++){
                		match_num = find_num.matcher(temp_data[i]);
                		if (!match_num.matches()) 
                			break;
                		map[line_num][i] = Integer.parseInt(match_num.group(0));
                	}
                	if (i != map_size) 
                		break;
                	line_num++;
                }
                read.close();
                if (line_num != map_size){
                	System.out.println(line_num+" Invalid map, please check again");
                	return false;
                }
                return true;
            }
            else {
        		System.out.println(file.getPath()+" is not a file or doesn't exist, please check again");
        		return false;
            }
        } catch (Exception e) {
            System.out.println("Invalid map, maybe you're not using ASCII");
            e.printStackTrace();
            return false;
        }
		
	}
	
	public static boolean cr_request(String line){
		Pattern find_request = Pattern.compile("\\[CR,\\((\\d\\d*),(\\d\\d*)\\),\\((\\d\\d*),(\\d\\d*)\\)\\]");
		Matcher match_request = find_request.matcher(line);
		
		if (match_request.matches()) {
			for (int i = 1;i<5;i++){
				if (Integer.parseInt(match_request.group(i)) > 79 
						|| Integer.parseInt(match_request.group(i)) < 0){
					return false;
				}
			}
			return true;
		}
			
		else return false;
	}

	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);
		long T = 0;
		long now = 0;
		String data[];
		Request temp_request;
		LinkedList<Request> templist = new LinkedList<>();
		System.out.println("Please input your request");
		start_time = System.currentTimeMillis();
		try {
			while (!Thread.interrupted()){
				line = scanner.nextLine().replace(" ", "");
				now = System.currentTimeMillis();
				T = now-start_time;
//				System.out.println(T);
				T = (T/100 < (T + 50)/100)? (T + 50)/100*100:T/100*100;
//				System.out.println(T);
				if (line.equals("end")) {
					endflag = true;
					break;
				}
				data = line.split(";");
				int i = 0;
				for (String test:data){
					if (cr_request(test)){
						temp_request = new Request(request_count,test,T);
//						for (i = 0){
//							if (){
//								templist.add(temp_request);
//								request_count++;
//							}
//							else{
//								System.out.println("Same request: "+temp_request);
//								continue;
//							}
//						}
						Iterator<Request> it = templist.iterator();
						boolean same = false;
						while (it.hasNext()){
							Request test_re = it.next();
							if (test_re.isSame(temp_request)){
								System.out.println("Same request: "+temp_request.basicinfo());
								same = true;
								break;
							}
						}
						if (!same){
							request_count++;
							templist.add(temp_request);
						}
						
//						System.out.println(temp_request);
					}
					else {
//						Main.output.put(System.currentTimeMillis() +":INVALID [" + test + ","+ df.format((double)T/1000) + "]");
						System.out.println("Invalid input");
						continue;
					}
					i++;
					if (i == 10){
						String out = "";
						for (;i<data.length;i++){
							out += ";";
							out += data[i];
						}
						System.out.println("Too many: "+ out);
//						Main.output.put(System.currentTimeMillis() +":INVALID [" + out + "," + df.format((double)T/1000) + "]");
						block_rq.addAll(templist);
						templist.clear();
						break;
					}
				}
				block_rq.addAll(templist);
				templist.clear();
				
				TimeUnit.MILLISECONDS.sleep(1);
			}
		}
		catch (InterruptedException e){
			System.out.println("Taxi scheduler system interrupted.");
		}
		catch (Exception e){
			e.printStackTrace();
			System.out.println("Invalid input: maybe the input is too long, check the virtual space assigned to your JVM");
		}
		finally {
			scanner.close();
		}
	}
}
