package homework_10;

/*Overview
 * 测试线程的相关函数都在这个类里进行填写和实现。
 */

public class EditYourCode{
    protected TestOperation testOperation;
    
    public EditYourCode(TaxiSystem taxisys) {
    	/*@REQUIRES: (\all TaxiSystem; taxisys != null)
    	 *@MODIFIES: testOperation
    	 *@EFFECTS: initialize the testOperation
    	 *
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
//    	testOperation.checkTaxi(0, 200);
//    	testOperation.checkTime();
//    	testOperation.feedRequest("[CR,(1,2),(3,4)]", 300);
    	
//    	最终会在控制台输入end结束整个程序之后在工作目录下输出taxi log 和 test log 
    }
}
