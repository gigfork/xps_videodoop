package job;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;


@SuppressWarnings("deprecation")
public class topWordsReduce  extends MapReduceBase 
			implements Reducer<Text, myCustomWritable, Text, Text> {

	@Override
	public void reduce(Text key, Iterator<myCustomWritable> values,
			OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		String word;
		int count;		
		myCustomWritable itr;
		while(values.hasNext())
		{
			itr = values.next();
			word = itr.getFilename().toString();
			count = itr.getCount().get();
			if(map.containsKey(word))
				map.put(word, map.get(word)+1);
			else
				map.put(word, 1);
		}
		
		LinkedHashMap<String, Integer> sortedmap = sortHashMapByValuesD(map);
		
		String cont="";
		String arr[] = new String[sortedmap.keySet().size()];
		sortedmap.keySet().toArray(arr);
		
		for(int i=arr.length-1;i>=arr.length-6;i--)
		{
			word = arr[i];
			cont+=word+"("+sortedmap.get(word)+")"+" , ";
		}
		
		output.collect(key, new Text(cont));
		
	}
	
	
	public LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
	    
		List mapKeys = new ArrayList(passedMap.keySet());
	    List mapValues = new ArrayList(passedMap.values());
	    
	    Collections.sort(mapValues);
	    Collections.sort(mapKeys);
	        
	    LinkedHashMap sortedMap = new LinkedHashMap();	    
	    Iterator valueIt = mapValues.iterator();
	    
	    while (valueIt.hasNext()) {
	        
	    	Object val = valueIt.next();
	        Iterator keyIt = mapKeys.iterator();
	        
	        while (keyIt.hasNext()) {
	            Object key = keyIt.next();
	            String comp1 = passedMap.get(key).toString();
	            String comp2 = val.toString();
	            
	            if (comp1.equals(comp2)){
	                passedMap.remove(key);
	                mapKeys.remove(key);
	                sortedMap.put((String)key, (Integer)val);
	                break;
	            }
	        }
	    }
	    return sortedMap;
	}

}
