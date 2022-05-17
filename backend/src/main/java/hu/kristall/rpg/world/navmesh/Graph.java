package hu.kristall.rpg.world.navmesh;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hu.kristall.rpg.Position;
import hu.kristall.rpg.Utils;

import java.util.*;

public class Graph {

	private Map<String, Vertex> vertices = new HashMap<>();
	private Map<String, Polygon> polygons = new HashMap<>();
	
	public Graph(JsonObject object) {
		JsonObject jsonVertices = object.get("vertices").getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : jsonVertices.entrySet()) {
			this.vertices.put(entry.getKey(), new Vertex(Utils.gson().fromJson(entry.getValue(), Position.class), entry.getKey()));
		}
		
		/*JsonObject jsonEdges = object.get("edges").getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : jsonEdges.entrySet()) {
			Vertex[] vertices1 = new Vertex[2];
			JsonArray edgeIDS = entry.getValue().getAsJsonArray();
			for (int i = 0; i < 2; i++) {
				vertices1[i] = this.vertices.get(edgeIDS.get(i).getAsString());
			}
			Edge edge = new Edge(vertices1[0], vertices1[1], entry.getKey());
			this.edges.put(entry.getKey(), edge);
			for (int i = 0; i < 2; i++) {
				try {
					vertices1[i].addEdge(edge);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		*/
		
		JsonObject jsonPolygons = object.get("polygons").getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : jsonPolygons.entrySet()) {
			JsonArray polygonJsonVertices = entry.getValue().getAsJsonArray();
			Vertex[] v = new Vertex[polygonJsonVertices.size()];
			int index = -1;
			for (JsonElement vertex : polygonJsonVertices) {
				v[++index] = vertices.get(vertex.getAsString());
			}
			for (int i = 0; i < v.length-1; i++) {
				Edge e = new Edge(v[i], v[i+1]);
				v[i].addEdge(e);
				v[i+1].addEdge(e);
			}
			Edge sideEdge = new Edge(v[0], v[v.length-1]);
			v[0].addEdge(sideEdge);
			v[v.length-1].addEdge(sideEdge);
			polygons.put(entry.getKey(), new Polygon(entry.getKey(), v));
		}
		for (Vertex value : vertices.values()) {
			value.bakeNeighbourVerticies();
		}
		
	}
	
	private Polygon getPolygon(Position pos) {
		for (Polygon value : polygons.values()) {
			if(value.isIn(pos)) {
				return value;
			}
		}
		return null;
	}
	
	private boolean hasLineOfSight(Position pos0, Position pos1) {
		Polygon fromRectangle = getPolygon(pos0);
		if(fromRectangle == null) {
			return false;
		}
		return fromRectangle.isIn(pos1);
		//oversimplified
	}
	
	public List<Position> findPath(Position from, Position to) {
		//RESET FIELDS
		for (Vertex value : vertices.values()) {
			value.reset();
		}
		Polygon fromPolygon = null, toPolygon = null;
		for (Polygon value : polygons.values()) {
			if(value.isIn(from)) {
				fromPolygon = value;
				break;
			}
		}
		if(toPolygon == null) {
			throw new IllegalArgumentException("from position is out of the map.");
		}
		for (Polygon value : polygons.values()) {
			if(value.isIn(to)) {
				toPolygon = value;
				break;
			}
		}
		if(toPolygon == null) {
			throw new IllegalArgumentException("to position is out of the map.");
		}
		
		if(fromPolygon.equals(toPolygon)) {
			return List.of(from, to);
		}
		
		Queue<Vertex> openList = new PriorityQueue<>();
		
		Vertex startVertex = fromPolygon.findVertexClosestTo(to);
		Vertex endVertext = toPolygon.findVertexClosestTo(from);
		
		startVertex.setOpen(true);
		startVertex.setG(0);
		startVertex.setH(endVertext.position);
		openList.add(startVertex);
		
		while (!openList.isEmpty()) {
			Vertex node = openList.poll();
			node.setOpen(false);
			if(node.equals(endVertext)) {
				//found target
				LinkedList<Position> positionChain = new LinkedList<>();
				Vertex reverseLink = node;
				while (reverseLink != null) {
					positionChain.addFirst(reverseLink.position);
					reverseLink = reverseLink.getParent();
				}
				positionChain.addFirst(from);
				positionChain.addLast(to);
				LinkedList<Integer> removeIndexes = new LinkedList();
				Position prev = positionChain.get(0);
				for (int i = 1; i < positionChain.size()-1; i++) {
					Position next = positionChain.get(i+1);
					if(hasLineOfSight(prev, next)) {
						removeIndexes.addFirst(i);
					}
					else {
						prev = positionChain.get(i);
					}
				}
				for (Integer removeIndex : removeIndexes) {
					positionChain.remove((int)removeIndex);
				}
				return positionChain;
			}
			for (Vertex childNode : node.getNeighbourVerticies()) {
				double gCost = node.getG() + Position.distance(node.position, childNode.position);
				if(gCost < childNode.getG()) {
					childNode.setParent(node);
					childNode.setG(gCost);
					if(childNode.isOpen()) {
						openList.remove(childNode);
					}
					childNode.setOpen(true);
					openList.add(childNode);
				}
			}
		}
		return null;
	}

}
