package homework_6;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class Readin {
	private Scanner scanner;
	private String flag;
	private static int temp_trigger, temp_task;
	private static String temp_subdir, temp_parentdir;
	protected static LinkedList<IFT> ifts = new LinkedList<>();
	protected static LinkedList<String> routes = new LinkedList<>();
	
	public Readin(){
		scanner = new Scanner(System.in);
		flag = "";
		temp_trigger = 0;
		temp_task = 0;
		temp_subdir = null;
		temp_parentdir = null;
	}
	
	public int checkTrigger(){
		String line;
		Pattern findNO = Pattern.compile("[1-4]");
		Matcher matchNO;
		while (true){
			System.out.println("Please choose the NO. of the trigger you want to use:");
			System.out.println("1 Renamed          2 Modified");
			System.out.println("3 Path-changed     4 Size-changed");
			line = scanner.nextLine();
			if (line.equals("end")) return 0;
			matchNO = findNO.matcher(line.replace(" ", ""));
			if (matchNO.matches()){
				return Integer.parseInt(matchNO.group(0));
			}
			else{
				System.out.println("Wrong input NO of trigger");
			}
		}
	}
	
	
	public String checkRoute(){
		String line;
		int flag = 0;
		Pattern findroute = Pattern.compile("[a-zA-Z]:/");
		Matcher matchroute;
		StringBuffer fullPath=new StringBuffer();
		while (true){
			System.out.println("Please enter the absolute  route of either a file or a folder you want to monitor:");
			line =scanner.nextLine();
			if (line.equals("end")) return null;
			String [] paths=line.split("/");  
		    for (int i = 0; i < paths.length; i++) {  
		    	fullPath.append(paths[i]).append("/");  
			    File file=new File(fullPath.toString());  
			    	if(!file.exists()){
			            System.out.println("No such path exists");
			            flag = 1;
			            break;
			    	}  
			}
		    if (flag == 0) break;
		}
		return fullPath.toString();
	
	
	
	}
	
	public int checkTask(){
		String line;
		Pattern findNO = Pattern.compile("[1-3]");
		Matcher matchNO;
		while (true){
			System.out.println("Please choose a NO. of the task you want to perform from the followings:");
			System.out.println("1 Record-summary   2 Record-detail     3 Recover");
			line = scanner.nextLine();
			if (line.equals("end")) return 0;
			matchNO = findNO.matcher(line.replace(" ", ""));
			if (matchNO.matches()){
				return Integer.parseInt(matchNO.group(0));
			}
			else{
				System.out.println("Wrong input NO of tasks");
			}
		}
		
	}
	
	public static void main(String[] args) {
		Readin readin = new Readin();
		int count = 0;
		IFT temp_ift;
		Snapshot snapshot = new Snapshot();
		
		int num = 0;
//		怎么记录父目录，确定工作区？
		while (!readin.flag.equals("end")){
			System.out.println("You can enter 'end' to start the IFTTT system whenever you want");
//			检测输入的数字如果不合适就继续输入
			temp_trigger = readin.checkTrigger();
			if (temp_trigger == 0){
				if (count>4){
					readin.flag = "end";
				}
				else{
					System.out.println("The total number of different monitored routes is less than 5, please keep on entering");
				}
				continue;
			}
//			检测输入的路径是否合法，存在性，格式合法乎？
			temp_subdir = readin.checkRoute();
			if (temp_subdir.equals(null)){
				if (count>4){
					readin.flag = "end";
				}
				else{
					System.out.println("The total number of different monitored routes is less than 5, please keep on entering");
				}
				continue;
			}
//			检测数字合法性，否则继续输入
			temp_task = readin.checkTask();
			if (temp_task == 0){
				if (count>4){
					readin.flag = "end";
				}
				else{
					System.out.println("The total number of different monitored routes is less than 5, please keep on entering");
				}
				continue;
			}
//			分配给对应触发器，是否重复了?是否已经八个监控路径了？是否少于五个?先检测任务是否不同，然后检测路径
			File file = new File(temp_subdir);
			if (temp_task == 3&&(temp_trigger == 2||temp_task == 4||file.isDirectory())){
				System.out.println("Only files with trigger rename or path_changed can be handled");
				continue;
			}
			temp_ift = new IFT(temp_trigger, temp_subdir, temp_task, temp_parentdir, num);
			if (!ifts.contains(temp_ift)) {
				ifts.add(temp_ift);
				num += 1;
			}
			else{
				System.out.println("Same IFT, your input will be sipped");
			}
			if (!routes.contains(temp_subdir) && count<9) {
				routes.add(temp_subdir);
				count += 1;
			}
			if (count == 8) readin.flag = "end";
			
		}
		System.out.println("Finished reading in IFTs, monitors start to run. You have "+ifts.size()+" IFTs");
//		输出已经输入的IFT列个IF THEN表
		IFT temp;
		int NO = 0;
		Iterator<IFT> it = ifts.iterator();
		while (it.hasNext()){
			temp = it.next();
			System.out.println(temp);
		}
//		readin 可以给出的接口有任务列表ifts和监控地址列表routes，
//		任务列表需要分配给各个触发器线程，分配的时候带上序号以方便和root数组对应
		while (it.hasNext()){
			temp = it.next();
			if (temp.getTrigger().equals("1")){
				snapshot.rename.addTaskNO(NO);
			}
			else if (temp.getTrigger().equals("2")){
				snapshot.modify.addTaskNO(NO);
			}
			else if (temp.getTrigger().equals("3")){
				snapshot.path_change.addTaskNO(NO);
			}
			else if (temp.getTrigger().equals("4")){
				snapshot.size_change.addTaskNO(NO);
			}
			NO += 1;
		}
		
//		开始监控
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(snapshot);
		exec.execute(snapshot.rename);
		exec.execute(snapshot.modify);
		exec.execute(snapshot.size_change);
		exec.execute(snapshot.path_change);
		
		
		Security_File security_File;
		/*
		 * 测试线程请在这里填写并启动
		 * 使用Security_File类即可,部分未提供功能可自行添加至该类中比如读写文件等，时间实在是来不及了，可能bug会很多，同学手下留情。。。拜托你了！
		 * File file = new file("route");
		 * security_file = new Security_File(file);
		 * security.xxxxxxxx
		 */
		
	}
	
	
}