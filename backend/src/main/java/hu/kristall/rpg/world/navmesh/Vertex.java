package hu.kristall.rpg.world.navmesh;

import hu.kristall.rpg.Position;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Vertex implements Comparable<Vertex> {

	//distance to target
	private double H = 0;
	
	//distance from start
	private double G = 0;
	//SUM
	private double F = 0;
	private Vertex parent;
	private boolean open = false;
	
	public void setParent(Vertex parent) {
		this.parent = parent;
	}
	
	public Vertex getParent() {
		return parent;
	}
	
	public void setOpen(boolean value) {
		this.open = value;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	@Override
	public int compareTo(@NotNull Vertex vertex) {
		return Double.compare(F, vertex.F);
	}
	
	public void setG(double cost) {
		this.G = cost;
		updateF();
	}
	
	public void reset() {
		parent = null;
		G = Double.MAX_VALUE;
		F = Double.MAX_VALUE;
		H = 0;
	}
	
	public void setH(Position target) {
		this.H = Position.distance(this.position, target);
		updateF();
	}
	
	public double getG() {
		return G;
	}
	
	private void updateF() {
		F = G + H;
	}
	
	///////////////////////////////////////////////////////
	
	
	public final Position position;
	public final String name;
	private Vertex[] neighbourVerticies;
	
	private Set<Edge> edges = new HashSet<>();
	
	public Vertex(Position position, String name) {
		this.position = position;
		this.name = name;
	}
	
	public void addEdge(Edge edge) {
		this.edges.add(edge);
	}
	
	public void bakeNeighbourVerticies() {
		int i = -1;
		neighbourVerticies = new Vertex[edges.size()];
		for (Edge edge : edges) {
			Vertex v = edge.end0;
			if(v.equals(this)) {
				v = edge.end1;
			}
			neighbourVerticies[++i] = v;
		}
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public Vertex[] getNeighbourVerticies() {
		return neighbourVerticies;
	}
	
}
