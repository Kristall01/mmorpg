package hu.kristall.rpg.world.navmesh;

import hu.kristall.rpg.Position;

public class Rectangle {

	private Position topLeft, bottomRight;
	
	private Vertex[] vertices = new Vertex[4];
	public final String name;
	
	public Rectangle(String name, Vertex[] vertices, Vertex tl, Vertex br) {
		this.name = name;
		this.vertices = vertices;
		this.topLeft = tl.position;
		this.bottomRight = br.position;
	}
	
	public boolean isIn(Position pos) {
		return
			topLeft.getX() <= pos.getX() && pos.getX() <= bottomRight.getX() &&
			topLeft.getY() >= pos.getY() && pos.getY() >= bottomRight.getY();
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public Vertex findVertexClosestTo(Position target) {
		double dist = Double.MAX_VALUE;
		Vertex v = null;
		for (int i = 0; i < 4; i++) {
			Vertex localVertext = vertices[i];
			double localDist = Position.distance(target, localVertext.position);
			if(localDist < dist) {
				dist = localDist;
				v = localVertext;
			}
		}
		return v;
	}

}
