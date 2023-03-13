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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ca.lambton.habittracker.category.dao.CategoryDao;
import ca.lambton.habittracker.category.model.Category;
import ca.lambton.habittracker.common.dao.PictureDao;
import ca.lambton.habittracker.common.helper.Converters;
import ca.lambton.habittracker.common.model.Picture;
import ca.lambton.habittracker.habit.dao.QuoteDao;
import ca.lambton.habittracker.model.Quote;

@Database(entities = {
        Category.class,
        Picture.class,
        Quote.class
}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static Context mContext;

    public synchronized static AppDatabase getInstance(Context context) {
        mContext = context;
        if (INSTANCE == null) {
            INSTANCE = getDatabase(context);
        }
        return INSTANCE;
    }

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "habit_tracker_db")
                        .addCallback(new Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                                Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        getInstance(context).categoryDao().insertAll(Category.populateData());
                                        getInstance(context).insertQuote(context);
                                    }
                                });
                            }
                        })
                        .build();
            }
        }
        return INSTANCE;
    }

    public abstract CategoryDao categoryDao();

    public abstract PictureDao pictureDao();

    public abstract QuoteDao quoteDao();

    public void insertQuote(Context context) {
        List<Quote> quotes = readQuoteFromFile(context);
        QuoteDao quoteDao = getInstance(context).quoteDao();
        databaseWriterExecutor.execute(() -> {
            quotes.forEach(quoteDao::insertQuote);
        });
    }

    private List<Quote> readQuoteFromFile(Context context) {
        List<Quote> dataList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("quote.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                dataList.add(new Quote(parts[0]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }
}
