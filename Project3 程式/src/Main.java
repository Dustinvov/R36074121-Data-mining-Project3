import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.jgraph.JGraph;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.io.CSVFormat;
import org.jgrapht.io.CSVImporter;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.VertexProvider;

public class Main {
	public static void main(String[] args) throws FileNotFoundException, ImportException {
		new Main();
	}
	public Main() throws FileNotFoundException, ImportException {
		getTime();
		
		//File file = new File("D:\\文件\\研究所課程\\DATA_MINING\\PROJECT3\\hw3dataset\\graph_6.txt");
		//File file = new File("D:\\文件\\研究所課程\\DATA_MINING\\PROJECT3\\hw3dataset\\fix\\graph_6.txt");
		File file = new File("D:\\文件\\研究所課程\\DATA_MINING\\PROJECT3\\hw3dataset\\test.txt");
		FileReader reader = new FileReader(file);
		
		VertexProvider<Integer> vertexProvider = (label, attributes) -> Integer.parseInt(label);
	    EdgeProvider<Integer, Edge> edgeProvider = (from, to, label, attributes) -> new Edge(from, to, label, attributes);
	    Graph<Integer, Edge> graph = new SimpleDirectedGraph<Integer, Edge>(Edge.class);
	    
		CSVImporter<Integer, Edge> importer = new CSVImporter<Integer, Edge>(vertexProvider,edgeProvider);
		//importer.setFormat(CSVFormat.EDGE_LIST);
		importer.setFormat(CSVFormat.ADJACENCY_LIST);
		importer.importGraph(graph, reader);
		
		System.out.printf("Total Vertex: %d\n",graph.vertexSet().size());
		System.out.printf("Total Edge: %d\n",graph.edgeSet().size());
		
		PageRank PR = new PageRank(graph,0.85);
		HashMap<Integer,Double> scorePR = PR.runScore();
		
		for(Integer v : new HashSet<Integer>(graph.vertexSet())) {
			System.out.printf("Vertex %d:\t %.08f\n", v, scorePR.get(v));
		}
		
		
		
		HITS hits = new HITS(graph);
		HashMap<Integer,HitsCell> scoreHits = hits.runScore();
		
		for(Integer v : new HashSet<Integer>(graph.vertexSet())) {
			System.out.printf("Vertex %d \t auth: %.08f \t hub: %.08f\n",v,scoreHits.get(v).auth,scoreHits.get(v).hub);
		}
		
		
		SimRank sim = new SimRank(graph);
		HashMap<Point,Double> scoreSimRank = sim.runScore();
		for(Entry<Point, Double> v : scoreSimRank.entrySet()) {
			System.out.printf("Vertex %d\t%d\t%.08f\n",v.getKey().x,v.getKey().y,v.getValue());
		}
		/*
		SimRankLike simLike = new SimRankLike(graph);
		HashMap<Point,Double> scoreSimRankLike = simLike.runScore();
		for(Entry<Point, Double> v : scoreSimRankLike.entrySet()) {
			System.out.printf("Vertex %d\t%d\t%.08f\n",v.getKey().x,v.getKey().y,v.getValue());
		}
		*/
		/*
		SimRankFast simLikeFast = new SimRankFast(graph);
		/*HashMap<Point,Double> scoreSimRankFast = simLikeFast.runScore();
		for(Entry<Point, Double> v : scoreSimRankFast.entrySet()) {
			System.out.printf("Vertex %d\t%d\t%.08f\n",v.getKey().x,v.getKey().y,v.getValue());
		}*/
		/*
		int x = 295;
		int y = 1200;
		System.out.printf("Vertex %d\t%d\t%.08f\n",x,y,simLikeFast.calculateSinglePair(x, y));
		*/
		getTime();
	}
	
	public static void getTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.n");
		LocalDateTime now = LocalDateTime.now();
		System.out.println(dtf.format(now));
	}
}
