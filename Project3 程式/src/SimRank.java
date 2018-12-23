import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.jgrapht.Graph;

public class SimRank {
	private Graph<Integer, Edge> graph;
	private HashMap<Point,Double> scoreMap;
	private double dampingFactor;
	public SimRank(Graph<Integer, Edge> g, double c) {
		graph = g;
		dampingFactor = c;
		scoreMap = new HashMap<Point,Double>();
	}
	public SimRank(Graph<Integer, Edge> g) {
		this(g,0.8);
	}
	
	public HashMap<Point,Double> runScore() {
		Set<Integer> vertexSet = graph.vertexSet();
		
		int vertexNum = vertexSet.size();
		Integer[] vertexArr = new Integer[vertexNum];
		vertexSet.toArray(vertexArr);
		
		for(int i = 0 ; i < vertexNum ; i++) {
			for(int j = i ; j < vertexNum ; j++) {
				int tempA = vertexArr[i];
				int tempB = vertexArr[j];
				if(tempA > tempB) {
					int tempPointer = tempA;
					tempA = tempB;
					tempB = tempPointer;
				}
				double tempValue = (tempA == tempB) ? 1.0 : 0.0;
				scoreMap.put(new Point(tempA,tempB), tempValue);
			}
		}
		
		double diff = 0;
		int step = 0;
		do {
			step++;
			//System.out.printf("Step %d\n",step);
			HashMap<Point,Double> preScoreMap = new HashMap<Point,Double>(scoreMap);
			HashMap<Point,Double> diffScoreMap = new HashMap<Point,Double>();
			for(int i = 0 ; i < vertexNum ; i++) {
			for(int j = i + 1 ; j < vertexNum ; j++) {
				int tempA = vertexArr[i];
				int tempB = vertexArr[j];
				if(tempA > tempB) {
					int tempPointer = tempA;
					tempA = tempB;
					tempB = tempPointer;
				}
				//System.out.printf("calculating %d and %d\n",tempA,tempB);
				
				Set<Edge> incomingEdgeA = graph.incomingEdgesOf(tempA);
				Set<Edge> incomingEdgeB = graph.incomingEdgesOf(tempB);
				
				
				double tempScore = 0;
				if(incomingEdgeA.size() == 0 || incomingEdgeB.size() == 0) {
					tempScore = 0;
				}else {
					for(Edge eA : incomingEdgeA) {
					for(Edge eB : incomingEdgeB) {
						int vertexFromA = eA.from;
						int vertexFromB = eB.from;
						if(vertexFromA > vertexFromB) {
							int tempPointer = vertexFromA;
							vertexFromA = vertexFromB;
							vertexFromB = tempPointer;
						}
						//System.out.printf("%d to %d ",vertexFromA,vertexFromB);
						double preScore = preScoreMap.get(new Point(vertexFromA,vertexFromB));
						//System.out.printf("score is %.8f\n",preScore);
						tempScore += preScore;
					}
					}
					tempScore = dampingFactor / (incomingEdgeA.size() * incomingEdgeB.size()) * tempScore;
				}
				
				//System.out.printf("Score of %d and %d is %.8f\n",tempA,tempB,tempScore);
				
				Point locate = new Point(tempA,tempB);
				scoreMap.put(locate, tempScore);
				double diffvalue = tempScore - preScoreMap.get(locate);
				diffScoreMap.put(locate, diffvalue);
			}
			}
			
			diff = distence(diffScoreMap.values());
		}while(diff > 0.00005 && step <= 100000);
		
		System.out.println("SimRank is finished after " + step + " steps.");
		return scoreMap;
	}
	
	private double distence(Collection<Double> collection) {
		double value = 0;
		for(Double v : collection) {
			value += Math.pow(v, 2);
		}
		return Math.sqrt(value);
	}
	
}

