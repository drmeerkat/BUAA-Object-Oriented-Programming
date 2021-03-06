 package homework_14;

import java.text.DecimalFormat;


public class Elevator implements Elerun_interface{	
	/* Overview
	 * 电梯类，保存了当前电梯内部运行时间和电梯按钮状态，所在楼层，运动状态，按钮对应请求
	 * 可供调度器查询,可输出字符串格式的电梯当前状态
	 * 抽象函数：
	 * AF(c)=(time,current_fr,state,button[],re_button[]) where time is c.runningtime,current_fr is c.floor,
	 * state is c.state, button[] is whether the buttons are pressed, re_button records 
	 * the re relates to the pressed button
	 */
	protected double time;
	protected int current_fr;
	protected String state;
	protected int [] button;
	protected Request [] re_button;
	
	
	public Elevator() {
		/*
		 * @REQUIRES:	None
		 * @MODIFIES:	time
		 * 				current_fr
		 * 				state
		 * 				button
		 * 				re_button
		 * 				
		 * @EFFECTS:	初始化电梯，设置好内部的的各种初始状态
		 */ 
		time = 0;
		current_fr = 1;
		state = "STILL";
		button = new int[] {0,0,0,0,0,0,0,0,0,0};
		re_button = new Request[10];
	}
	 
	public boolean repOK(){
		/*
		 * @REQUIRES:	None;
		 * @MODIFIES:	None;
		 * @EFFECTS:	Checks if "this" is legal.
		 */
		if (state == null||re_button==null||button==null||current_fr>10||current_fr<1)
			return false;
		return true;
	}

	public String toString(){
		/*
		 * @REQUIRES:	None
		 * @MODIFIES:	None
		 * @EFFECTS:	返回当前电梯状态的详细信息，同时时间计数保留一位小数
		 */
		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		return "(" + current_fr + "," + state + "," + String.valueOf(decimalFormat.format(time))+")";
	} 
	
	
	public void setCurrentfr(String dir){
		/*
		 * @REQUIRES:	/all String dir;dir == UP|DOWN
		 * @MODIFIES:	current_fr
		 * @EFFECTS:	如果方向是上楼则楼层加一，如果是下楼则楼层减一，
		 * 				如果输入方向不是规定的字符串则报错
		 */
		if (dir == "UP"){
			current_fr += 1;
		}
		
		else if (dir == "DOWN"){
			current_fr -= 1;
		}
		
		else{
			System.out.println("FATAL ERROR:state still cannot refresh the fr");
		}
	}
	
	public boolean pressButton(Request button_request){
		/*
		 * @REQUIRES:	/all Request button_request;button_request!=null
		 * @MODIFIES:   button
		 * 				re_button
		 * @EFFECTS:	模拟电梯按钮被按下。如果输入不合法则输出报错信息
		 */
		if (button_request!=null){
			int k = button_request.getFlr();
			button[k-1] = 1;
			re_button[k-1] = button_request;
			return true;
		}
		else{
			System.out.println("Wrong input!");
			return false;
		}
	}
	

	
	
	public boolean unlock(int k){
		/*
		 * @REQUIRES:	/all int k;1<=k<=10
		 * @MODIFIES:	button
		 * 				re_button
		 * @EFFECTS:	将指定楼层电梯按钮恢复到没有按下的状态并清空这个按钮对应的请求信息。
		 * 				如果输入不合法或对应的按钮并没有被按下则输出报错信息
		 */ 
		if (k<=10 && k>=1 && button[k-1] == 1){
			button[k-1] = 0;
			re_button[k-1] = null;
			return true;
		}
			
		else{
			System.out.println("ERROR3: the button isn't lightend");
			return false;
		}
	}
}

