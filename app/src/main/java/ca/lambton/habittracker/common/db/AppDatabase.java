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
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ca.lambton.habittracker.category.dao.CategoryDao;
import ca.lambton.habittracker.category.model.Category;
import ca.lambton.habittracker.common.dao.PictureDao;
import ca.lambton.habittracker.common.helper.Converters;
import ca.lambton.habittracker.common.model.Picture;
import ca.lambton.habittracker.community.dao.CommentDao;
import ca.lambton.habittracker.community.dao.PostDao;
import ca.lambton.habittracker.community.model.Comment;
import ca.lambton.habittracker.community.model.Post;
import ca.lambton.habittracker.habit.dao.HabitDao;
import ca.lambton.habittracker.habit.dao.ProgressDao;
import ca.lambton.habittracker.habit.dao.QuoteDao;
import ca.lambton.habittracker.habit.dao.UserDao;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.model.Quote;
import ca.lambton.habittracker.habit.model.User;

@Database(entities = {
        Category.class,
        Picture.class,
        Quote.class,
        Habit.class,
        Progress.class,
        User.class,
        Post.class,
        Comment.class
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
                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
                            getInstance(context).populateQuote(context);
                            getInstance(context).populateHabit(context);
                            getInstance(context).populateCategory(context);
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

    public abstract ProgressDao progressDao();

    public abstract UserDao userDao();

    public abstract PostDao postDao();

    public abstract CommentDao commentDao();

    public void populateQuote(Context context) {
        List<Quote> quotes = readQuoteFromFile(context);
        QuoteDao quoteDao = getInstance(context).quoteDao();
        databaseWriterExecutor.execute(() -> {
            quotes.forEach(quoteDao::insertQuote);
        });
    }

    public void populateHabit(Context context) {
        List<Habit> habits = readHabitFromFile(context);
        HabitDao habitDao = getInstance(context).habitDao();
        databaseWriterExecutor.execute(() -> habits.forEach(habitDao::insertHabit));
    }

    public void populateCategory(Context context) {
        List<Category> categories = readCategoryFromFile(context);
        CategoryDao categoryDao = getInstance(context).categoryDao();
        databaseWriterExecutor.execute(() -> categories.forEach(categoryDao::save));
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
                habit.setUserId(parts[4]);
                habit.setDuration(Integer.parseInt(parts[5]));
                habit.setDurationUnit(parts[6]);
                habit.setFrequency(Integer.parseInt(parts[7]));
                habit.setFrequencyUnit(parts[8]);
                habit.setCategoryId(Long.parseLong(parts[9]));
                habit.setImagePath(parts[10]);
                habit.setHabitType(parts[11]);
                dataList.add(habit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    private List<Category> readCategoryFromFile(Context context) {
        List<Category> dataList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("category.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");

                Category category = new Category();
                category.setName(parts[0]);
                category.setImageName(parts[1]);
                category.setDuration(Integer.parseInt(parts[2]));
                category.setInterval(parts[3]);
                category.setFrequencyUnit(parts[4]);
                category.setCreatedDate(new Date());
                category.setUpdatedDate(new Date());
                category.setIsDefault(true);
                category.setUserId(-1);

                dataList.add(category);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }
}
