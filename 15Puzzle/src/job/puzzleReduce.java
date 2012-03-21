package job;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import job.Puzzle.myCounters;;



public class puzzleReduce extends MapReduceBase 
implements Reducer<Text, Text, Text, Text> {

	private JobConf conf;
	
	@Override
	public void configure(JobConf conf)
	{
		this.conf = conf;
	}
	
	
	@Override
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		
		String value,t;
		char type = 'C';
		String other = "none",other1=null; // parent + heuristics
		int min = 11,tmp=0;
		generate gn = new generate(key.toString());		
		
		if(gn.check(gn.convertToArray(key.toString())))
			reporter.incrCounter(myCounters.Finished, 1);	
		//System.err.println("---------------------------------------------------");
		System.err.println(key.toString());
		while(values.hasNext())
		{
			value = values.next().toString();
			type = value.charAt(0);
			if(type =='C')
				other = value.substring(2);
			if(type =='P')
			{
				other = value.substring(2);
				break;
				/*t = other.substring(other.lastIndexOf('\t')+1);
				
				if(!t.equals("none"))
				{
					tmp = Integer.parseInt(t);
					if(tmp<8)System.err.print(tmp);
					if(tmp<min)
					{
						min = tmp;
						other1 = other;
						//System.err.println("----"+min + "  "+tmp);
					}
					else if (tmp>=11)
					{					
						min = -1;
						break;
					}
				}
				else if(t.equals("none"))
				{					
					min = -1;
					break;
				}*/
			}
		}
		/*
		if(type!='C'){
		String ot;
		if(min!=-1)
		{
		   ot = other1.substring(0,other1.lastIndexOf('\t')+1)+min;
		   System.err.println("**"+min);
		}else
			ot = other;
		}*/
		
		
		if(type=='C')
			reporter.incrCounter(myCounters.NoCs,1);
		if(type!='\0')
			output.collect(key,new Text(type+"	"+other));
			
	}

}
