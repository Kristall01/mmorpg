package hu.kristall.rpg.persistence;

import com.google.gson.*;
import hu.kristall.rpg.Position;

import java.lang.reflect.Type;

public class SavedPortal {
	
	public final Position position;
	public final String targetWorld;
	public final Position targetPosition;
	
	public SavedPortal(Position position, String targetWorld, Position targetPosition) {
		this.position = position;
		this.targetWorld = targetWorld;
		this.targetPosition = targetPosition;
	}
	
	public static class SavedPortalParser implements JsonDeserializer<SavedPortal> {
		
		@Override
		public SavedPortal deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
			try {
				JsonObject base = jsonElement.getAsJsonObject();
				Position pos = ctx.deserialize(base.get("position"), Position.class);
				String target = base.get("targetWorld").getAsString();
				Position targetPosition = ctx.deserialize(base.get("targetPosition"), Position.class);
				return new SavedPortal(pos, target, targetPosition);
			}
			catch (Throwable err) {
				err.printStackTrace();
				throw err;
			}
		}
	}
	
}
