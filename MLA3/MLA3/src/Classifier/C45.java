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
	private String delim = ",";
	private String headerRow;
	private int numatt;
	private int linecount=0;
	private int numCases;
	
	private String[][] csvParse = new String[10][400];
	private String[][] Training;
	private String[][] Testing;

	private int trainingCases;
	private int testingCases;
	private ArrayList<Integer> ListShuffle = new ArrayList<Integer>();
	
	private ArrayList<String> classes = new ArrayList<String>();
	private int numClasses;
	private ArrayList<String> attributes = new ArrayList<String>();

	private double systemEnt;
	
	private String[][] minmax;
	
	public C45(String csvLocation) throws IOException
	{
		this.csv = csvLocation;
		DataIn();
		DataSplit();
		systemEnt = systemEntropy();
		
		entropy();

	}
	
	private int count(String val, String att)
	{
		int counter =0;
		int col = attributes.indexOf(att);
			for(int i = 0; i< trainingCases; i++)
			{
				if(Training[col][i].equals(val))
				{
					counter++;
				}
			}
		return counter;
	}
	
	private int count4threshold(int clas, int att, double val)
	{
		int counter =0;
		
			for(int i = 0; i< trainingCases; i++)
			{
				if (Training[numatt][i].equals(classes.get(clas))){
				if(Double.parseDouble(Training[att][i]) <= val)
				{
					counter++;
				}
				}
			}
		return counter;
	}
	
	private double systemEntropy()
	{
		double ent=0.0;
		for(int i =0; i<numClasses;i++)
		{
			double x = count(classes.get(i), attributes.get(numatt));
			ent = ent+ (-x/trainingCases)*(logb2(x/trainingCases));
		}
		
		double max = -logb2(((double)trainingCases/numClasses)/trainingCases);
		ent = ent/max;
		System.out.println(ent);
		return ent;
	}
	
	private void average(int att)
	{
		double sm=0;
		double av=0;
	 for(int clas =0; clas<numClasses; clas++)
	 {
		for(int i=0; i<trainingCases; i++)
		{
			if(Training[numatt][i].equals(classes.get(clas)))
			{
				sm = sm + Double.parseDouble(Training[att][i]);
			}
		}
		String cl= classes.get(clas);
		String s = attributes.get(att);
		int c = count(cl,attributes.get(numatt)); //counting total number of instances of a class, numatt =5(fifth column is type/class)
		av = sm/c;
		System.out.println("For class: " + cl+ " the avereage " + s + " is " +av);
	  }	
	} 
	
	
	private double minmax(int clas, int att)
	{
		minmax = new String[3][numClasses];
		double initmax = 0.0;
		double tempmax =0.0;
		double initmin = 50000.0;
		double tempmin =0.0;
		double max = 0.0;
		double min = 0.0;
		 //for(int clas =0; clas<numClasses; clas++)
		// {
				//initmax = 0.0;
				//initmin = 50000.0;
		int num =count(classes.get(clas), classes.get(numatt));
			for(int i=0; i<num; i++)
			{
			  if(Training[numatt][i].equals(classes.get(clas)))
			  {
				if((tempmax = Double.parseDouble(Training[att][i]))> initmax)
				{
					max = tempmax;
					initmax = max;
				}
				if((tempmin = Double.parseDouble(Training[att][i]))< initmin)
				{
					min= tempmin;
					initmin =min;
				}
			  }
				
			}
			System.out.println("For class: " + classes.get(clas) +" Attribute: " + attributes.get(att) 
					+ " MIN: " + min+ " MAX: " +max);
			return max;
		 
	}

	
	private void entropy()
	{
		for(int i=0; i<numatt; i++)
		{
			double ent =0.0;
			for(int j=0; j<numClasses; j++)
			{
				
				double threshold = minmax(j,i);
				
				
				int numCasesThresh = count4threshold(j,i,threshold);
				double total= count(classes.get(j), attributes.get(numatt));
				if(numCasesThresh!= 0){
				ent = ent+ (-numCasesThresh/total)*(logb2(numCasesThresh/total));
				System.out.println(numCasesThresh);
				}
				
				System.out.println("For class: " + classes.get(j)+" the entropy of instances with " + attributes.get(i)+ " less than " + threshold + " is " + String.valueOf(ent));
			}
			
		}  
	}
	
	private double logb2(double n)
	{
		double x =(Math.log(n)/ Math.log(2));
		return x;
	}
	
	private void infoGain()
	{
		
	}

	
	private void DataIn() throws IOException
	{
		//count the number of cases and attributes in total
		BufferedReader Countreader = 
						new BufferedReader(new FileReader(csv)); // read file
		String temp;

		while((temp = Countreader.readLine()) != null) 			// number of lines= number of cases
		{
			char[] countatt = temp.toCharArray(); 				// create character array of first line
			char c;
			if(linecount ==0)
			{
				headerRow=temp;
			for(int i =0; i<countatt.length; i++)
			{
				c=countatt[i];
				if(c == ',')									// count number of commas in line1 .csv
				{
					numatt++;									// let this be num attributes
				}
			}
			}
			linecount++;										// count number of lines= num cases
		}
		numCases = linecount-1;
		Countreader.close();
		
		System.out.println("Number of Cases: " + numCases);
		
		
		
		//read each line/case into the string array
		BufferedReader reader = 								// new reader
				new BufferedReader(new FileReader(csv));
		String addme = null;
		int j=0;
		while( (addme = reader.readLine()) != null)				// read each line
		{		
			String[] splitline = addme.split(delim);			//	read to each comma
			for (int i =0; i<numatt+1;i++)						// for every attribute +1(target)
			{
				csvParse[i][j] = splitline[i];					// add each value to a table matrix
			}
		  j++;
		}	
		reader.close();
		
		// gather and count the classes
		for(int targetcol=1; targetcol<numCases; targetcol++)	// classes listed in last column line 2
		{
			String target = csvParse[numatt][targetcol];
			if(!classes.contains(target))						// if it hasn't already been added
					{
						classes.add(target);					// add to list
					}
		}
		numClasses = classes.size();
		System.out.println("Number of Classes: " + numClasses); // print
		
		// gather and count attributes
		System.out.println("Number of  Pedictor Attributes: " +numatt);	// number of atts already counted
		String[] headings = headerRow.split(delim);				// collect from header row(0)
		for(int i=0; i< numatt+1; i++)				
		{
			attributes.add(headings[i]);						// add and print each heading
			System.out.println(attributes.get(i));
		}
	
				
				
	}

	//randomly split into training and test data
	public void DataSplit()
	{
		trainingCases = (numCases/3)*2;					// training cases is two thirds
		testingCases = numCases-trainingCases;			// remaining is test
		
		Training = new String[numatt+1][trainingCases];  // create new table matrices 
		Testing = new String[numatt+1][testingCases];	
		
		
		// this is to randomly add from csvParse to Training and Testing matrices
		for(int shuf=1; shuf <numCases+1; shuf++)			// begin at 1 to discount the header row added to csvParse
		{
			ListShuffle.add(shuf);						// list of integers 0 to total number cases
		}
		
		Collections.shuffle(ListShuffle);				// shuffle these ints

		// randomly assign cases as training data
		for(int split = 0; split < trainingCases; split++)
		{
			for(int i = 0; i<numatt+1; i++)
			{
				Training[i][split] = csvParse[i][ListShuffle.get(split)];
			}
		}
		
		// randomly assign cases to testing data
		for(int split = trainingCases; split <numCases; split++)	//+1 header row
		{
			for(int i = 0; i<numatt+1; i++)
			{
				Testing[i][split-trainingCases] = csvParse[i][ListShuffle.get(split)];
			}
		}
		
		// print training data for test purposes
		System.out.println("TRAINING DATA");
		for(int j =0; j<trainingCases; j++)
		{	
				for (int i =0; i<numatt+1;i++)
				{
					System.out.print(Training[i][j]+ delim);
				}
			System.out.print("\n");
		}	
		
		// print testing data for test purposes
		System.out.println("TESTING DATA");
		for(int j =0; j<testingCases; j++)
		{	
				for (int i =0; i<numatt+1;i++)
				{
					System.out.print(Testing[i][j]+ delim);
				}
			System.out.print("\n");
		}	
		
		}
	
	// to Optimize the threshold
	private void threshold(int att)
	{
		/*
		double gap = 0.0;
		double max =0.0;
		for(int clas =0; clas <numClasses; clas++)
		{
			for(int clas1 =1; clas1<numClasses;clas1++)
			{
				if(clas==1)
				{ int place1=0; }
				
			if(Double.parseDouble(minmax[1][clas]) > Double.parseDouble(minmax[2][clas-1]))
			{
				gap=Double.parseDouble(minmax[1][clas]) - Double.parseDouble(minmax[2][clas-1]);
				if(gap>max)
				{
					max=gap;
				}
			}
			if(gap>1)
			{
				System.out.println("threshold:" +gap);
			}
		}
	}*/
		int attsec = att*numClasses;
		for(int clas=attsec; clas<(numClasses+attsec); clas++)
		{
			int i=attsec;
			while(i<numClasses)
			{
				if(i<clas)
				{
					System.out.println(clas + " 3and "+ i);
					if(Double.parseDouble(minmax[1][clas]) > Double.parseDouble(minmax[2][i]))
					{
						
					}
					i++;
				}
				if(clas<i)
				{
					System.out.println(clas +" 2and "+ i);
					if(Double.parseDouble(minmax[1][clas]) > Double.parseDouble(minmax[2][i]) )
					{
						
					
					}
					i++;
				}		
				if(i==clas)
				{
					i++;
					System.out.println(clas +" 1and "+ i);
					if((i!=numClasses) && Double.parseDouble(minmax[1][clas]) > Double.parseDouble(minmax[2][i]))
					{
						
					}
					i++;
				}

				System.out.println(i);
				
			}
		}
	}
}
