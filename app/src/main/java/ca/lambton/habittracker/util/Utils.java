package ca.lambton.habittracker.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;

public class Utils {

    /**
     * Compute progress of the habit
     *
     * @param habitProgress HabitProgress
     * @return int that represent the percentage
     */
    public static int computeProgress(HabitProgress habitProgress) {
        double frequency = habitProgress.getHabit().getFrequency();
        Map<String, Integer> progressList = habitProgress.getProgressList()
                .stream()
                .collect(Collectors.groupingBy(Progress::getDate, Collectors.summingInt(Progress::getCounter)));

        double sumOfPercentages = progressList.values().stream()
                .mapToDouble(sum -> (sum / frequency) * 100)
                .sum();

        return (int) (sumOfPercentages / progressList.size());
    }

    public static byte[] loadAssetFile(Context context, String assetFile) {
        try {
            InputStream is = context.getAssets().open(assetFile);

            byte[] buf = new byte[1024 * 500];

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int size;
            while ((size = is.read(buf, 0, buf.length)) >= 0) {
                output.write(buf, 0, size);
            }

            return output.toByteArray();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static String parseDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        Date date = new Date(time);
        return formatter.format(date);
    }

    public static String capitalize(String string) {
        String firstCharacter = string.substring(0, 1);
        return firstCharacter.toUpperCase() + string.substring(1).toLowerCase();
    }

    public static byte[] convertImageToBytes(Context context, String imagePath) throws IOException {
        InputStream is = context.getAssets().open("img/" + imagePath);

        Bitmap bitmap = BitmapFactory.decodeStream(is);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap convertBytesToImage(byte[] imageData) {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }


}
