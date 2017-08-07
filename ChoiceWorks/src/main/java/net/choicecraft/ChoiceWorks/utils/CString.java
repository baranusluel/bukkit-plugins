package net.choicecraft.ChoiceWorks.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CString {
	
	/**
	 * Generates the MD5 hash from the given string.
	 * @param md5 The input string to be hashed.
	 * @return The hash string on success or null if MD5 is not available.
	 */
	public static String MD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}
	
	/**
	 * Converts a string into a boolean (accepts null).
	 * @param str "true" or "false" (case insensitive).
	 * @return true if "true" or false if "false".
	 */
	public static Boolean stringToBoolean(String str)
	{
		if(str == null) return false;
		return str.toLowerCase().equals("true");
	}
	
	/**
	 * Converts a boolean into a string (accepts null).
	 * @param bool the state to be converted.
	 * @return "true" if true or "false" if false.
	 */
	public static String booleanToString(Boolean bool)
	{
		if(bool == null) return "false";
		return bool ? "true" : "false";
	}
	
	/**
	 * Creates a UUID object from a string. The string 
	 * should contain a UUID, either with or without dashes.
	 * @param string The string to be converted
	 * @return The UUID created
	 */
	public static UUID formatUUID(String string){
		if (!string.contains("-")){
			string = string.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
		}
		return UUID.fromString(string);
	}
	
	/**
	 * Unique random number generator, maybe to be moved somewhere else.
	 */
	private static Random randomGenerator = new Random(System.nanoTime());

	/**
	 * Scrambles the characters of a string randomly.
	 * @param str The string to be shuffled.
	 * @return Randomized result of the shuffled string.
	 */
	public static String scramble(String str) {
		if (str == null)
			return null;

		char[] arr = str.toCharArray();
		List<Character> charList = new ArrayList<Character>(arr.length);
		for (final char c : arr) {
			charList.add(c);
		}

		Collections.shuffle(charList, randomGenerator);
		char[] converted = new char[charList.size()];
		for (int i = 0; i < charList.size(); i++) {
			converted[i] = charList.get(i).charValue();
		}

		return new String(converted);
	}
}
