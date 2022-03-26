package hu.kristall.rpg;

public class GeneratedID<T> {
	
	public final int value;
	
	public GeneratedID(int value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof GeneratedID)) return false;
		GeneratedID<?> that = (GeneratedID<?>) o;
		return value == that.value;
	}
	
	@Override
	public int hashCode() {
		return value;
	}
	
}
