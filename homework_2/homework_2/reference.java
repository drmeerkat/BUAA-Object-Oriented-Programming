package homework_2;

import java.util.Scanner;

public class reference {
	
	public static void main(String[] args) { 
        @SuppressWarnings("resource")
		Scanner s = new Scanner(System.in); 
        System.out.println("请输入字符串："); 
        while (true) { 
                String line = s.nextLine(); 
                if (line.equals("exit")) break; 
                System.out.println(">>>" + line); 
        } 
} 

}
