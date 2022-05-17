package hu.kristall.rpg.world.navmesh;

import hu.kristall.rpg.Position;

public class Polygon {

	private Vertex[] vertices;
	public final String name;
	private Vector[] vectors;
	
	public Polygon(String name, Vertex[] vertices) {
		this.name = name;
		this.vertices = vertices;
		if(vertices.length < 3) {
			throw new IllegalArgumentException("at least verticies must be given for a polygon");
		}
		vectors = new Vector[vertices.length];
		int index = 0;
		for (; index < vertices.length-2; index++) {
			vectors[index] = new Vector(vertices[index].position, vertices[index+1].position);
		}
		vectors[index] = new Vector(vertices[index].position, vertices[0].position);
		/*double sum = 0;
		int i = 1;
		for (; i < vertices.length-1; i++) {
			sum += Math.PI - (Position.angleBetween(vertices[i-1].position, vertices[i].position, vertices[i+1].position));
		}
		sum += Math.PI - Position.angleBetween(vertices[i-1].position, vertices[i].position, vertices[0].position);
		sum += Math.PI - Position.angleBetween(vertices[i].position, vertices[0].position, vertices[1].position);
		System.out.println("sum: "+Math.toDegrees(sum));
		//every vertex must be convex
		
		//TODO check vertex checks: count rounds, convexity
		*/
	}
	
	public boolean isIn(Position pos) {
		return true; //TODO implement
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public Vertex findVertexClosestTo(Position target) {
		double dist = Double.MAX_VALUE;
		Vertex v = null;
		for (int i = 0; i < 4; i++) {
			Vertex localVertex = vertices[i];
			double localDist = Position.distance(target, localVertex.position);
			if(localDist < dist) {
				dist = localDist;
				v = localVertex;
			}
		}
		return v;
	}
	
	private static Vector createVector(Position from, Position to) {
		if(from.getX() == to.getX()) {
			double x = from.getX();
			if(from.getY() < to.getY()) {
				return new PositiveVerticalVector(x);
			}
			else {
				return new NegativeVerticalVector(x);
			}
		}
		return new FunctionVector(from, to);
	}
	
	private static class Vector {
		
		double x, y;
		
		public Vector(Position from, Position to) {
			this.x = to.getX() - from.getX();
			this.y = to.getY() - from.getY();
		}
		
		public boolean isRightIn(Position subject) {
			if(x == 0) {
				return x <= subject.getX();
				if(y > 0) {
					return x <= subject.getX();
				}
				else {
					return x >= subject.getX();
				}
			}
			double
			/*if(y == 0) {
				if(x > 0) {
					return subject.getY() <= y;
				}
				else {
					return subject.getY() >= y;
				}
			}*/
			if(x > 0) {
				return subject.getX()
			}
		}
		
	}

}
