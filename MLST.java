import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
public class MLST {
	public static int read_input(ArrayList<ArrayList<String>> input)
	{
		int counter = 0;
		int num_graph = 0;
		int current_bound = 2;
		int next_bound = 2;
		int num_lists = 0;
		File file = new File("hard.in");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;
			while ((text = reader.readLine()) != null) {
				counter++;
				if(counter == 1)
					num_graph = Integer.parseInt(text);
				if(counter == current_bound)
				{
					next_bound = current_bound+Integer.parseInt(text)+1;
					current_bound = next_bound;
					num_lists++;
					input.add(new ArrayList<String>());
				}
		        //System.out.println("->"+text);
		        if(counter>=2)
		        	input.get(num_lists-1).add(text);
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    }
		}
		return num_graph;
	}
	public static void Apply_Reduc_Rules(Hashtable<Integer,TreeSet<Integer>>E, TreeSet<Integer> V,TreeSet<Integer>IN,TreeSet<Integer>LN,TreeSet<Integer>BN,TreeSet<Integer>FL,TreeSet<Integer>Free)
	{
		//=============== RULE 1 ==============
		{
			for(Integer u : FL)
			{
				TreeSet<Integer> nei_u = new TreeSet<Integer>(E.get(u));
				for(Integer v:E.get(u))
				{
					if(FL.contains(v))
					{
						//E.get(u).remove(v);
						//E.get(v).remove(u);
						nei_u.remove(v);
						nei_u.remove(u);
					}
				}
				E.put(u, nei_u);
			}
			for(Integer u : BN)
			{
				TreeSet<Integer> nei_u = new TreeSet<Integer>(E.get(u));
				for(Integer v:E.get(u))
				{
					if(BN.contains(v))
					{
						//E.get(u).remove(v);
						//E.get(v).remove(u);
						nei_u.remove(v);
						nei_u.remove(u);
					}
				}
				E.put(u, nei_u);
			}
		}
		//==================== RULE 2 ============
		{
			TreeSet<Integer> BN_copy = new TreeSet<Integer>(BN);
			for(Integer u : BN)
			{
				if(get_degree(u,E,V,IN,LN,BN,FL,Free)==0)
				{
					LN.add(u);
					BN_copy.remove(u);
					Free.remove(u);
				}
			}
			BN = BN_copy;
		}
		//=================== RULE 3 ================
		{
			TreeSet<Integer> Free_copy = new TreeSet<Integer>(Free);
			for(Integer u : Free)
			{
				if(get_degree(u,E,V,IN,LN,BN,FL,Free)==1)
				{
					FL.add(u);
					BN.remove(u);
					Free_copy.remove(u);
				}
			}
			Free = Free_copy;
		}
		//=================== RULE 4 ================
		{
			TreeSet<Integer> temp = new TreeSet<Integer>(Free);
			TreeSet<Integer> Free_copy = new TreeSet<Integer>(Free);
			temp.addAll(FL);
			for(Integer u : Free)
			{
				for(Integer v : E.get(u))
				{
					//if(!temp.contains(v))
					temp.retainAll(E.get(v));
					if(temp.equals(E.get(v)))
					{
						FL.add(v);
						Free_copy.remove(v);
					}
				}
			}
			Free = new TreeSet<Integer>(Free_copy);
		}
		//=================== RULE 5 ================
		{
			TreeSet<Integer> Free_copy = new TreeSet<Integer>(Free);
			for(Integer x : Free)
			{
				if(get_degree(x,E,V,IN,LN,BN,FL,Free)==2)
				{
					for(Integer y: E.get(x))
					{
						for(Integer z: E.get(y))
						{
							if(E.get(z).contains(x))
							{
								FL.add(x);
								Free_copy.remove(x);
							}
						}
					}
				}
			}
			//Free = new TreeSet<Integer>(Free_copy);
			Free = Free_copy;
		}
		//=================== RULE 6 ================
		{
			Hashtable<Integer,Integer> visited = new Hashtable<Integer,Integer>();
			TreeSet<Integer> BN_copy = new TreeSet<Integer>(BN);
			for(Integer u:BN)
			{
				for(Integer v:E.keySet())
					visited.put(v, 0);
				Queue<Integer> s = new LinkedList<Integer>();
				s.add(E.get(u).first());
				visited.put(E.get(u).first(),1);
				while(s.size()!=0)   //?????????????????
				{
					Integer t = s.poll();
					visited.put(t, 1);
					for(Integer n:E.get(t))
					{
						if(n!=u && visited.get(n)==0)
							s.add(n);
					}
				}
				if(visited.contains(0))
				{
					//u--->IN
					IN.add(u);
					BN_copy.remove(u);
				}
			}
			BN = BN_copy;
		}
		//=================== RULE 7 ================
		{
			TreeSet<Integer> tem = new TreeSet<Integer>(V);
			tem.removeAll(IN);
			for(Integer u : LN)
			{
				TreeSet<Integer> nei_u = new TreeSet<Integer>(E.get(u));
				for(Integer v:E.get(u))
				{
					if(tem.contains(v))
					{
						//E.get(u).remove(v);
						//E.get(v).remove(u);
						nei_u.remove(v);
						nei_u.remove(u);
					}
				}
				E.put(u, nei_u);
			}
		}
	}
	public static int get_degree(Integer v,Hashtable<Integer,TreeSet<Integer>>E, TreeSet<Integer> V,TreeSet<Integer>IN,TreeSet<Integer>LN,TreeSet<Integer>BN,TreeSet<Integer>FL,TreeSet<Integer>Free)
	{
		TreeSet<Integer> TEMP_IL = new TreeSet<Integer>(IN);
		TreeSet<Integer> V_IL = new TreeSet<Integer>(V);
		TEMP_IL.addAll(LN);
		V_IL.removeAll(TEMP_IL);
		TreeSet<Integer> N = new TreeSet<Integer>(E.get(v));
		if(V_IL.contains(v))
		{
			if(BN.contains(v))
			{
				TreeSet<Integer> temp = new TreeSet<Integer>(Free);
				temp.addAll(FL);
				temp.retainAll(N);
				return temp.size();
			}
			else if(Free.contains(v))
			{
				TreeSet<Integer> temp = new TreeSet<Integer>(Free);
				temp.addAll(FL);
				temp.addAll(BN);
				temp.retainAll(N);
				return temp.size();
			}
			else //if(FL.contains(v))
			{
				TreeSet<Integer> temp = new TreeSet<Integer>(Free);
				temp.addAll(BN);
				temp.retainAll(N);
				return temp.size();
			}
		}
		else
			return N.size();
	}
	public static void construct_entry(Integer key, Integer value,Hashtable<Integer,TreeSet<Integer>>E)
	{
		if(!E.containsKey(key))
		{
			TreeSet<Integer> temp = new TreeSet<Integer>();
			temp.add(value);
			E.put(key, temp);
		}
		else
		{
			E.get(key).add(value);
		}
	}
	public static void initial_EV(Hashtable<Integer,TreeSet<Integer>>E,TreeSet<Integer>V,ArrayList<String> input)
	{
		int num_edges = Integer.parseInt(input.get(0));
		for(int i=1;i<=num_edges;i++)
		{
			int key = Character.getNumericValue(input.get(i).charAt(0));  //input.get(i) = "1_2"
			int value = Character.getNumericValue(input.get(i).charAt(2));
			construct_entry(key,value,E);
			construct_entry(value,key,E);
			V.add(key);
			V.add(value);
		}
	}
	public static int mlst(Integer vn,Hashtable<Integer,TreeSet<Integer>> E, TreeSet<Integer> V,TreeSet<Integer>IN,TreeSet<Integer>LN,TreeSet<Integer>BN,TreeSet<Integer>FL,TreeSet<Integer>Free)
	{
		IN.add(vn);
		BN.addAll(E.get(vn));
		Free.remove(vn);
		Free.removeAll(BN);
		
		Apply_Reduc_Rules(E,V,IN,LN,BN,FL,Free);//Reduce G according to the reduction rules
		TreeSet<Integer> fUfl = new TreeSet<Integer>(FL);// Free union FL
		fUfl.addAll(Free);
		//if there is some unreachable v belongs Free union FL then return 0
		Hashtable<Integer,Integer> visited = new Hashtable<Integer,Integer>();
		Integer s = fUfl.first();//E.keys().nextElement();
		for(Integer v:E.keySet())
			visited.put(v, 0);
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.add(s);
		while(queue.size()!=0)
		{
			Integer t = queue.poll();
			visited.put(t, 1);
			for(Integer n:E.get(t))
			{
				if(visited.get(n)==0)
					queue.add(n);
			}
		}
		/*for(Integer k:visited.keySet())
		{
			if(visited.get(k)==0)
				return 0;
		}*/
		if(visited.contains(0))
			return 0;
		TreeSet<Integer> inUln = new TreeSet<Integer>(IN); // IN union LN
		inUln.addAll(LN);
		//if V = (IN union LN) then return |LN|
		if(V.equals(inUln))
		{
			return LN.size();
		}
		Integer max_dg_v = BN.first();  // v\\
		int max_dg = get_degree(max_dg_v,E,V,IN,LN,BN,FL,Free);  // d(v)
		//Choose a vertex v belongs BN of maximum degree
		for(Integer v:BN)
		{
			int degree = get_degree(v,E,V,IN,LN,BN,FL,Free);
			if(max_dg < degree)
			{
				max_dg_v = v;
				max_dg = degree;
			}
		}
		TreeSet<Integer> NvFL = new TreeSet<Integer>(FL);// NFL(v)
		NvFL.retainAll(E.get(max_dg_v));
		//if d(v)>=3 or (d(v)=2 and NFL(v)!=empty)
		if(max_dg>=3 || (max_dg==2 && NvFL.size()!=0))
		{
			double n = Math.random();
			if(n<=0.5)
				LN.add(max_dg_v);
			else
				IN.add(max_dg_v);
		}
		else if(max_dg==2)
		{
			//Let{x1,x2} = Nfree(v) such that d(x1)<=d(x2)
			TreeSet<Integer> NvFree = new TreeSet<Integer>(Free);//Nfree(v)
			NvFree.retainAll(E.get(max_dg_v));
			Integer x1,x2;
			int first_deg = get_degree(NvFree.first(),E,V,IN,LN,BN,FL,Free);
			int second_deg = get_degree(NvFree.last(),E,V,IN,LN,BN,FL,Free);
			if(first_deg <= second_deg)
			{
				x1 = NvFree.first();
				x2 = NvFree.last();
			}
			else
			{
				x1 = NvFree.last();
				x2 = NvFree.first();
				int temp_first = first_deg;
				first_deg = second_deg;
				second_deg = temp_first;
			}
			//=============================================
			TreeSet<Integer> x1insectx2 = new TreeSet<Integer>(E.get(x1));  //N(x1) intersect N(x2)
			x1insectx2.retainAll(E.get(x2));
			TreeSet<Integer> x1insectx2FL = new TreeSet<Integer>(x1insectx2);  //(N(x1) intersect N(x2))\FL
			x1insectx2FL.removeAll(FL);
			TreeSet<Integer> x1insectx2FLv = new TreeSet<Integer>(x1insectx2FL);//(N(x1) intersect N(x2)) \FL \v
			x1insectx2FLv.remove(max_dg_v);
			TreeSet<Integer> NFLx1 = new TreeSet<Integer>(FL); //NFL(x1)
			NFLx1.retainAll(E.get(x1));
			TreeSet<Integer> NFLx2 = new TreeSet<Integer>(FL); //NFL(x2)
			NFLx2.retainAll(E.get(x2));
			TreeSet<Integer> NFLx1insetNFLx2 = new TreeSet<Integer>(NFLx1); //NFL(x1) intersect NFL(x2)
			NFLx1insetNFLx2.retainAll(NFLx2);
			TreeSet<Integer> Nx1unionNx2 = new TreeSet<Integer>(E.get(x1)); //Nx1 union Nx2
			Nx1unionNx2.addAll(E.get(x2));
			TreeSet<Integer> Nx1unionNx2insectFree = new TreeSet<Integer>(Nx1unionNx2);//Nx1 union Nx2 intersect Free
			Nx1unionNx2insectFree.retainAll(Free);
			if(Nx1unionNx2insectFree.contains(x1))
				Nx1unionNx2insectFree.remove(x1);
			if(Nx1unionNx2insectFree.contains(x2))
				Nx1unionNx2insectFree.remove(x2);
			
			TreeSet<Integer>  Nx1unionNx2insectBN = new TreeSet<Integer>(Nx1unionNx2);//Nx1 union Nx2 intersect BN
			Nx1unionNx2insectBN.retainAll(BN);
			if(Nx1unionNx2insectBN.contains(x1))
				Nx1unionNx2insectBN.remove(x1);
			if(Nx1unionNx2insectBN.contains(x2))
				Nx1unionNx2insectBN.remove(x2);
			//===============================================
			//if(d(x1)=2) then
			//if(get_degree(x1,E,V,IN,LN,BN,FL,Free)==2)
			if(first_deg == 2)
			{
				//Let{z} = N(x1)\{x}
				TreeSet<Integer> Nx1 = new TreeSet<Integer>(E.get(x1));
				Nx1.remove(max_dg_v);
				Integer z = Nx1.first();
				//if z belongs to Free then
				if(Free.contains(z))
				{
					//<v->LN||v->IN , x1->IN||v->IN, x1->LN>
					double n = Math.random();
					if(n <= 1/3)
					{
						LN.add(max_dg_v);
					}
					else if(n <= 2/3)
					{
						IN.add(max_dg_v);
						IN.add(x1);
					}
					else
					{
						IN.add(max_dg_v);
						LN.add(x1);
					}
				}
				//else if z belongs FL then<v->IN>
				else if(FL.contains(z))
					IN.add(max_dg_v);
			}
			//else if(N(x1) intersect N(x2)\FL={v} and for all z beglongs (Nfl(x1) intersect Nfl(x2)),d(z)>=3 )
			else{
				
				if(x1insectx2FLv.size()==0 && check1(NFLx1insetNFLx2,E,V,IN,LN,BN,FL,Free))
				{		
					double n = Math.random();
					//v->LN
					if(n<=0.25)
					{
						LN.add(max_dg_v);
					}
					//v->IN,x1 -> IN
					else if(n<=0.5)
					{
						IN.add(max_dg_v);
						IN.add(x1);
					}
					//v->IN,x1->LN,x2->IN
					else if(n<=0.75)
					{
						IN.add(max_dg_v);
						LN.add(x1);
						IN.add(x2);
					}
					//v->IN,x1->LN,x2->LN,Nfree(x1,x2)->FL,Nfree(x1,x2)->LN
					else
					{
						IN.add(max_dg_v);
						LN.add(x1);
						LN.add(x2);
						FL.addAll(Nx1unionNx2insectFree);
						LN.addAll(Nx1unionNx2insectBN);
					}
				}
			//else if(N(x1) intersect N(x2)\FL!={v})
				else if(x1insectx2FLv.size() != 0)
				{
					double n = Math.random();
					if(n<=1/3)
					{
						LN.add(max_dg_v);
					}
					else if(n<=2/3)
					{
						IN.add(max_dg_v);
						IN.add(x1);
						
					}
					else
					{
						IN.add(max_dg_v);
						LN.add(x1);
						IN.add(x2);
					}
				}
			}
		}
		else if(max_dg==1)
		{
			ArrayList<Integer> P = new ArrayList<Integer>(max_path_Free(max_dg_v,E,V,IN,LN,BN,FL,Free));
			TreeSet<Integer> P_set = new TreeSet<Integer>();
			for(Integer node: P)
				P_set.add(node);
			TreeSet<Integer> Pk_nei_dis_P = new TreeSet<Integer>(E.get(P.get(P.size()-1)));
			Pk_nei_dis_P.removeAll(P_set);
			TreeSet<Integer> Z = Pk_nei_dis_P;
			for(Integer z:Z)
			{
				if(FL.contains(z) && get_degree(z,E,V,IN,LN,BN,FL,Free)==1)
				{
					for(Integer node:P)
						IN.add(node);
					LN.add(z);
				}
				else if(FL.contains(z) && get_degree(z,E,V,IN,LN,BN,FL,Free)>1)
				{
					int i=0;
					for(;i<P.size()-1;i++)
					{
						IN.add(P.get(i));
					}
					LN.add(P.get(i));	
				}
				else if(BN.contains(z))
				{
					LN.add(P.get(0));
				}
				else if(Free.contains(z))
				{
					double n = Math.random();
					if(n<=0.5)
					{
						for(int i=0 ;i<P.size();i++)
						{
							IN.add(P.get(i));
						}
						IN.add(z);
					}
					else
					{
						LN.add(P.get(0));
					}
				}
			}
		}
		return E.size();   //!!!!!!!!!!!!!!!!!!!!!
	}
	public static void main(String[] args)
	{
		// read input file, generate input array
		/*ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
		int num_graphs = read_input(input);
		for(int i=0;i<num_graphs;i++)
			System.out.println(input.get(i));*/
		//=============================================
		
		int num_edges = 0;
		Hashtable<Integer,TreeSet<Integer>> E = new Hashtable<Integer,TreeSet<Integer>>();
		ArrayList<String> test_input = new ArrayList<String>();
		test_input.add("5");
		test_input.add("1 2");
		test_input.add("2 3");
		test_input.add("3 4");
		test_input.add("2 4");
		test_input.add("1 4");
		
		TreeSet<Integer> IN = new TreeSet<Integer>();
		TreeSet<Integer> BN = new TreeSet<Integer>();
		TreeSet<Integer> LN = new TreeSet<Integer>();
		TreeSet<Integer> FL = new TreeSet<Integer>();
		TreeSet<Integer> V = new TreeSet<Integer>();
		initial_EV(E,V,test_input);
		TreeSet<Integer> Free = new TreeSet<Integer>(V);
		int max_edges = 0;
		Hashtable<Integer,TreeSet<Integer>>max_tree;

		for(Integer nv:V)
		{
			num_edges = mlst(nv,E,V,IN,LN,BN,FL,Free);
			if(max_edges<num_edges)
			{
				max_tree = new Hashtable<Integer,TreeSet<Integer>>(E);
				max_edges = num_edges;
			}
			initial_EV(E,V,test_input);
			Free = new TreeSet<Integer>(V);
			IN = new TreeSet<Integer>();
			BN = new TreeSet<Integer>();
			LN = new TreeSet<Integer>();
			FL = new TreeSet<Integer>();
		}
		System.out.println(num_edges);
		
	}

//===============================helper functions====================================
	public static boolean check1(TreeSet<Integer> NFLx1insetNFLx2,Hashtable<Integer,TreeSet<Integer>>E, TreeSet<Integer> V,TreeSet<Integer>IN,TreeSet<Integer>LN,TreeSet<Integer>BN,TreeSet<Integer>FL,TreeSet<Integer>Free)
	{
		if(NFLx1insetNFLx2.size()==0)
		{
			System.out.println("empty CHECK1!!!");
			return false;
		}
		for(Integer z : NFLx1insetNFLx2) //for all z beglongs (Nfl(x1) intersect Nfl(x2)),d(z)>=3
		{
			if(get_degree(z, E,V,IN,LN,BN,FL,Free) < 3)
				return false;
		}
		return true;
	}
	public static ArrayList<Integer> max_path_Free(Integer v,Hashtable<Integer,TreeSet<Integer>>E, TreeSet<Integer> V,TreeSet<Integer>IN,TreeSet<Integer>LN,TreeSet<Integer>BN,TreeSet<Integer>FL,TreeSet<Integer>Free)
	{
		ArrayList<Integer> max_path = new ArrayList<Integer>();
		max_path.add(v);
		//temp_max_path.add(v);
		Integer Nv = E.get(v).first();
		if (Free.contains(Nv) && get_degree(Nv,E,V,IN,LN,BN,FL,Free)==2)
		{
			max_path.add(Nv);
			for(Integer n:E.get(Nv))
			{	
			    if(Free.contains(n) && get_degree(n,E,V,IN,LN,BN,FL,Free)==2)
				{	
			    	max_path.add(n);
					explore1(n,Nv, E,V,IN,LN,BN,FL,Free,max_path);
				}
			}
		}
		return max_path;
	}
	public static void explore1(Integer node,Integer prev_node,Hashtable<Integer,TreeSet<Integer>>E, TreeSet<Integer> V,TreeSet<Integer>IN,TreeSet<Integer>LN,TreeSet<Integer>BN,TreeSet<Integer>FL,TreeSet<Integer>Free,ArrayList<Integer> max_path)
	{
		for(Integer nei: E.get(node))
		{
			if(Free.contains(nei) && get_degree(nei,E,V,IN,LN,BN,FL,Free)==2 && nei!=prev_node)
			{
				max_path.add(nei);
				explore1(nei, node, E,V,IN,LN,BN,FL,Free, max_path);
			}
		}
	}
}