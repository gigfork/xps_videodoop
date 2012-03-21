package job;
// modified order crossover(OX) operator

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapFile;

public class crossover {		

	
	//best routes preserving crossover
	public static String[] doCrossover(String can1,String can2) throws IOException 
	{
		ArrayList<ArrayList<Integer>> cand1 = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> cand2 = new ArrayList<ArrayList<Integer>>();
		StringTokenizer token1 = new StringTokenizer(can1,"#");
		StringTokenizer token2;
		String tmp;
		ArrayList<Integer> tmparray;
		while(token1.hasMoreTokens())
		{
			tmp = token1.nextToken();
			token2 = new StringTokenizer(tmp,",");
			tmparray = new ArrayList<Integer>();
			while(token2.hasMoreTokens())
				tmparray.add(Integer.parseInt(token2.nextToken()));
			cand1.add(tmparray);
		}
		token1 = new StringTokenizer(can2,"#");
		while(token1.hasMoreTokens())
		{
			tmp = token1.nextToken();
			token2 = new StringTokenizer(tmp,",");
			tmparray = new ArrayList<Integer>();
			while(token2.hasMoreTokens())
				tmparray.add(Integer.parseInt(token2.nextToken()));
			cand2.add(tmparray);
		}		
	}
	
	public static int[][] find_route(ArrayList<ArrayList<Integer>> cand1,ArrayList<ArrayList<Integer>> cand2)
	{
		int[][] result = new int[1][2];
		ArrayList<Integer> index1 = new ArrayList<Integer>();
		ArrayList<Integer> index2 = new ArrayList<Integer>();
		int maxindex1,max1=0,maxindex2,max2=0;
		for(int i=0;i<cand1.size();i++)
			if(cand1.get(i).size()>=max1)
			{
				if(cand1.get(i).size()>max1)
				{
					index1.clear();
					index1.add(i);
					max1 = cand1.get(i).size();
				}
				else
					index1.add(i);
			}
		for(int i=0;i<cand2.size();i++)
			if(cand2.get(i).size()>=max2)
			{
				if(cand2.get(i).size()>max2)
				{
					index2.clear();
					index2.add(i);
					max2 = cand2.get(i).size();
				}
				else
					index2.add(i);
			}
		if(max1>max2)
		{
			if(index1.size()==1)
			{
				result[0][0] = 1;
				result[0][1] = index1.get(0);
			}
			else
			{
				
			}
		}
			

	}
	
	
	
	
	/*
	//2 point crossover
	public static String[] doCrossover(String can1,String can2) throws IOException 
	{	
		int[] canArray1 = toArray(can1);
		int[] canArray2 = toArray(can2);
		
		Random rnd = new Random();
		int first = rnd.nextInt(canArray1.length);
		int second = rnd.nextInt(canArray1.length);
		
		int tmp;
		for(int i=first+1;i<second;i++)
		{
			tmp = canArray1[i];
			canArray1[i] = canArray2[i];
			canArray2[i] = tmp;
		}
		String[] cans = new String[2];		
		
		if(rnd.nextInt(100)<20)
		{
		     cans[0] = tostring(repair(mutate(canArray1)));
		     cans[1] = tostring(repair(mutate(canArray2)));
		}
		else
		{
			cans[0] = tostring(repair(canArray1));
			cans[1] = tostring(repair(canArray2));
		}
		return cans;
	}
	*/
	static void  printarray(int[] array)
	{
		for(int i=0;i<array.length;i++)
			System.err.print(array[i]+",");
		System.err.println();
	}
	/*
	public static String[] doCrossover(String can1,String can2) throws IOException 
	{
		int[] canArray1 = toArray(can1);
		int[] canArray2 = toArray(can2);
		
		int[] tmp1 = new int[canArray1.length];
		int[] tmp2 = new int[canArray2.length];
		boolean[] check = new boolean[canArray1.length+1];
		//System.err.println("---------- check ---- "+canArray1.length);
		//printarray(canArray2);
		Random rnd = new Random();
		int first = rnd.nextInt(canArray1.length);
		int second = rnd.nextInt(canArray1.length);		
		
		int tmp;
		
		if(first>second && !((second==canArray1.length-2)&& first==second+1))
		{
			tmp = first;
			first = second;
			second = tmp;
		}
		else if(first==second || first==canArray1.length-1 || (first==second-1 && first==canArray1.length-2))
		{
			first = (canArray1.length-1)/3;
			second = ((canArray1.length-1)*2)/3;
		}
		
		for(int i=first+1;i<second;i++)
		{
			tmp1[i] = canArray1[i];
			check[canArray1[i]] = true;
		}
			
		for(int i=first+2,j=0; j<canArray2.length ; i=(i+1)%canArray1.length)
		{			
		  //System.err.println(i+"-----"+j);
			if(j>first && j<second)
				j++;
			else
			{
				if(!check[canArray2[i]])
				{
					tmp1[j++] = canArray2[i];
					check[canArray2[i]] = true;
				}
			}
		}
		//------------------------------------------
		check = new boolean[canArray1.length+1];
		
		first = rnd.nextInt(canArray2.length);
		second = rnd.nextInt(canArray2.length);		
		
		if(first>second && !((second==canArray1.length-2)&& first==second+1))
		{
			tmp = first;
			first = second;
			second = tmp;
		}
		else if(first==second || first==canArray1.length-1 || (first==second-1 && first==canArray1.length-2))
		{
			first = (canArray1.length-1)/3;
			second = ((canArray1.length-1)*2)/3;
		}
		
		
		for(int i=first+1;i<second;i++)
		{
			tmp2[i] = canArray2[i];
			check[canArray2[i]] = true;
		}		
		
		for(int i=first+2,j=0; j<canArray1.length  ; i=(i+1)%canArray1.length)
		{			
			if(j>first && j<second)
				j++;
			else
			{
				if(!check[canArray1[i]])
				{
					tmp2[j++] = canArray1[i];
					check[canArray1[i]] = true;
				}
			}
		}
		
		String[] cans = new String[2];	
		if(rnd.nextInt(100)<30)
		{
		     cans[0] = tostring(mutate(tmp1));
		     cans[1] = tostring(mutate(tmp2));
		}
		else
		{
			cans[0] = tostring(tmp1);
			cans[1] = tostring(tmp2);
		}
		return cans;
		
	}
	*/
	
	static int[] mutate(int[] can)
	{
		int t=100;
		
		Random rnd = new Random();
		rnd.nextInt(100);
		
		if(t<25)
		{
			int i = rnd.nextInt(can.length);
			int j = rnd.nextInt(can.length);
			
			t = can[i];
			can[i] = can[j];
			can[j] = t;
			return can;
		}
		return can;
	}
	
	static int[] toArray(String str)
	{
		StringTokenizer token = new StringTokenizer(str,",");
		int[] array = new int[token.countTokens()];
		int i=0;
		while(token.hasMoreTokens())
			array[i++] = Integer.parseInt(token.nextToken());
		return array;
	}

	static String  tostring(int[] can)
    {
    	StringBuffer buf = new StringBuffer();
    	int i;
    	for(i=0;i<can.length-1;i++)
    		buf.append(can[i]+",");
    	buf.append(can[i]);
    	return buf.toString();
    }
	
	static int[] repair(int[] array)
	{
		int[] dummy = new int[array.length+1];
		for(int i=0;i<array.length;i++)
			dummy[array[i]]++;
		
		for(int i=0;i<array.length;i++)
		{
			if(dummy[array[i]]>1)
			{
				dummy[array[i]]--;
				array[i] = getFirstZero(dummy);
				dummy[array[i]]++;				
			}
		}
		return array;
	}
	
	static int getFirstZero(int[] dummy)
	{
		for(int i=1;i<dummy.length;i++)
			if(dummy[i]==0)
				return i;
		return -1;
	}
	
}
