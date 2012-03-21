package job;

import java.io.IOException;
import java.util.Iterator;

import job.Puzzle.myCounters;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class puzzleReduce_second extends MapReduceBase 
implements Reducer<keyPair, Text, Text, Text> {
  
	public void reduce(keyPair key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		
		int count = 0;
		while(values.hasNext() )
		{
			String s = values.next().toString();
			if(count<15000  ||s.charAt(s.indexOf('\t')+1)=='P')
			{
				if(s.charAt(s.indexOf('\t')+1)=='C')
				{
					count++;
					reporter.incrCounter(myCounters.NoCs1,1);
				}
				output.collect(new Text(s.substring(0,s.indexOf('\t'))), new Text(s.substring(s.indexOf('\t')+1)));
			}
		}
	}
}
