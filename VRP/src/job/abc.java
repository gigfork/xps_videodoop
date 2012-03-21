package job;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapFile;





public class abc
{

	int customer[][];
	int distance[][];
	int type[][];	
	float speed[][];
	int numNodes,cap;	
	calculateFitness2Opt cft;
	
	abc() throws IOException
	{
		conf_normal();
		cft = new calculateFitness2Opt(customer, distance, type, speed, cap);
	}
	
	public void conf_normal()
	{
		try
		{		Configuration conf = new Configuration();
	
				MapFile.Reader reader;
				FileSystem fs = FileSystem.get(URI.create("/user/user/user/rohit/input/vrp_input/customerFile.map"), conf);
				reader = new MapFile.Reader(fs,"/user/user/user/rohit/input/vrp_input/customerFile.map",conf);
				createCustDistArray(reader);
				reader = new MapFile.Reader(fs,"/user/user/user/rohit/input/vrp_input/type.map",conf);
				createTypeArray(reader);
				reader = new MapFile.Reader(fs,"/user/user/user/rohit/input/vrp_input/speed.map",conf);
				createSpeedArray(reader);
				cft = new calculateFitness2Opt(customer,distance,type,speed,cap);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	void createCustDistArray(MapFile.Reader customerFile) throws IOException
	{		
		IntArrayWritable atcust  = new IntArrayWritable(6);
		customerFile.get(new IntWritable(-2),atcust);
		numNodes = atcust.get()[0].get();
		customerFile.get(new IntWritable(-1),atcust);
		cap = atcust.get()[0].get();
		
		customer = new int[numNodes][6];
		
		for(int i=0;i<numNodes;i++)
		{
			customerFile.get(new IntWritable(i),atcust);
			for(int j=0;j<6;j++)
				customer[i][j] = atcust.get()[j].get();
		}
		createDistanceArray();
	}
	
	void createDistanceArray()
	{		
		distance = new int[numNodes][numNodes];
		
		for(int i=0;i<numNodes;i++)
			for(int j=0;j<numNodes;j++)
				if(i==j)
					distance[i][j]=0;
				else
					distance[i][j]=distance[j][i]= (int) Math.sqrt((customer[i][0] - customer[j][0])*(customer[i][0] - customer[j][0]) + (customer[i][1] - customer[j][1])*(customer[i][1] - customer[j][1]));
					
	}
		
	void createTypeArray(MapFile.Reader reader) throws IOException
	{
		type = new int[numNodes][numNodes];
		IntArrayWritable arr  = new IntArrayWritable(numNodes);
		for(int i=0;i<numNodes;i++)
		{
			reader.get(new IntWritable(i),arr);
			for(int j=0;j<numNodes;j++)
				type[i][j] = arr.get()[j].get();
		}		
	}
	
	void createSpeedArray(MapFile.Reader reader) throws IOException
	{
		speed = new float[6][5];
		IntArrayWritable arr  = new IntArrayWritable(4);
		for(int i=1;i<=5;i++)
		{
			reader.get(new IntWritable(i-1),arr);
			for(int j=1;j<=4;j++)
				speed[i][j] = arr.get()[j-1].get()/10.0f;
		}		
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
	
	
	public static void main(String[] args) throws Exception
	{
		StringBuffer j = new StringBuffer("");
		j.append("hey");
		System.out.print(j.toString());
		ArrayList<Integer> a = new ArrayList<Integer>();
		a.add(22);
		a.add(32);
		a.add(33);
		System.out.println(a.indexOf(33));
		/*
		int array[] = {21,23,83,52,59,75,11,12,14,47,15,16,9,99,57,86,87,82,65,90,69,98,53,88,100,70,55,68,61,54,81,96,93,71,72,41,39,37,35,36,43,1,3,46,4,60,17,10,13,97,58,74,24,25,48,77,89,32,91,80,42,44,2,45,5,73,7,78,79,6,8,40,38,94,92,95,84,51,76,63,85,50,62,67,56,64,66,22,20,49,19,18,26,27,34,31,29,28,30,33};
		int fitness;
		abc boo = new abc();
		
		fitness = boo.cft.getFitness(array);
		System.err.println(fitness);
		*/
	}
}