package hu.kristall.rpg.persistence;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hu.kristall.rpg.Position;

import java.lang.reflect.Type;

public class Parsers {
	
	public static JsonDeserializer<Position> positionParser = (JsonElement jsonElement, Type type, JsonDeserializationContext ctx) -> {
		JsonObject base = jsonElement.getAsJsonObject();
		return new Position(base.get("x").getAsDouble(), base.get("y").getAsDouble());
	};
	
	
}
