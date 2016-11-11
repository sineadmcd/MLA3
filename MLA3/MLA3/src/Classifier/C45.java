package Classifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


//this implementation of C4.5 is dynamically modelled to work with 
//any data set 
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

	private String[][] workingSet;
	
	private int trainingCases;
	private int workingCases;
	private int testingCases;
	private ArrayList<Integer> ListShuffle = new ArrayList<Integer>();
	
	private ArrayList<String> classes = new ArrayList<String>();
	private int numClasses;
	private ArrayList<String> attributes = new ArrayList<String>();

	private double maxEnt;
	private double systemEnt;
	
	private boolean t_flag=false;
	double tolerance = 0.0;
	private double tempthresh =0.0;
	private double[][] thresholds = new double[2][100];
	private int tcount;
	
	public C45(String csvLocation) throws IOException
	{
		this.csv = csvLocation;
		DataIn();
		DataSplit();
		systemEnt = systemEntropy();
		
	//while(workingSet != null)
	//{
		for(int i =0; i<numatt; i++)
		{
			Optimizer(i);
		}
	//}

	}
	
	
	//method counts the number of instances of a certain class within the working set
	// e.g num BarnOwls = 31
	private int count(int clas)
	{
		int counter =0;
		int att = numatt;
			for(int i = 0; i< trainingCases; i++)
			{
				if(workingSet[att][i].equals(classes.get(clas)))
				{
					counter++;
				}
			}
		return counter;
	}
	
	
	// given a threshold (val) count the number of cases 
	// of a certain class that is less than the threshold
	private int count4threshold(int clas, int att, double val)
	{
		int counter =0;
			for(int i = 0; i< workingCases; i++)
			{
				if (workingSet[numatt][i].equals(classes.get(clas))){
				if(Double.parseDouble(workingSet[att][i]) <= val)
				{
					counter++;
				}
				}
			}
		return counter;
	}
	
	private int countAbovethreshold(int clas, int att, double val)
	{
		int counter =0;
			for(int i = 0; i< workingCases; i++)
			{
				if (workingSet[numatt][i].equals(classes.get(clas))){
				if(Double.parseDouble(workingSet[att][i]) > val)
				{
					counter++;
				}
				}
			}
		return counter;
	}
	
	
	
	// at a certain node calculate the 
	// total entropy of the remaining cases
	private double systemEntropy()
	{
		double ent=0.0;
		for(int i =0; i<numClasses;i++)
		{
			double x = count(i);
			ent = ent+ (-x/workingCases)*(logb2(x/workingCases));
		}
		
		maxEnt = -logb2(((double)workingCases/numClasses)/workingCases);
		ent = ent/maxEnt;
		//System.out.println("System Entropy: " +String.format( "%.4f", ent));
		return ent;
	}
	
	
	
	// calculates the max value of each 
	// attribute for a given class
	private double max(int clas, int att)
	{
		double initmax = 0.0;
		double tempmax =0.0;
		double max = 0.0;
		int num =count(clas);
			for(int i=0; i<num; i++)
			{
			  if(workingSet[numatt][i].equals(classes.get(clas)))
			  {
				if((tempmax = Double.parseDouble(workingSet[att][i]))> initmax)
				{
					max = tempmax;
					initmax = max;
				}
				
			  }
				
			}
			return max;
	}
	
	// finds minimum infogain at a certain node
	private void Optimizer(int att)
	{
		
		t_flag = false;
		double entropyt = 0.0;
		double entropyf =0.0;
		double entf =0.0;
		double infogain =0.0;
		double tempinfo=0.0;
		double bestinfo =0.0;
		double bestthresh =0.0;
		double t = 0.0;
		int timeout=0;

		for(int i=0; i<numClasses; i++) // check if optimal info can be found at threshold = max val of att for some class i
		{	
			t=max(i,att);
			entropyt = entropy(t, att);
			entropyf = entropyi(t,att);
			infogain =infoGain(att, t, entropyt, entropyf);
			if(infogain> bestinfo)
			{
				bestinfo = infogain;
			}
			
		}
		tempinfo= bestinfo;
			while(bestinfo< 1 && timeout<200)
			{
				t= threshold(att);
				entropyf = entropyi(t,att);
				entropyt = entropy(t, att);
				bestinfo =infoGain(att, t, entropyt, entropyf);
				if(bestinfo> tempinfo)
				{
					tempinfo= bestinfo;
					bestthresh = t;
				}
				timeout++;
			}
			
			System.out.println("*********best info Gain is " + String.format( "%.4f",tempinfo) 
					+ " for " + attributes.get(att));
			System.out.println(" at threshold " + bestthresh
					+ " for " );
	}
	

	// Given a threshold and an attribute this method
	// gets the total number of instances below the given threshold
	// and then finds the number of cases of a certain class below a threshold
	// and hence calculates the entropy
	// -(LEbelowThresh/totalBelowThresh)*logb2(LEbelowThresh/totalBelowThresh)-(BObelowTresh/totalBelowThresh)*logb2(BObelowThresh/totalBelowThresh)-(SObelowThresh/totalBelowThresh)*logb2(SObelowThresh/totalBelowThresh)-
	private double entropy(double threshold, int att)
	{
		double ent =0.0;
		int i = att;
			for(int j=0; j<numClasses; j++)
			{	
			  double total =0.0;
				for(int tot=0; tot<numClasses; tot++)
				{
					total+= count4threshold(tot,i,threshold); //all instances below threshold
				}
				int numCasesThresh = count4threshold(j,i,threshold);
				//System.out.println("class: " + classes.get(j) + " below thresh: " + numCasesThresh );
				//System.out.println("total below thresh: " + total);
				if(numCasesThresh!= 0)
				{
					
					ent = ent+ (-numCasesThresh/total)*(logb2(numCasesThresh/total));
				}
				
			}
				ent = ent/maxEnt;
				//System.out.println("entropy t : " + ent + " @ thresh " + threshold );
				return ent;
	}
	
	private double entropyi(double threshold, int att)
	{
		double ent =0.0;
		int i = att;
			for(int j=0; j<numClasses; j++)
			{	
			  double total =0.0;
				for(int tot=0; tot<numClasses; tot++)
				{
					total+= countAbovethreshold(tot,i,threshold);
				}
				
				int numCasesThresh = countAbovethreshold(j,i,threshold);
				//System.out.println("class: " + classes.get(j) + " above thresh: " + numCasesThresh );
				//System.out.println("total above thresh: " + total);
				if(numCasesThresh> 0)
				{
					ent = ent+ (-numCasesThresh/total)*(logb2(numCasesThresh/total));
				}
				
			}
			
				ent = ent/maxEnt;
				//System.out.println("entropy f : " + ent + " @ thresh " + threshold);
				return ent;
	}
	
	// to Optimize the threshold
	private double threshold(int att)
	{
		double initmax = 0.0;
		double threshmax=0.0;
		double threshold=0.0;
		if(!t_flag)
		{
		for(int i=0; i<numClasses; i++)
		{
			threshold = max(i,att);
			if( threshold>initmax)
			{
				threshmax = threshold; 	// find absolute max value for given attribute
				initmax=threshold;
			}													
		}
		tempthresh= threshmax;
		tolerance = threshmax/200;	//reduction of 0.5 percent	
		}
		t_flag = true;
		// keep reducing threshold until Optimizer function stops it
		tempthresh = tempthresh - tolerance;
		return tempthresh;
	}
	
	private double logb2(double n)
	{
		double x =(Math.log(n)/ Math.log(2));
		return x;
	}
	
	private double infoGain(int att, double threshold, double entropyt, double entropyf)
	{
		int numCasesLess=0;
		int numCasesG8r =0;
		for( int clas =0; clas<numClasses; clas++)
		{
		numCasesLess += count4threshold(clas, att, threshold);
		numCasesG8r += countAbovethreshold(clas, att, threshold);
		}
		
		double ent =systemEntropy();
		double IG;
		IG = (ent-(((double)numCasesLess/workingCases)*entropyt)-(((double)numCasesG8r/workingCases)*entropyf));
		//System.out.println("threshold: " + threshold + " numCases less: " + numCasesLess +" working cases: " + workingCases +"numCases above:" + numCasesG8r +" entropy t: " + entropyt +" entropy f: " + entropyf +" Information Gain: " + String.format( "%.4f",IG));
		return IG;
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
		
		workingSet = Training;							//working set changes at each node
		workingCases = trainingCases;
		
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
		int c = count(clas); //counting total number of instances of a class, numatt =5(fifth column is type/class)
		av = sm/c;
		System.out.println("For class: " + cl+ " the avereage " + s + " is " +av);
	  }	
	} 

}
