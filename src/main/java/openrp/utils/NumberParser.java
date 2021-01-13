package openrp.utils;

/**
 * A very basic replacement for NumberUtils, since people have had problems with
 * it loading.
 * 
 * @author Darwin Jonathan
 *
 */
public class NumberParser {

	/**
	 * Checks wheter a given String can be created as an integer.
	 * @param input - the input String.
	 * @return true only if creatable, else false
	 */
	public static boolean isCreatable(String input) {
		if (input.length() == 0) {
			return false;
		}
		if (input.length() > 1) {
			if (input.startsWith("0")) {
				return false;
			}
		}
		if (input.matches("^[0-9]+$")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Creates an input number from a String.
	 * @param input - the input String.
	 * @return the created int. If not creatable, returns -1.
	 */
	public static int createInt(String input) {
		if (isCreatable(input)) {
			int i = 0;
			for (String s : input.split("")) {
				if (s.equalsIgnoreCase("0")) {
					i = i*10;
				} else if (s.equalsIgnoreCase("1")) {
					i = i*10 + 1;
				} else if (s.equalsIgnoreCase("2")) {
					i = i*10 + 2;
				} else if (s.equalsIgnoreCase("3")) {
					i = i*10 + 3;
				} else if (s.equalsIgnoreCase("4")) {
					i = i*10 + 4;
				} else if (s.equalsIgnoreCase("5")) {
					i = i*10 + 5;
				} else if (s.equalsIgnoreCase("6")) {
					i = i*10 + 6;
				} else if (s.equalsIgnoreCase("7")) {
					i = i*10 + 7;
				} else if (s.equalsIgnoreCase("8")) {
					i = i*10 + 8;
				} else if (s.equalsIgnoreCase("9")) {
					i = i*10 + 9;
				}
			}
			return i;
		}
		return -1;
	}

}
