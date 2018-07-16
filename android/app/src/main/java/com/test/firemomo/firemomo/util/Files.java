package com.test.firemomo.firemomo.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Files {

  public static File createImageFile() throws IOException {
    String timeStamp =
        new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String imageFileName = "IMG_" + timeStamp + "_";

    return File.createTempFile(imageFileName, ".jpg");
  }
}
