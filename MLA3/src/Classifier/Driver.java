package Classifier;

import java.io.IOException;


public class Driver {

	
	public static void main(String[] args)
	{
		System.out.println("entered");
		
		String csv = "C:/Users/user/Desktop/owls15.csv";
		try {
			C45 Testing = new C45(csv);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
