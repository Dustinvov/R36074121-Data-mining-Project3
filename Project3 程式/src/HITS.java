import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.jgrapht.Graph;

public class HITS {
	private Graph<Integer, Edge> graph;
	
	public HITS(Graph<Integer, Edge> g) {
		graph = g;
	}
	
	public HashMap<Integer,HitsCell> runScore() {		
		
		HashMap<Integer,Double> preScoreAuth;
		HashMap<Integer,Double> preScoreHub;
		HashMap<Integer,Double> scoreAuth = new HashMap<Integer,Double>();
		HashMap<Integer,Double> scoreHub = new HashMap<Integer,Double>();
		double diff = 0;
		
		graph.vertexSet().forEach(k->{
			scoreAuth.put(k, 1.0);
			scoreHub.put(k, 1.0);
		});
		
		int step = 0;
		do {
			step++;
			
			preScoreAuth = new HashMap<Integer,Double>(scoreAuth);
			preScoreHub = new HashMap<Integer,Double>(scoreHub);
			
			HashMap<Integer,Double> tempAuthTable = new HashMap<Integer,Double>();
			HashMap<Integer,Double> tempHubTable = new HashMap<Integer,Double>();
			
			for(Integer v : graph.vertexSet()) {
				double tempAuth = 0;
				double tempHub = 0;
				for(Edge e : graph.incomingEdgesOf(v)) {
					tempAuth += preScoreHub.get(e.from);
				}
				for(Edge e : graph.outgoingEdgesOf(v)) {
					tempHub += preScoreAuth.get(e.to);
				}
				tempAuthTable.put(v, tempAuth);
				tempHubTable.put(v, tempHub);
			}
			
			double disTempAuthTable = distence(tempAuthTable.values());
			double disTempHubTable = distence(tempHubTable.values());
			
			for(Integer v : graph.vertexSet()) {
				double tempAuth = tempAuthTable.get(v) / disTempAuthTable;
				double tempHub = tempHubTable.get(v) / disTempHubTable;
				
				scoreAuth.put(v, tempAuth);
				scoreHub.put(v, tempHub);
				
				tempAuthTable.put(v, scoreAuth.get(v) - preScoreAuth.get(v));
				tempHubTable.put(v, scoreHub.get(v) - preScoreHub.get(v));
			}
			
			diff = distence(tempAuthTable.values()) + distence(tempHubTable.values());
			/*
			System.out.println("Step " + step);
			for(Integer v : graph.vertexSet()) {
				System.out.printf("Vertex %d \t auth: %.08f \t hub: %.08f\n",v,scoreAuth.get(v),scoreHub.get(v));
			}
			System.out.println("diff " + diff);
			System.out.println("-----------------------------------");
			*/
		}while( diff > 0.00005 && step <= 100000);
		
		System.out.println("HITS is finished after " + step + " steps.");
		
		HashMap<Integer,HitsCell> resultHashMap = new HashMap<Integer,HitsCell>();
		for(Integer v : graph.vertexSet()) {
			resultHashMap.put(v, new HitsCell(scoreAuth.get(v),scoreHub.get(v)));
		}
		return resultHashMap;
	}
	
	private double distence(Collection<Double> collection) {
		double value = 0;
		for(Double v : collection) {
			value += Math.pow(v, 2);
		}
		return Math.sqrt(value);
	}
}
