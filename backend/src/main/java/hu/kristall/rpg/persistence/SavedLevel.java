package hu.kristall.rpg.persistence;

import com.google.gson.*;
import hu.kristall.rpg.Position;
import hu.kristall.rpg.Utils;
import hu.kristall.rpg.world.grid.SearchGrid;
import hu.kristall.rpg.world.path.plan.AStarPathFinder;
import hu.kristall.rpg.world.path.plan.FreePathFinder;
import hu.kristall.rpg.world.path.plan.PathFinder;
import hu.kristall.rpg.world.path.plan.ReducedPathFinder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SavedLevel {

	public String name;
	public final int width, height;
	public final String[] layers;
	public final List<SavedMonsterspawn> monsterspawns;
	public final List<SavedPortal> portals;
	public PathFinder pathFinder;
	
	public SavedLevel(String name, int width, int height, String[] layers, List<SavedMonsterspawn> monsterspawns, List<SavedPortal> portals, PathFinder pathFinder) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.layers = layers;
		this.monsterspawns = List.copyOf(monsterspawns);
		this.portals = List.copyOf(portals);
		this.pathFinder = pathFinder;
	}
	
	public static class SavedLevelParser implements JsonDeserializer<SavedLevel> {
		
		@Override
		public SavedLevel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
			try {
				JsonObject base = jsonElement.getAsJsonObject();
				int width = base.get("width").getAsInt();
				int height = base.get("height").getAsInt();
				ArrayList<SavedPortal> portals = new ArrayList<>();
				for (JsonElement portalJson : base.get("portals").getAsJsonArray()) {
					portals.add(ctx.deserialize(portalJson, SavedPortal.class));
				}
				//List<SavedMonsterspawn> monsters = Utils.mapJsonArray(base.get("monsterspawns"), SavedMonsterspawn.class, ctx);
				List<String> layers = Utils.mapJsonArray(base.get("layers").getAsJsonArray(), String.class, ctx);
				String pathType = base.get("type").getAsString();
				PathFinder pathFinder;
				if(pathType.equals("free")) {
					pathFinder = new FreePathFinder();
				}
				else if(pathType.equals("reduced")) {
					pathFinder = new ReducedPathFinder(Utils.gson().fromJson(base.get("min"), Position.class), Utils.gson().fromJson(base.get("max"), Position.class));
				}
				else if(pathType.equals("astar")) {
					int wallsSize = width*height;
					JsonArray arr = base.get("walls").getAsJsonArray();
					boolean[][] walls = new boolean[height][width];
					for (int i = 0; i < arr.size(); i++) {
						walls[i/width][i%width] = arr.get(i).getAsBoolean();
					}
					pathFinder = new AStarPathFinder(new SearchGrid(walls, width, height));
				}
				else {
					pathFinder = new FreePathFinder();
				}
				return new SavedLevel(null,width, height, layers.toArray(new String[0]),  new ArrayList<>(), portals, pathFinder);
			}
			catch (Throwable err) {
				err.printStackTrace();
				throw err;
			}
		}
	}
	
}
