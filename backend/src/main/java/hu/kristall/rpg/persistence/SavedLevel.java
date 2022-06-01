package hu.kristall.rpg.persistence;

import com.google.gson.*;
import hu.kristall.rpg.Position;
import hu.kristall.rpg.Utils;
import hu.kristall.rpg.world.EntitySpawner;
import hu.kristall.rpg.world.entity.EntityType;
import hu.kristall.rpg.world.grid.SearchGrid;
import hu.kristall.rpg.world.path.plan.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SavedLevel {

	public String name;
	public final int width, height;
	public final String[] layers;
	public final List<EntitySpawner> entitySpawners;
	public final List<SavedPortal> portals;
	public PathFinder pathFinder;
	public Position spawnPosition;
	
	public SavedLevel(String name, int width, int height, String[] layers, List<EntitySpawner> entitySpawners, List<SavedPortal> portals, PathFinder pathFinder, Position spawnPosition) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.layers = layers;
		this.entitySpawners = Collections.unmodifiableList(entitySpawners);
		this.portals = List.copyOf(portals);
		this.pathFinder = pathFinder;
		this.spawnPosition = spawnPosition;
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
				else if(pathType.equals("astar") || pathType.equals("thetastar")) {
					JsonArray arr = base.get("walls").getAsJsonArray();
					boolean[][] walls = new boolean[height][width];
					for (int i = 0; i < arr.size(); i++) {
						walls[i/width][i%width] = arr.get(i).getAsBoolean();
					}
					if(pathType.equals("astar")) {
						pathFinder = new AStarPathFinder(new SearchGrid(walls, width, height));
					}
					else {
						pathFinder = new ThetaStarPathFinder(new SearchGrid(walls, width, height));
					}
				}
				else {
					pathFinder = new FreePathFinder();
				}
				JsonArray spawners = base.get("spawners").getAsJsonArray();
				List<EntitySpawner> entitySpawners = new ArrayList<>();
				for (JsonElement spawnerElement : spawners) {
					JsonObject spawner = spawnerElement.getAsJsonObject();
					String entityType = spawner.get("entityType").getAsString();
					int count = spawner.get("count").getAsInt();
					long respawnTimer = spawner.get("respawnTimer").getAsLong();
					Position topLeft = Utils.gson().fromJson(spawner.get("topLeft"), Position.class);
					Position bottomRight = Utils.gson().fromJson(spawner.get("bottomRight"), Position.class);
					entitySpawners.add(new EntitySpawner(count,respawnTimer, EntityType.valueOf(entityType), topLeft, bottomRight));
				}
				Position spawnPosition = Utils.gson().fromJson(base.get("spawnPosition"), Position.class);
				return new SavedLevel(null,width, height, layers.toArray(new String[0]), entitySpawners, portals, pathFinder, spawnPosition);
			}
			catch (Throwable err) {
				err.printStackTrace();
				throw err;
			}
		}
	}
	
}
