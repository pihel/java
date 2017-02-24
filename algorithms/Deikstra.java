package algorithms;

import java.util.*;

public class Deikstra {
	// список смежности + веса
	/*class GraphList {
		// точка
		int pnt;

		// смежные точки
		ArrayList<Integer> pnts;
		// смежные веса ребер: точка - вес
		HashMap<Integer, Integer> rbr;

		public GraphList(int point, ArrayList p, HashMap r) {
			pnt = point;
			rbr = r;
			pnts = p;
		} // GraphList
	} // GraphList
	*/
	
	class Rebro {
		Integer to_point;
		Double weight;
		
		public Rebro(Integer p, Double w) {
			to_point = p;
			weight = w;
		} //Rebro
		
		void dump() {
			System.out.println( " -> " + to_point + " (" + weight + ")");
		} //dump
	} //Rebro

	//точка откуда, точка куда, вес ребра
	//graph[откуда][].куда (вес)
	HashMap<Integer, ArrayList<Rebro>> graph;

	public Deikstra() {
		graph = new HashMap<Integer, ArrayList<Rebro>>();
	} // Deikstra
	
	void add(Integer from_point, Integer to_point, Double weight) {
		if(!graph.containsKey(from_point)) {
			ArrayList<Rebro> rbr = new ArrayList<Rebro>();
			graph.put(from_point, rbr);
			graph.get(from_point).add(new Rebro(from_point, 0.0));
		}
		graph.get(from_point).add(new Rebro(to_point, weight));
	} //add
	
	void getShortestWay(Integer from_point, Integer to_point) {
		Integer pnt = from_point;
		
		//TODO
		/*while(pnt != to_point) {
			System.out.print(pnt);
			graph.get(pnt).get(index)
		}*/
	} //getShortestWay
	
	void dump() {
		for(Map.Entry<Integer, ArrayList<Rebro>> entry : graph.entrySet()) {
			Integer from_point = entry.getKey();
			ArrayList<Rebro> rbr_arr = entry.getValue();
			
			System.out.println(from_point);
			
			for (Rebro rbr: rbr_arr) {
				if(rbr.to_point != from_point) {
					rbr.dump();
				}
			}			
			
			System.out.println(" ");
		} //for
	} //dump

	public static void main(String[] args) {
		Deikstra d = new Deikstra();
		d.add(1, 2, 7.0);
		d.add(1, 3, 12.0);
		
		d.add(2, 3, 4.0);
		d.add(2, 4, 15.0);
		
		d.add(3, 5, 5.0);
		
		d.add(4, 6, 3.0);
		
		d.add(5, 6, 11.0);
		d.add(5, 4, 1.0);
		
		//d.dump();
		
		d.getShortestWay(1, 6);
	}

}