package Classifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


//this implementation of C4.5 is dynamically modelled to work with 
//any data set with four attributes and three classes
//
public class C45 {
	

	private String csv;
	private char delimc =  ',';
	private String delim = ",";
	private int numatt;
	private String[][] csvParse = new String[10][400];
	private String[][] Training;
	private String[][] Testing;
	private int linecount;
	private int trainingCases;
	private int testingCases;
	private ArrayList<Integer> ListShuffle = new ArrayList<Integer>();
	private int longearCount;
	private int snowyCount;
	private int barnCount;

	
	public C45(String csvLocation) throws IOException
	{
		this.csv = csvLocation;
		DataIn();
		DataSplit();
	}

	
	private void DataIn() throws IOException
	{
		//count the number of cases and attributes in total
		BufferedReader Countreader = new BufferedReader(new FileReader(csv)); //read file
		String temp;
		int linenum=0;
		
		while((temp = Countreader.readLine()) != null) //number of lines= number of cases-1
		{
			char[] countatt = temp.toCharArray();
			char c;
			if(linecount ==0)
			{
			for(int i =0; i<countatt.length; i++)
			{
				c=countatt[i];
				if(c == ',')
				{
					numatt++;
				}
			}
			}
			linecount++;
			
		}
		System.out.println(numatt);
		System.out.println(linecount);
		
		//read each line into the string array
		BufferedReader reader = new BufferedReader(new FileReader(csv));
		String addme = null;
		int j=0;
					while( (addme = reader.readLine()) != null)
					{
						
						String[] splitline = addme.split(delim);
						for (int i =0; i<5;i++)
						{
							csvParse[i][j] = splitline[i];
						}
						j++;
					}	
				
					
					/* print test for all 135 cases
				for(int jj =0; jj<linecount; jj++)
				{	
						for (int i =0; i<5;i++)
						{
							System.out.print(csvParse[i][jj]+ delim);
							
						}
						System.out.print("\n");
				}*/
				
				
	}


	public void DataSplit()
	{
		trainingCases = (linecount/3)*2;
		testingCases = linecount-trainingCases;
		
		Training = new String[5][trainingCases];
		Testing = new String[5][testingCases];
		
		for(int shuf=0; shuf <linecount; shuf++)
		{
			ListShuffle.add(shuf);
		}
		//to split the cases randomly
		Collections.shuffle(ListShuffle);

		//assign cases as training data
		for(int split = 0; split < trainingCases; split++)
		{
			for(int i = 0; i<5; i++)
			{
				Training[i][split] = csvParse[i][ListShuffle.get(split)];
			}
		}
		
		//assign cases to testing data
		for(int split = trainingCases; split <linecount; split++)
		{
			for(int i = 0; i<5; i++)
			{
				Testing[i][split-trainingCases] = csvParse[i][ListShuffle.get(split)];
			}
		}
		
		//print training and testing data
		System.out.println("TRAINING DATA");
		
		for(int j =0; j<trainingCases; j++)
		{	
				for (int i =0; i<5;i++)
				{
					System.out.print(Training[i][j]+ delim);
				}
			System.out.print("\n");
		}	
		
		System.out.println("TESTING DATA");
		
		for(int j =0; j<testingCases; j++)
		{	
				for (int i =0; i<5;i++)
				{
					System.out.print(Testing[i][j]+ delim);
				}
			System.out.print("\n");
		}	
		
		}

}
