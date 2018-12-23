import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.jgrapht.Graph;

public class SimRankFast {
	private Graph<Integer, Edge> graph;
	private HashMap<Point,Double> scoreMap;
	private double dampingFactor;
	private int maxStep;
	private int maxWalker;
	
	Edge[][] incomingEdgeArr;
	int[] incomingEdgeNumArr;
	Integer[] vertexArr;
	public SimRankFast(Graph<Integer, Edge> g, double c, int s, int w) {
		graph = g;
		dampingFactor = c;
		scoreMap = new HashMap<Point,Double>();
		maxStep = s;
		maxWalker = w;
		
		Set<Integer> vertexSet = graph.vertexSet();
		
		int vertexNum = vertexSet.size();
		vertexArr = new Integer[vertexNum];
		vertexSet.toArray(vertexArr);
		Arrays.sort(vertexArr);
		
		incomingEdgeArr = new Edge[vertexNum + 1][];
		incomingEdgeNumArr = new int[vertexNum + 1];
		for(int i = 0 ; i < vertexNum ; i++) {
			Set<Edge> incomingEdge = graph.incomingEdgesOf(vertexArr[i]);
			int sizeInbound = incomingEdge.size();
			incomingEdgeNumArr[i] = sizeInbound;
			if(incomingEdgeNumArr[i] != 0) {
				incomingEdgeArr[i] = new Edge[sizeInbound];
				incomingEdge.toArray(incomingEdgeArr[i]);
			}
			//System.out.printf("sizeInbound for %d:%d\n",vertexArr[i],incomingEdgeNumArr[i]);
		}
	}
	public SimRankFast(Graph<Integer, Edge> g) {
		this(g,0.8,100000,500);
	}
	
	public HashMap<Point,Double> runScore() {
		Set<Integer> vertexSet = graph.vertexSet();
		int vertexNum = vertexSet.size();
		for(int i = 0 ; i < vertexNum ; i++) {
		for(int j = i ; j < vertexNum ; j++) {
			int tempA = vertexArr[i];
			int tempB = vertexArr[j];
			
			double value = calculateSinglePair(tempA,tempB);
			
			Point locate = new Point(tempA,tempB);
			scoreMap.put(locate, value);
		}
		}
		
		return scoreMap;
	}
	
	public double calculateSinglePair(int tempA,int tempB) {
		int walkerAIndex = Arrays.binarySearch(vertexArr, tempA);
		int walkerBIndex = Arrays.binarySearch(vertexArr, tempB);
		//System.out.printf("Testing %d and %d \n",tempA,tempB);
		
		
		if(tempA == tempB) {
			//System.out.printf("Same, score 1.0\n");
			return 1.0;
		}else if(incomingEdgeNumArr[walkerAIndex] == 0 || incomingEdgeNumArr[walkerBIndex] == 0) {
			//System.out.printf("No parents, score 0.0\n");
			return 0.0;
		}
		
		double value = 0;
		int successStep = 0;
		for(int tempStep = 1 ; tempStep <= maxStep ; tempStep++) {
			//System.out.printf("Step %d \n",tempStep);
			int tempWalker = 0;
			int[] walker = new int[] {tempA,tempB};
			Random rn = new Random();
			while(walker[0] != walker[1] && tempWalker <= maxWalker) {
				for(int k = 0; k <= 1; k++) {
					int walkerIndex = Arrays.binarySearch(vertexArr, walker[k]);
					if(incomingEdgeNumArr[walkerIndex] == 0) {
						tempWalker = maxWalker + 1;
						break;
					}
					int nextPath = rn.nextInt(incomingEdgeNumArr[walkerIndex]);
					walker[k] = incomingEdgeArr[walkerIndex][nextPath].from;
					//System.out.printf("walker[%d] -> %d \n",k,walker[k]);
				}
				tempWalker++;
			}
			if(tempWalker <= maxWalker) {
				value += Math.pow(dampingFactor, tempWalker);
			}
			
		}
		
		
		return value / maxStep;
	}
}