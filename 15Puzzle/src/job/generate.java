package job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;

public class generate {

	String finalString;
	String[] finalArray;
	
	public generate(String s) {
		
		StringTokenizer token = new StringTokenizer(s,",");
		int l = token.countTokens();
		StringBuffer s1 = new StringBuffer();
		int i=1;
		while(i!=l)
		{
			s1.append(i++);
			s1.append(',');
		}
		s1.append('x');
		finalString = s1.toString();
		finalArray = convertToArray(finalString);
	}	
	
	String[] convertToArray(String s)
	{
		StringTokenizer token = new StringTokenizer(s,",");
		String[] array = new String[token.countTokens()];
		int i=0;
		while(token.hasMoreTokens())
			array[i++] = token.nextToken();
		return array;
	}	
	
	int find(String[] array,String s)
	{
		for(int i=0;i<array.length;i++)
			if(array[i].equals(s)) return i;
		return -1;			
	}
	
	String tostring(String[] array)
	{
		StringBuffer s = new StringBuffer();
		for(String s1:array)
		{
			s.append(s1);
			s.append(',');
		}
		s.deleteCharAt(s.length()-1);
		return s.toString();		
	}
	
	
	String Up (String[] s,int l,int ind)
	{		
		String[] s1 = Arrays.copyOf(s,s.length);		
		if(ind+1 > l)
		{
			//System.err.println(ind+" "+l);
			s1[ind] = s1[ind-l];
			s1[ind-l] = "x";
			return tostring(s1);
		}	
		return null;
	}
	
	String Down (String[] s, int l, int ind)
	{		
		String[] s1 = Arrays.copyOf(s,s.length);
		if(ind+1 <= l*(l-1))
		{
			s1[ind] = s1[ind+l];
			s1[ind+l] = "x";
			return tostring(s1);
		}	
		return null;
	}
	
	String Right (String[] s, int l, int ind)
	{		
		String[] s1 = Arrays.copyOf(s,s.length);

		if((ind+1)%l!=0)
		{
			s1[ind] = s1[ind+1];
			s1[ind+1] = "x";
			return tostring(s1);
		}	
		return null;
	}
	
	String Left (String[] s, int l, int ind)
	{		
		String[] s1 = Arrays.copyOf(s,s.length);
		if((ind+1)%l!=1)
		{
			s1[ind] = s1[ind-1];
			s1[ind-1] = "x";
			return tostring(s1);
		}	
		return null;
	}
	
	String getJobId(String s)
	{
		StringTokenizer token = new StringTokenizer(s,"_");	
		token.nextToken();
		token.nextToken();
		return String.valueOf(Integer.parseInt(token.nextToken())+10);
	}
	
	
	boolean check(String[] s)
	{
		for(int i=0;i<s.length;i++)
			if(!s[i].equals(finalArray[i]))
				return false;		
		return true;
	}
	
	int Max_swap(String[] array)
	{
		String[] s = Arrays.copyOf(array,array.length);
		int[] b = new int[s.length];
		
		int swap=0;
		
		for(int i=0;i<s.length-1;i++)
			b[i] = find(s,String.valueOf(i+1));		
		b[b.length-1] = find(s,"x");
		
		while(!check(s))
		{
			if(b[b.length-1]==(b.length-1))
			{
				randomPut(s,b);
				swap++;
			}
			
			s[b[b.length-1]] = String.valueOf(b[b.length-1]+1);
			s[b[b[b.length-1]]] = "x";
			
			int t = b[b[b.length-1]];
			b[b[b.length-1]] = b[b.length-1];
			b[b.length-1] = t;			
			swap++;
		}
		return swap;
	}
	
	void randomPut(String[] s,int[] b)
	{
		Random rnd = new Random();
		int t=-1;		
		while(true)
		{
			t++;
			if(b[t]!=t)
			{
				//System.err.println(t+" "+b[t]+" "+s[b[t]]);
				s[b.length-1] = s[b[t]];
				s[b[t]]="x";
				b[b.length-1] = b[t];
				b[t] = b.length-1;
				break;
			}				
		}
	}
	
	
	int out_rc(String[] s)
	{
		int num=0;
		int l = (int)Math.sqrt(s.length);
		for(int i=0;i<s.length;i++)
		{
			if(!s[i].equals("x") && find(finalArray,s[i])/l != i/l)
				num++;
			if(!s[i].equals("x") && find(finalArray,s[i])%l != i%l)
				num++;
		}
		return num;
	}
	
	
	int MDistPair(String[] s)
	{
		int l = (int)Math.sqrt(s.length);
		return (ManhattanHeuristics(s, l)+distancePair(s, l));
	}	
	
	
	int ManhattanHeuristics(String[] s, int l)
	{
		int mdist=0;
		
		for(int i=0;i<s.length;i++)
		{
			if(!s[i].equals("x"))
			{
				mdist += Math.abs(i/l - (find(finalArray,s[i])/l)); // row difference
				mdist += Math.abs((i%l)- (find(finalArray,s[i])%l)); // col difference
			}
		}
		return mdist;
	}
	
	int distancePair(String[] s, int l)
	{
		ArrayList<Integer> rowlist = new ArrayList<Integer>();
		int pdist=0;
		for(int i=0;i<s.length/l;i++)
		{
			for(int j=i*l;j<(i+1)*l;j++)
				for(int k=j+1;k<(i+1)*l;k++)
				{
					if(s[j].equals("x") && s[k].equals("x") && rowlist.indexOf(j)==-1 && rowlist.indexOf(k)==-1 && rPair(s,i,j,k,l))
					{
						rowlist.add(j);
						rowlist.add(k);
						pdist+=2;
					}						
				}
		}
		ArrayList<Integer> collist = new ArrayList<Integer>();
				
		for(int i=0;i<s.length/l;i++)
		{
			for(int j=i;j<i+1+l*(l-1);j=j+l)
				for(int k=j+l;k<i+1+l*(l-1);k=k+l)
				{
					if(s[j].equals("x") && s[k].equals("x") && collist.indexOf(j)==-1 && collist.indexOf(k)==-1 && cPair(s,i,j,k,l))
					{
						collist.add(j);
						collist.add(k);
						pdist+=2;
					}						
				}
		}
		return pdist;		
	}
	
	boolean rPair(String[] s,int i,int j,int k,int l)
	{
		String[] finalArray =  convertToArray(finalString);
		
		int x,y;
		if(((x=find(finalArray,s[j])/l) == find(finalArray,s[k])/l) && x==i)
		{
			x = find(finalArray,s[j]);
			y = find(finalArray,s[k]);
			
			if((j>k && x<y)||(j<k && x>y))
				return true;
		}
		return false;
	}
	
	boolean cPair(String[] s,int i,int j,int k,int l)
	{
		String[] finalArray =  convertToArray(finalString);
		
		int x,y;
		if( (x=find(finalArray,s[j])%l) == find(finalArray,s[k])%l && x==i)
		{
			x = find(finalArray,s[j]);
			y = find(finalArray,(s[k]));
			
			if((j>k && x<y)||(j<k && x>y))
				return true;
		}
		return false;
	}
	
}
