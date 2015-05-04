package geneticsHomework;

public class Gene {

	private GeneType type;
	private boolean value;
	
	public Gene(GeneType type, boolean value) {
		this.type = type;
		this.value = value;
	}
	
	public GeneType getType() {
		return type;
	}
	
	public boolean getValue() {
		return value;
	}
}
