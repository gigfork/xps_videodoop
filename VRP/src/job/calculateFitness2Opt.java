package job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import job.generateClass.Node;

import org.apache.hadoop.io.IntWritable;

public class calculateFitness2Opt {

	int customer[][];
	int distance[][];
	int type[][];	
	float speed[][];
	int numNodes,cap;	
	int fit,veh,distf;
	
	public calculateFitness2Opt(int customer[][],int distance[][],int type[][],float speed[][],int cap) throws IOException {
		this.customer = customer;
		this.distance = distance;
		this.type = type;
		this.speed = speed;
		numNodes = customer.length;
		this.cap = cap;	
	}
	
	String getNumVeh()
	{
		return ""+fit+'\t'+distf+'\t'+veh;
	}
	
	void printarray(int[] array)
	{
		for(int k=0;k<array.length;k++)
			System.err.print(array[k]+" ");
		System.err.println();
	}
	
	void printNodeArray(ArrayList<Node> list)
	{
		System.err.print("--");
		for(int k=0;k<list.size();k++)
			System.err.print(list.get(k).value+",");
		System.err.print("--");
	}
	
	String getFitness(int[] array) throws Exception
	{			
		int tot_veh=0,tot_time=0,tot_dist=0;
		//tot_time calculated directly at end of route
		int tmp_time=0,tmp_dist=0;
		
		int tmp,first=0,last=0;
		
		StringBuffer return_val = new StringBuffer("");
		
		tot_veh++;
		Random rnd = new Random();
		
		int i = array[0]; 								// i is now number of customers
		
		Node n = new Node(i);
		Node n0 = new Node(0); // depot
		n.time_dep = getTime(n0,i)+customer[i][5];
		n.cap_left -= customer[i][2];
		tmp_dist += distance[0][n.value];
		
		ArrayList<Node> list = new ArrayList<Node>();
		list.add(n);
		//for(int y=0;y<list.size();y++)
			//System.err.print(list.get(y).value +",");
		//System.err.println();
		for(int k=1;k<array.length;k++)
		{
			tmp = check_feasibility(list.get(list.size()-1),array[k]);			
			
			if(tmp!=-1) //if not end of route
			{
				last = k;
				i = array[k];
				n = new Node(i);
				n.cap_left = list.get(list.size()-1).cap_left - customer[i][2];
				n.time_dep = tmp + customer[i][5];
				tmp_dist+=distance[list.get(list.size()-1).value][i];
				list.add(n);			
			}
			
			if(tmp==-1 || k == array.length-1) //if end of route/last index
			{
				int prb = rnd.nextInt(100);
				
				if(prb<30 && list.size()>2)
				{
					tmp_time+=getTime(list.get(list.size()-1),0);
					tmp_dist+=distance[list.get(list.size()-1).value][0];
					int[] p = TwoOpt(array,first,last,tmp_time, tmp_dist);
					tmp_time = p[0];					
					tmp_dist = p[1];
				}
				else
				{
					tmp_time+=getTime(list.get(list.size()-1),0);
					tmp_dist+=distance[list.get(list.size()-1).value][0];
				}
				last = first = last+1;
				
				//System.err.print("-------- route = ");
				for(int y=0;y<list.size();y++)
					return_val.append(list.get(y).value+",");
				return_val.deleteCharAt(return_val.length()-1);
				return_val.append("#");				
				//	System.err.print(list.get(y).value +",");
				//System.err.println();
				list.clear();		
				
				tot_dist += tmp_dist;
				tot_time += tmp_time;
				
				tmp_dist = 0;
				tmp_time = 0;
				
				if(tmp==-1) //i.e. it is not the last index which made the route to end
				{
					//new route
					i = array[k];
					n = new Node(i);
					n.cap_left -= customer[i][2];
					n.time_dep = getTime(n0,i)+customer[i][5];
					tmp_dist += distance[0][n.value];				
					list.add(n);					
					if(k==array.length-1)
					{
						tmp_time += getTime(list.get(list.size()-1),0);
						tmp_dist += distance[n.value][0];
						tot_dist += tmp_dist;
						tot_time += tmp_time;
						
						for(int y=0;y<list.size();y++)
							return_val.append(list.get(y).value+",");
						return_val.deleteCharAt(return_val.length()-1);
						return_val.append("#");	
					}
					tot_veh++;
				}
			}				
		}		
		return_val.deleteCharAt(return_val.length()-1);
		this.veh = tot_veh;		
		this.fit = tot_time;
		this.distf = tot_dist;
		//System.err.println((numNodes*customer[0][4])+"-----"+((float)tot_veh*customer[0][4]+tot_time));
		//System.err.println((1000*((numNodes*customer[0][4])/((float)tot_veh*customer[0][4]+tot_time))));
		double t1 = ((double)numNodes)*customer[0][4];
		double t2 = (((double)tot_veh)*customer[0][4]+tot_time);
		return_val.append("\t");
		return_val.append((int) ((10000*t1/t2)));
		return return_val.toString();
		//return (int) ((10000*t1/t2));//((100*((numNodes*customer[0][4])/((double)tot_veh*customer[0][4]+tot_time))));		
	}
	 
	
	int[] getFitnessOpt(int[] array) throws Exception
	{			
		//there should be only route if not the 2opt is not possible
		int tmp_time=0,tmp_dist=0;		
		int tmp_veh=1;
		int tmp;
		
		int i = array[0]; // i is now number of customer
		Node n = new Node(i);
		Node n0 = new Node(0); // depot
		
		n.time_dep = getTime(n0,i)+customer[i][5];
		n.cap_left -= customer[i][2];
		
		tmp_dist += distance[0][n.value];
		
		ArrayList<Node> list = new ArrayList<Node>();
		list.add(n);
		
		for(int k=1;k<array.length;k++)
		{
			tmp = check_feasibility(list.get(list.size()-1),array[k]);			
			
			if(tmp!=-1) //if not end of route
			{
				i = array[k];
				n = new Node(i);
				n.cap_left = list.get(list.size()-1).cap_left - customer[i][2];
				n.time_dep = tmp + customer[i][5];
				tmp_dist+=distance[list.get(list.size()-1).value][i];
				list.add(n);			
			}
			if(tmp==-1 && k!=array.length-1)
				tmp_veh++;
			else
			{
				tmp_time+=getTime(list.get(list.size()-1),0);
				tmp_dist+=distance[list.get(list.size()-1).value][0];				
			}				
		}		
		int[] ret_val = new int[3];
		ret_val[0] = tmp_veh;
		ret_val[1] = tmp_time;
		ret_val[2] = tmp_dist;
		return ret_val;
	}
	
	int[] TwoOpt(int array[],int first,int last,int orig_fitness,int orig_dist) throws Exception
	{
		//System.err.println("--------------");
		int t;
		int[] dummy = new int[last-first+1];	
		int[] tmp = new int[last-first+1];
		int[] tmp_ret;
		int fit1 = orig_fitness;
		int dist1 = orig_dist;

		for(int i=first;i<last;i++)
			for(int j=i+1;j<=last;j++)
			{
				t=0;
				for(int k=first;k<=i;k++) //first-a
					dummy[t++] = array[k]; 
				
				dummy[t++] = array[j];  // b
				
				for(int k=i+2;k<j;k++)  // c-b
					dummy[t++] = array[k];
				
				if(i+1<j)
					dummy[t++] = array[i+1]; //c
				for(int k=j+1;k<=last;k++) //d-last
					dummy[t++] = array[k];
				
				tmp_ret = getFitnessOpt(dummy);
				//System.err.println(fit1+" - "+tmp_ret[0]+'\t'+tmp_ret[1]);
				if(tmp_ret[0]==1 && tmp_ret[1]<fit1)
				{
					tmp = Arrays.copyOf(dummy,dummy.length);
					fit1 = tmp_ret[1];
					dist1 = tmp_ret[2];
				}
			}
		
		t=0;
		for(int k=first;k<=last;k++) //first-a
			dummy[t++] = array[k];
		
		if(fit1<orig_fitness)
		{
			//System.err.println();
			//System.err.println("---------2opt");
			//printarray(dummy);
			t=0;
			for(int k=first;k<=last;k++)
				array[k] = tmp[t++];
			//printarray(tmp);
		}
		
		int[] ret_val = new int[2];
		ret_val[0] = fit1;
		ret_val[1] = dist1;
		return ret_val;
	}	
	
	int check_feasibility(Node n,int m)
	{
		int time;
		if(n.time_dep > customer[m][4] || n.cap_left < customer[m][2] ) return -1;
		else
		{
			time = getTime(n, m);			
			
			if(time>customer[m][4] || time==-1)
				return -1;
			else 
			{
				Node tmpN = new Node(m);
				tmpN.cap_left = n.cap_left - customer[m][2];
				tmpN.time_dep = time + customer[m][5];
				//System.err.println(n.value+","+m+"-("+time+","+customer[0][4]+")-");
				if(getTime(tmpN,0)>customer[0][4])
					return -1;				
				return time;
			}
		}			
	}
	

	int getTime(Node n,int m)
	{
		
		int div = customer[0][4]/4;
		int typer = type[n.value][m];
		int zone = (int)(n.time_dep/div)+1;		
		
		int dist = distance[n.value][m];	
		int ctime = n.time_dep;
		
		while(dist!=0 && zone<= type[1].length)
		{
			if((((zone*div)-ctime)*speed[typer][zone])>=dist)
			{
				ctime = ctime + (int)(dist/speed[typer][zone]);
				if(ctime<customer[m][3])
					return customer[m][3];
				else
					return ctime;
			}
			else
			{
				dist-=((zone*div)-ctime)*speed[typer][zone];
				ctime = (zone*div);
				zone++;
			}
		}
		return -1;
	}
	
	class Node
	{
		int value;
		int time_dep;
		int cap_left;
		int lastInd;
		
		public Node(int v) {
			value = v;
			time_dep = 0;
			cap_left = cap;
		}
	}	
}
