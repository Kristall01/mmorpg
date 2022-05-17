package hu.kristall.rpg.world.path.plan;

import com.google.gson.JsonObject;
import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.navmesh.Graph;
import hu.kristall.rpg.world.path.Path;

public class NavmeshPathfinder implements PathFinder {
	
	private Graph graph;
	
	public NavmeshPathfinder(JsonObject base) {
		graph = new Graph(base);
	}
	
	@Override
	public Path findPath(Position from, Position to, double cellsPerSec, long startTimeNanos) {
		return PathFinder.generatePath(graph.findPath(from, to), startTimeNanos, cellsPerSec);
	}
	
}
