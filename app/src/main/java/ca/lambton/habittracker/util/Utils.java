package ca.lambton.habittracker.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;

public class Utils {

    /**
     * Compute progress of the habit
     *
     * @param habitProgress HabitProgress
     * @return int that represent the percentage
     */
    public static int computeProgress(@NonNull HabitProgress habitProgress) {
        double frequency = habitProgress.getHabit().getFrequency();
        Map<String, Integer> progressMap = habitProgress.getProgressList()
                .stream()
                .collect(Collectors.groupingBy(Progress::getDate, Collectors.summingInt(Progress::getCounter)));

        double sumOfPercentages = progressMap.values().stream()
                .mapToDouble(sum -> (sum / frequency) * 100)
                .average()
                .orElse(0);

        return (int) Math.round(sumOfPercentages);
    }

    /**
     * Maximum numbers of Streak. If there are two time or more is Streak. One doesn't count as Steak.
     *
     * @param habitProgress HabitProgress
     * @return int of max streak
     */
    public static int maxNumbersOfStreak(HabitProgress habitProgress) {
        Map<String, Integer> countsByDate = new HashMap<>();

        Habit habit = habitProgress.getHabit();

        habitProgress.getProgressList().stream()
                .filter(progressObj -> progressObj.getDate() != null)
                .forEach(obj -> {
                    String date = obj.getDate();
                    countsByDate.putIfAbsent(date, 0);
                    countsByDate.computeIfPresent(date, (d, count) -> count + 1);
                });

        // Iterate over the dates in order and print the counts
        LocalDate startDate = countsByDate.keySet().stream().map(LocalDate::parse).min(LocalDate::compareTo).orElse(null);

        Instant endDateInstant = Instant.ofEpochMilli(habit.getEndDate());
        LocalDate endDate = endDateInstant.atZone(ZoneId.systemDefault()).toLocalDate();

        if (startDate != null && endDate != null) {
            LocalDate currentDate = startDate;
            int currentValue = -1; // initialize to a value that won't match any count
            int currentStreak = 0;
            int maxStreak = 0;

            while (!currentDate.isAfter(endDate)) {

                int count = countsByDate.getOrDefault(currentDate.toString(), -9);

                if (count == currentValue && count > 0) {
                    currentStreak++;
                    maxStreak = Math.max(maxStreak, currentStreak);
                } else {
                    currentValue = count;
                    currentStreak = 1;
                }
                //Move next day
                currentDate = currentDate.plusDays(1);
            }
            return maxStreak;
        }
        return 0;
    }
    
    /**
     * Current number of Streak.
     *
     * @param habitProgress HabitProgress
     * @return int of current streak
     */
    public static int currentNumberOfStreak(HabitProgress habitProgress) {
        Map<String, Integer> countsByDate = new HashMap<>();

        Habit habit = habitProgress.getHabit();

        habitProgress.getProgressList().stream()
                .filter(progressObj -> progressObj.getDate() != null)
                .forEach(obj -> {
                    String date = obj.getDate();
                    countsByDate.putIfAbsent(date, 0);
                    countsByDate.computeIfPresent(date, (d, count) -> count + 1);
                });

        // Iterate over the dates in order and print the counts
        LocalDate startDate = countsByDate.keySet().stream().map(LocalDate::parse).min(LocalDate::compareTo).orElse(null);

        Instant endDateInstant = Instant.ofEpochMilli(habit.getEndDate());
        LocalDate endDate = endDateInstant.atZone(ZoneId.systemDefault()).toLocalDate();

        if (startDate != null && endDate != null) {
            LocalDate currentDate = startDate;
            int currentValue = -1; // initialize to a value that won't match any count
            int currentStreak = 0;

            while (!currentDate.isAfter(endDate)) {

                int count = countsByDate.getOrDefault(currentDate.toString(), -9);

                if (count == currentValue && count > 0) {
                    currentStreak++;
                } else {
                    currentValue = count;
                    currentStreak = 1;
                }
                //Move next day
                currentDate = currentDate.plusDays(1);
            }
            return currentStreak;
        }
        return 0;
    }

    /**
     * Calculate the numbers of days for a habit to be completed
     *
     * @param habit Habit
     * @return long numbers of days
     */
    public static long getTotalDays(@NonNull Habit habit) {
        Instant instantStartDate = Instant.ofEpochMilli(habit.getStartDate());
        LocalDate startLocalDate = instantStartDate.atZone(ZoneId.systemDefault()).toLocalDate();

        Instant instantEndDate = Instant.ofEpochMilli(habit.getEndDate());
        LocalDate endLocalDate = instantEndDate.atZone(ZoneId.systemDefault()).toLocalDate();

        return ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
    }

    /**
     * Calculate the numbers of weeks for a habit to be completed
     *
     * @param habit Habit
     * @return long numbers of weeks
     */
    public static long getTotalOfWeeks(@NonNull Habit habit) {
        Instant instantStartDate = Instant.ofEpochMilli(habit.getStartDate());
        LocalDate startLocalDate = instantStartDate.atZone(ZoneId.systemDefault()).toLocalDate();

        Instant instantEndDate = Instant.ofEpochMilli(habit.getEndDate());
        LocalDate endLocalDate = instantEndDate.atZone(ZoneId.systemDefault()).toLocalDate();

        return ChronoUnit.WEEKS.between(startLocalDate, endLocalDate);
    }

    /**
     * Calculate the numbers of months for a habit to be completed
     *
     * @param habit Habit
     * @return long numbers of months
     */
    public static long getTotalOfMonths(@NonNull Habit habit) {
        Instant instantStartDate = Instant.ofEpochMilli(habit.getStartDate());
        LocalDate startLocalDate = instantStartDate.atZone(ZoneId.systemDefault()).toLocalDate();

        Instant instantEndDate = Instant.ofEpochMilli(habit.getEndDate());
        LocalDate endLocalDate = instantEndDate.atZone(ZoneId.systemDefault()).toLocalDate();

        return ChronoUnit.MONTHS.between(startLocalDate, endLocalDate);
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
