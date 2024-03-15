package application;

public class TextFormat extends Format {

	@Override
	public boolean testValueForFormat(String val) {
		return true;
	}

	@Override
	public String getSign() {
		return "text";
	}

}
