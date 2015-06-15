package org.apache.ibatis.migration.utils;

import java.io.File;
import java.io.IOException;

public enum Util {
  ;

  public static boolean isOption(String arg) {
    return arg.startsWith("--") && !arg.trim().endsWith("=");
  }

  public static File file(File path, String fileName) {
    File file = new File(path.getAbsolutePath() + File.separator + fileName);
    try {
      return file.getCanonicalFile();
    } catch (IOException e) {
      return file;
    }
  }
}
