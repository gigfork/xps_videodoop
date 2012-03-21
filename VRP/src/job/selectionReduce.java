package job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

/******************* cum_fit technique*****/
@SuppressWarnings("deprecation")
public class selectionReduce extends MapReduceBase 
implements Reducer<IntWritable, Text, IntWritable, Text>{
	
	private JobConf conf;
	private FileSystem fs;
	
	
	@Override
	public void configure(JobConf conf)
	{
		this.conf = conf;
		try {
			fs = FileSystem.get(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void reduce(IntWritable key, Iterator<Text> values,
			OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException {
		
		
	    int max_fitness=0,max_index=0,tmp;
	    
	    ArrayList<String> val = new ArrayList<String>();			//storing individual
	    ArrayList<Integer> rank_prob = new ArrayList<Integer>();	//Cumulative rank
	    int fitness_prob=0;
	    
		String temp;
		HashMap fit_map = new HashMap();								// main map
		
		int sum = 0,p=0;
		//System.out.println("-------------");
		while(values.hasNext())
		{
			temp = values.next().toString();			
			val.add(temp.substring(0,temp.indexOf('\t')));
			tmp = Integer.parseInt(temp.substring(temp.indexOf('\t')+1)); //fitness
		//	System.out.println("tmp = "+tmp);		
			sum+=tmp;
			//System.out.println("sum = "+sum);
			if(tmp>max_fitness)
			{
			   	max_index = val.size()-1;
			   	max_fitness = tmp;
			}

			fit_map.put(val.size()-1, tmp);
		}
	
		LinkedHashMap sortedmap;
		sortedmap = sortHashMapByValuesD(fit_map);
		

		//for (Iterator iter = sortedmap.keySet().iterator(); iter.hasNext();) {
			//int map_key = (Integer) iter.next();
		//	System.out.println(map_key+"--"+sortedmap.get(map_key));		
		//}
		
		/*
		while(values.hasNext())
		{
			temp = values.next().toString();			
			val.add(temp.substring(0,temp.indexOf('\t')));
			
			tmp = Integer.parseInt(temp.substring(temp.indexOf('\t')+1)); //fitness		    
		    if(tmp>max_fitness)
		    {
		    	max_index = val.size()-1;
		    	max_fitness = tmp;
		    }
		    fitness+=tmp;		    	
		    cum_fitness.add(fitness);
		}
		*/
		
		output.collect(key,new Text(val.get(max_index))); // elitism
		fs.delete(new Path("/user/user/user/rohit/input/islands/"+key.toString()));
		FSDataOutputStream out = fs.create(new Path("/user/user/user/rohit/input/islands/"+key.toString()));
		//System.out.println("sum = "+ sum);
		//out.writeInt(max_fitness);	
		out.writeInt(sum/val.size());
		out.flush();
		out.close();
		
		int[] ind_array = new int[val.size()+1]; // rank wise map index store - ascending order
		//ranking method-----	
		int i=0,j=0,k=0;
		for (Iterator iter = sortedmap.keySet().iterator(); iter.hasNext();) {
			int map_key = (Integer) iter.next();
			//System.out.println(map_key+" -- "+sortedmap.get(map_key));
			//System.out.println("Value/key:"+sortedmap.get(map_key)+"/"+map_key);
			//System.err.println((val.size()*val.size()*2*i)/((float)(val.size()*(val.size()+1))));
			fitness_prob += (val.size()*val.size()*2*(i+1))/((float)(val.size()*(val.size()+1)));
			//System.err.println("--"+fitness_prob);
			rank_prob.add(fitness_prob);
			ind_array[i] = map_key;
			i++;
		}
	//	System.err.println(sortedmap.get(ind_array[100])+"--"+sortedmap.get(ind_array[200]));
		Random rnd = new Random();			
		String[] cans;
		i=0;
		while(++i<val.size()/2)
		{
			j=0;k=0;
			tmp = rnd.nextInt(fitness_prob);
			while(rank_prob.get(j++).compareTo(tmp)<0);
			tmp = rnd.nextInt(fitness_prob);
			while(rank_prob.get(k++).compareTo(tmp)<0);			
			//System.out.println(j+"--"+k);
			tmp = rnd.nextInt(100);
			cans = new String[2];
			//System.err.println(j+"-"+ind_array[j-1]+"-"+sortedmap.get((ind_array[j-1]))+" --**-- "+k+"-"+ind_array[k-1]+"-"+sortedmap.get(((ind_array[k-1]))));
			//island change
			if(tmp<30)
			{
				int key1 = rnd.nextInt(conf.getNumReduceTasks());
				cans = crossover.doCrossover(val.get(ind_array[j-1]),val.get(ind_array[k-1]));
				output.collect(new IntWritable(key1), new Text(cans[0]));
				key1 = rnd.nextInt(conf.getNumReduceTasks());
				output.collect(new IntWritable(key1), new Text(cans[1]));	
			}
			
			else
			{
				cans = crossover.doCrossover(val.get(ind_array[j-1]),val.get(ind_array[k-1]));
				//System.out.println(cans[0]);
				//System.out.println(cans[1]);
				output.collect(key, new Text(cans[0]));	
				output.collect(key, new Text(cans[1]));	
			}
		}		
	}
	
	public static LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
	    
		List mapKeys = new ArrayList(passedMap.keySet());
	    List mapValues = new ArrayList(passedMap.values());
	    Collections.sort(mapValues);
	    Collections.sort(mapKeys);
	        
	    LinkedHashMap sortedMap = 
	        new LinkedHashMap();
	    
	    Iterator valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	        Object val = valueIt.next();
	        Iterator keyIt = mapKeys.iterator();
	        
	        while (keyIt.hasNext()) {
	            Object key = keyIt.next();
	            Integer comp1 = Integer.parseInt(passedMap.get(key).toString());
	            Integer comp2 = Integer.parseInt(val.toString());
	            
	            if (comp1.equals(comp2)){
	                passedMap.remove(key);
	                mapKeys.remove(key);
	                sortedMap.put((Integer)key, (Integer)val);
	                break;
	            }
	        }
	    }
	    return sortedMap;
	}
}
