package ca.lambton.habittracker.util;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static String loadAssetFile(Context context, String assetFile) {
        try {
            InputStream is = context.getAssets().open(assetFile);

            byte[] buf = new byte[1024 * 500];

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int size = 0;
            while ((size = is.read(buf, 0, buf.length)) >= 0) {
                output.write(buf, 0, size);
            }

            return new String(output.toByteArray());
        } catch (IOException e) {
        }
        return null;
    }

    public static String parseDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        Date date = new Date(time);
        return formatter.format(date);
    }
}
