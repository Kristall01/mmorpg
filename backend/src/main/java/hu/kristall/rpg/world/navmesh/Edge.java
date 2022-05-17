package hu.kristall.rpg.world.navmesh;

import java.util.Objects;

public class Edge {
	
	public final Vertex end0;
	public final Vertex end1;
	public final String name;
	
	private final int hashCode;
	
	public Edge(Vertex end0, Vertex end1) {
		if(end0.position.compareTo(end1.position) < 0) {
			this.end0 = end0;
			this.end1 = end1;
		}
		else {
			this.end1 = end0;
			this.end0 = end1;
		}
		this.name = end0+"-"+end1;
		
		hashCode = Objects.hash(end0, end1);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Edge edge = (Edge) o;
		return (end0.equals(edge.end0) && end1.equals(edge.end1));
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
}
