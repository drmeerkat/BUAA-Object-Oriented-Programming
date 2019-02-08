package homework_11;

import java.util.ListIterator;


/*Overview
 * 测试线程的相关函数都在这个类里进行填写和实现。
 */

public class EditYourCode{
    protected TestOperation testOperation;
    private static final int pink_count = 30;
    
    public EditYourCode(TaxiSystem taxisys) {
    	/*@REQUIRES: (\all TaxiSystem; taxisys != null)
    	 *@MODIFIES: testOperation
    	 *@EFFECTS: initialize the testOperation
    	 */
    	testOperation = new TestOperation(taxisys);
    }
    
	public boolean repOK(){
		/*
		 * @REQUIRES:	None;
		 * @MODIFIES:	None;
		 * @EFFECTS:	Checks if "this" is legal.
		 */
		if (testOperation == null)
			return false;
		return true;
	}

    public void test() {
        /*----------------------------------------------------------
         *                        Code here
         *----------------------------------------------------------
         */
//		For example:
//    	testOperation.checkState("waiting", 10000);
//    	testOperation.checkTaxi(1, 200);
//    	testOperation.checkTime();
//    	testOperation.feedRequest("[CR,(1,2),(3,4)]", 300);
    	
//    	if (testOperation.checkTime() == 3000){
//  			ListIterator<String> test = testOperation.getIterator(0);
//  			while(test.hasNext()){
//  				System.out.println(test.next());
//  			}
//  			while(test.hasPrevious()){
//  				System.out.println(test.previous());
//  			}
//  			
//  	    }
 
    	
//    	最终会在控制台输入end结束整个程序之后在工作目录下输出taxi log 和 test log 
    }
    
    public Taxi[] initTaxi(TaxiGUI gui,Findpath gps){
		/*@REQUIRES: None
		 *@MODIFIES: this.testSys
		 *			 gui
		 *@EFFECTS: initialize all the taxis
		 *			for (int i = 0;i<TaxiSystem.taxinum;i++){
		 * 				if (i == 你希望的新型出租车编号 && 粉车总数没超过30){
		 * 					taxis[i] = new PinkTaxi(i+1, gps);
		 *					gui.SetTaxiType(i, 1);
		 *					pinknum++;
		 *				}
		 *				else{
		 *					taxis[i] = new Taxi(i+1, TaxiSystem.gps); //id 1-100
		 *					gui.SetTaxiType(i, 0);
		 *				}
		 *				gui.SetTaxiStatus(i, taxis[i].getGuiPoint(), 2);
		 *			}
		 */
//    	以下为示例代码，按照指导书的说法，要提供空方法供填写，你可以选择删了再写一遍23333
//    	自定义的话实现比较自由，选择你想要的编号填写在if里面就好了，或者你可以弄一个数组，只要i存在于数组中就声明为特殊车之类
    	int pinknum=0;
    	Taxi [] taxis = new Taxi[TaxiSystem.taxinum];
    	
		for (int i = 0;i<TaxiSystem.taxinum;i++){
			if ((i == 0||i == 20||i > 80) && pinknum < pink_count){//在这里自定义
				taxis[i] = new PinkTaxi(i+1, gps);   //此处使用参数中传入的固定GPS
				gui.SetTaxiType(i, 1);
				pinknum++;
			}
			else{
				taxis[i] = new Taxi(i+1, TaxiSystem.gps); //id 1-100 注意此处需要使用出租车调度系统的统一GPS
				gui.SetTaxiType(i, 0);
			}
			gui.SetTaxiStatus(i, taxis[i].getGuiPoint(), 2);
		}
		
		
		
		return taxis;
    }
}
