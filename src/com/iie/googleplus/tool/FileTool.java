package com.iie.googleplus.tool;

import java.io.File;

public class FileTool {
	public  static boolean isExistFile(String filename) {
		File ff = new File(filename);
		if (ff.exists()) {
			return true;
		}
		return false;
	}
}

