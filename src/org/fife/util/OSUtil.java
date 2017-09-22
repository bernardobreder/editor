package org.fife.util;

import java.io.File;

public class OSUtil {
	
	public static File homeFile() {
		return new File(System.getProperty("user.home"));
	}
	
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}
	
	public static boolean isMac() {
		return System.getProperty("os.name").toLowerCase().contains("mac");
	}
	
	public static boolean isLinux() {
		return System.getProperty("os.name").toLowerCase().contains("linux");
	}
	
	public static String getExecutableExtension() {
		return isWindows() ? ".exe" : "";
	}
	
	public static String getMetaKey() {
		return isMac() ? "meta" : "ctrl";
	}
	
}
