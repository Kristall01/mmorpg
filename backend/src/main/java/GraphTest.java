import com.google.gson.JsonObject;
import hu.kristall.rpg.Position;
import hu.kristall.rpg.Utils;
import hu.kristall.rpg.world.navmesh.Graph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

public class GraphTest {
	
	static Random r = new Random();
	
	static double r() {
		return (r.nextDouble()*2-1);
	}
	
	public static void main(String[] args) throws IOException {
		String s = Files.readString(new File("/home/dominik/graph.json").toPath());
		Graph g = new Graph(Utils.gson().fromJson(s, JsonObject.class));
		//Object o = g.findPath(new Position(-3, -1), new Position(6.5,1.5));
		//System.out.println(Utils.gson().toJson(o));
	}
	
}
