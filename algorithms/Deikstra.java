package algorithms;

import java.util.*;

public class Deikstra {	
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
	
	class Distance {
		Double dist;
		boolean processed;
		
		public Distance() {
			dist = Double.MAX_VALUE;
			processed = false;
		} //Distance
	} //Distance

	//точка откуда, точка куда, вес ребра
	//graph[откуда][].куда (вес)
	ArrayList<ArrayList<Rebro>> graph;
	
	//расстояние до рассчитываемой точки
	Distance distance[];

	public Deikstra() {
		graph = new ArrayList<ArrayList<Rebro>>();
	} // Deikstra
	
	void add(Integer from_point, Integer to_point, Double weight) {
		if( graph.size() <= from_point ) {
			ArrayList<Rebro> rbr = new ArrayList<Rebro>();
			graph.add(from_point, rbr);
		}
		graph.get(from_point).add(new Rebro(to_point, weight));
	} //add
	
	void getShortestWay(Integer from_point, Integer to_point) {
		//добавим последнюю точку 1 раз
		if(graph.get(graph.size()-1).size() > 0) {
			graph.add(new ArrayList<Rebro>());
		}
		
		//заполним все точки как недостижимые
		distance = new Distance[graph.size()];
		for(int i = 0; i < distance.length; i++) {
			distance[i] = new Distance(); 
		}
		//растояние до начальной точки = 0
		distance[from_point].dist = 0.0;
		
		//запускаем бесконечный цикл
		Integer point = from_point;
		while( true ) {
			
			//найдем точку с минимальным расстоянием из необработанных (для первой итерации - это начальная точка)
			point = -1;
			Double min_distance = Double.MAX_VALUE;
			//обходим все точки из необработанного множества (processed)
			for(int p = 0; p < distance.length; p++) {
				//выбираем с наименьшей дистанцией
				if(!distance[p].processed && distance[p].dist < min_distance) {
					min_distance = distance[p].dist;
					point = p;
				}
			}
			//если пути дальше нет, то выходим
			if(point < 0) break;
			
			//получаем все исходящии ребра
			ArrayList<Rebro> rbrs = graph.get(point);
			//обходим их
			for(int r = 0; r < rbrs.size(); r++) {
				//если текущая дистация до соседней точки > дистанция до текущей точки + длина ребра , то обновляем дистанцию
				if(distance[rbrs.get(r).to_point].dist > ( distance[point].dist + rbrs.get(r).weight ) ) {
					distance[rbrs.get(r).to_point].dist =  distance[point].dist + rbrs.get(r).weight;
				}
			}
			
			//минимальная точка обработана
			distance[point].processed = true;
		}
		
		//print
		point = from_point;
		Integer min_point = point;
		
		do {
			point = min_point;
			
			System.out.print(point + " (" + distance[point].dist + ") -> ");
			
			Double min_dist = Double.MAX_VALUE;
			
			for(int r = 0; r < graph.get(point).size(); r++ ) {
				if( distance[ graph.get(point).get(r).to_point ].dist  < min_dist ) {
					min_point = graph.get(point).get(r).to_point;
					min_dist = distance[min_point].dist;
				} //if
			} //for	
		} while(point < to_point && point != min_point); //while
		System.out.println("\r\n");
		
	} //getShortestWay
	
	void dump() {
		System.out.println("-------------");
		for(int p = 0; p < graph.size(); p++) {
			
			if(distance.length > 0) {
				System.out.println(p + " (" + distance[p].dist + ")");
			} else {
				System.out.println(p);
			}
			
			for (Rebro rbr: graph.get(p)) {
				rbr.dump();
			}			
			
			System.out.println(" ");
		} //for
		System.out.println("-------------");
	} //dump

	public static void main(String[] args) {
		Deikstra d = new Deikstra();
		d.add(0, 1, 7.0);
		d.add(0, 2, 12.0);
		
		d.add(1, 2, 4.0);
		d.add(1, 3, 15.0);
		
		d.add(2, 4, 5.0);
		
		d.add(3, 5, 3.0);
		
		d.add(4, 5, 11.0);
		d.add(4, 3, 1.0);
		
		d.getShortestWay(0, 5);
		
		d.dump();
	}

}