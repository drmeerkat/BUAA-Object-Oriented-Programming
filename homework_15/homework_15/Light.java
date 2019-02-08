package homework_15;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*Overview
 * 读取红绿灯地图，初步检查红绿灯地图合法性（不负责检查是否符合岔路口和30%数量条件，请测试者保证）
 * 初始化信号灯系统
 * 模拟红绿灯运行，提供接口查询红绿灯状态
 * 抽象函数：映射红绿灯地图中记录的0/1数据到红绿灯实例化对象并模拟红绿灯灯组的运行
 */

public class Light implements Runnable{
	protected int [][] lightmap;
	protected static int time_lapse;
	protected TaxiGUI gui;
	protected int color;
	protected static int time_left;
	
	public boolean repOK(){
		/*
		 * @REQUIRES:	None;
		 * @MODIFIES:	None;
		 * @EFFECTS:	Checks if "this" is legal and the lapse time is in range.
		 */
		if (lightmap == null||time_lapse>=200||time_lapse<=500||gui == null
				||time_left<0)
			return false;
		return true;
	}
	
	public Light(String path, TaxiGUI gui){
		/*
		 * @REQUIRES:	String path != null and TaxiGui gui != null;
		 * @MODIFIES:	gui
		 * 				lightmap
		 * 				time_lapse
		 * 				color
		 * @EFFECTS:	初始化信号灯系统
		 */
		this.gui = gui;
		Random random = new Random();
		lightmap = new int[80][80];
		time_lapse = random.nextInt(301)+200;//200-500
		color = 1;
		
		File file = new File(path);
		int line_num = -1, i;
		String [] temp_data;
        String encoding="ASCII", line;
        Pattern find_num = Pattern.compile("[0-1]");
        Matcher match_num;

        
		try {
            if(file.isFile() && file.exists()){
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file),encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                while((line = bufferedReader.readLine()) != null){
                	line_num++;
                	temp_data = line.replaceAll("[\\t\\v ]+", " ").split(" ");
                	if (temp_data.length != Readin.getMapSize()) break;
                	for (i = 0;i < Readin.getMapSize();i++){
                		match_num = find_num.matcher(temp_data[i]);
                		if (!match_num.matches()) 
                			break;
                		lightmap[line_num][i] = Integer.parseInt(match_num.group(0));
                		gui.SetLightStatus(new Point(line_num, i), 0);
                		if (lightmap[line_num][i] != 0){
//                			lightmap[line_num][i] = time_lapse;
                			gui.SetLightStatus(new Point(line_num, i), random.nextInt(2));
                		}  
                	}
                	if (i != Readin.getMapSize() || line_num == Readin.getMapSize()-1) 
                		break;
                }
                read.close();
                if (line_num != Readin.getMapSize() - 1){
                	System.out.println("Invalid map, please check again");
                }
            }
            else {
        		System.out.println(file.getPath()+" is not a file or doesn't exist, please check again");
            }
        } catch (Exception e) {
            System.out.println("Invalid map, maybe you're not using ASCII");
        }
	
	}
	
	public void run() {
		/*
		 * @REQUIRES:	None;
		 * @MODIFIES:	time_left
		 * 				gui
		 * @EFFECTS:	根据时间片切换信号灯状态
		 */
		try{
			setTimeleft(time_lapse);
			while (!Thread.interrupted()){
				TimeUnit.MILLISECONDS.sleep(1);
				int temp = getTimeleft();
				setTimeleft(temp-1);
				int i,j;
				for (i = 0;i<80;i++){
					for (j = 0;j<80;j++){
						if (lightmap[i][j] != 0){
							synchronized (this){
								if (time_left == 0){
									color = (guigv.lightmap[i][j] == 1)? 2:1;
									gui.SetLightStatus(new Point(i, j), color);
								}
							}	
						}
					}
				}
				if (time_left == 0) setTimeleft(time_lapse);
			}
		}
		catch (InterruptedException e){
		}
		catch (Exception e) {
		}
	}
	
	public static synchronized int getTimeleft(){
		/*
		 * @REQUIRES:	None;
		 * @MODIFIES:	None;
		 * @EFFECTS:	返回当前信号灯状态的剩余维持时间
		 */
		return time_left;
	} 
	
	private synchronized void setTimeleft(int left){
		/*
		 * @REQUIRES:	\all int left; -time_lapse <= left <= time_lapse
		 * @MODIFIES:	None;
		 * @EFFECTS:	设置新的信号灯状态维持时间
		 */
		time_left = left;
	}
	
	
	
}
