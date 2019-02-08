package homework_9;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Readin implements Runnable{
	private String line;
	private File file;
	protected static int[][] map;
	private int line_num;
	private final static int map_size = 80;
	private static long start_time;
	protected static boolean endflag;
	protected static AtomicInteger request_count;
	protected BlockedRequestQueue block_rq;
	protected static LinkedBlockingQueue<String> block_setroad;
	
	public Readin(BlockedRequestQueue block_rq){
	/*@REQUIRES:None
	 *@MODIFIES:line_num
	 *			map
	 *			this.block_rq
	 *			block_setroad
	 *			request_count
	 *@EFFECTS:初始化各个类内属性和变量
	 */
		line_num = 0;
		map = new int[map_size][map_size];
		this.block_rq = block_rq;
		readmap("map.txt");
		block_setroad = new LinkedBlockingQueue<>();
		request_count = new AtomicInteger(0);
	}
		
	public static int getMapSize() {
		/*@REQUIRES:None
		 *@MODIFIES:None
		 *@EFFECTS: 返回地图的边长（此处为80）
		 */
		return map_size;
	}
	
	public boolean readmap(String map_route){
		/*@REQUIRES:None
		 *@MODIFIES:map
		 *@EFFECTS: 将输入的路径指向的地图文件都去进map数组中，如果地图合法则返回true，否则返回false
		 */
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
		/*@REQUIRES:None
		 *@MODIFIES:None
		 *@EFFECTS: 判断输入的line字符串是否符合CR请求的正则表达式。符合的话返回true，否则返回false
		 */
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
		/*@REQUIRES:None
		 *@MODIFIES:map
		 *			line
		 *			request_num
		 *			start_time
		 *			block_setroad
		 *			endflag
		 *			block_rq
		 *@EFFECTS:	读取控制台输入并处理输入的每一行请求。将请求分为CR类和修路类请求分别进行处理。
		 *		   	记录合法的CR和修路请求并将他们分别插入阻塞队列等待分配调用。
		 *			记录总请求个数。
		 *			监控控制台输入，如果输入了end则在等待剩余请求处理完之后关闭整个程序。
		 */
		Scanner scanner = new Scanner(System.in);
		long T = 0;
		long now = 0;
		String data[];
		Request temp_request;
		LinkedList<Request> templist = new LinkedList<>();
		System.out.println("Please input your request");
		start_time = System.currentTimeMillis();
		Pattern find_roadup = Pattern.compile("\\[\\((\\d\\d?),(\\d\\d?)\\),\\((\\d\\d?),(\\d\\d?)\\),([01])\\]");
		Matcher match_roadup;
		try {
			while (!Thread.interrupted()){
				line = scanner.nextLine().replace(" ", "");
				now = System.currentTimeMillis();
				T = now-start_time;
				T = (T/100 < (T + 50)/100)? (T + 50)/100*100:T/100*100;
				if (line.equals("end")) {
					endflag = true;
					break;
				}
				data = line.split(";");
				int num = 0;
				boolean roadup = false;
				for (String temp:data){
					match_roadup = find_roadup.matcher(temp);
					if (match_roadup.matches() && num<5){
						num++;
						block_setroad.offer(temp);
						roadup = true;
					}
					else if (num >= 5){
						System.out.println("Too many road change requests!");
						break;
					}
					else{
						System.out.println("Invalid road change: "+temp);
						continue;
					}
				}
				if (roadup){
					continue;
				}
				
				
				int i = 0;
				for (String test:data){
					if (cr_request(test)){
						temp_request = new Request(request_count,test,T);
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
							request_count.incrementAndGet();
							templist.add(temp_request);
						}
						
					}
					else {
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
//			e.printStackTrace();
			System.out.println("Invalid input: maybe the input is too long, check the virtual space assigned to your JVM");
		}
		finally {
			scanner.close();
		}
	}
}
