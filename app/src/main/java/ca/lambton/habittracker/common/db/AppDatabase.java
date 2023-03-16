package ca.lambton.habittracker.common.db;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ca.lambton.habittracker.category.dao.CategoryDao;
import ca.lambton.habittracker.category.model.Category;
import ca.lambton.habittracker.common.dao.PictureDao;
import ca.lambton.habittracker.common.helper.Converters;
import ca.lambton.habittracker.common.model.Picture;
import ca.lambton.habittracker.habit.dao.HabitDao;
import ca.lambton.habittracker.habit.dao.QuoteDao;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.model.Quote;

@Database(entities = {
        Category.class,
        Picture.class,
        Quote.class,
        Habit.class
}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public synchronized static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = getDatabase(context);
        }
        return INSTANCE;
    }

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "habit_tracker_db").addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                getInstance(context).categoryDao().insertAll(Category.populateData());
                                getInstance(context).insertQuote(context);
                                getInstance(context).insertHabit(context);
                            }
                        });
                    }
                }).build();
            }
        }
        return INSTANCE;
    }

    public abstract CategoryDao categoryDao();

    public abstract PictureDao pictureDao();

    public abstract QuoteDao quoteDao();

    public abstract HabitDao habitDao();

    public void insertQuote(Context context) {
        List<Quote> quotes = readQuoteFromFile(context);
        QuoteDao quoteDao = getInstance(context).quoteDao();
        databaseWriterExecutor.execute(() -> {
            quotes.forEach(quoteDao::insertQuote);
        });
    }

    public void insertHabit(Context context) {
        List<Habit> habits = readHabitFromFile(context);
        HabitDao habitDao = getInstance(context).habitDao();
        databaseWriterExecutor.execute(() -> habits.forEach(habitDao::insertHabit));
    }


    private List<Quote> readQuoteFromFile(Context context) {
        List<Quote> dataList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("quote.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\n");

                Arrays.stream(parts).forEach(q -> dataList.add(new Quote(q.substring(1, q.length() - 1))));


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    private List<Habit> readHabitFromFile(Context context) {
        List<Habit> dataList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("habit.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");

                Habit habit = new Habit();
                habit.setName(parts[0]);
                habit.setDescription(parts[1]);
                habit.setCreationDate(Long.parseLong(parts[2]));
                habit.setPredefined(Boolean.parseBoolean(parts[3]));
                habit.setUserId(Long.parseLong(parts[4]));
                habit.setDuration(parts[5]);
                habit.setFrequency(parts[6]);
                habit.setTime(parts[7]);
                habit.setCategoryId(Long.parseLong(parts[8]));
                habit.setImagePath(parts[9]);

                dataList.add(habit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }
}
