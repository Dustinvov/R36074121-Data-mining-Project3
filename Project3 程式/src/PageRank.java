import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.jgrapht.Graph;

public class PageRank {
	private Graph<Integer, Edge> graph;
	private double dampingFactor;
	
	public PageRank(Graph<Integer, Edge> g, double d) {
		graph = g;
		dampingFactor = d;
	}
	
	public PageRank(Graph<Integer, Edge> g) {
		this(g, 0.85);
	}
	
	public HashMap<Integer,Double> runScore() {
		int vSize = graph.vertexSet().size();
		
		double diff;
		HashMap<Integer,Double> preScore;
		HashMap<Integer,Double> score = new HashMap<Integer,Double>();
		
		graph.vertexSet().forEach(
			k->score.put(k, 1.0 / vSize)
		);
		
		int step = 0;
		do {
			step++;
			
			preScore = new HashMap<Integer,Double>(score);
			HashMap<Integer,Double> tempDiff = new HashMap<Integer,Double>();
			
			final double DAMPING_SCORE = (1.0 - dampingFactor) / vSize;
			for(Integer k : graph.vertexSet()) {
				double tempScore = 0;
				for(Edge j : graph.incomingEdgesOf(k)) {
					tempScore += preScore.get(j.from) / graph.outDegreeOf(j.from);
				}
				tempScore = DAMPING_SCORE + dampingFactor * tempScore;
				score.put(k, tempScore); 
				tempDiff.put(k, score.get(k) - preScore.get(k));
			}
			diff = distence(tempDiff.values());
			/*
			System.out.println("Step " + step);
			for(Integer v : new HashSet<Integer>(graph.vertexSet())) {
				System.out.println("Vertex " + v + " : " + score.get(v));
			}
			System.out.println("Diff:" + diff);
			System.out.println("-----------------------------------");
			*/
		}while( diff > 0.00005 && step <= 100000);
		
		System.out.println("PageRank is finished after " + step + " steps.");
		return score;
	}
	
	private double distence(Collection<Double> collection) {
		double value = 0;
		for(Double v : collection) {
			value += Math.pow(v, 2);
		}
		return Math.sqrt(value);
	}
}
