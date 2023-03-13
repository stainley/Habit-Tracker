package ca.lambton.habittracker.category.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

import ca.lambton.habittracker.R;

@Entity(tableName = "CATEGORY_TBL")
public class Category implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "CATEGORY_ID")
    private Long id;
    private String name;
    private String imageName;

    private int duration;

    private String interval;

    private long createdDate;

    private long updatedDate;

    private Boolean isDefault;

    private int userId;

    public Category() {}

    @Ignore
    public Category(String name, String imageName, int Duration, String interval, long createdDate, long updatedDate, Boolean isDefault, int userId) {
        this.name = name;
        this.imageName = imageName;
        this.duration = Duration;
        this.interval = interval;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.isDefault = isDefault;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(long updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public static Category[] populateData() {
        return new Category[] {
            new Category("Short Duration", "R.drawable.short_duration", 2, "5 - 10 mins", new Date(), new Date(), true, 0),
            new Category("Long Duration", "R.drawable.long_duration", 3, "20 mins - 1 hour", new Date(), new Date(), true, 0),
            new Category("Hobbies", "R.drawable.hobbies", 3, "30 mins", new Date(), new Date(), true, 0),
            new Category("Outdoor Activities", "R.drawable.outdoor_activities", 3, "20 mins - 1 hour", new Date(), new Date(), true, 0),
            new Category("Quit Bad Habits", "R.drawable.quit_bad_habits", 2, "20 mins - 1 hour", new Date(), new Date(), true, 0),
            new Category("Food Habits", "R.drawable.foods_habit", 2, "20 mins - 1 hour", new Date(), new Date(), true, 0),
            new Category("Socialize", "R.drawable.socialize", 2, "20 mins - 1 hour", new Date(), new Date(), true, 0),
            new Category("Relaxation", "R.drawable.relaxation", 3, "20 mins - 1 hour", new Date(), new Date(), true, 0),
            new Category("Physical Health", "R.drawable.physical_health", 3, "20 mins - 1 hour", new Date(), new Date(), true, 0),
            new Category("Mental Health", "R.drawable.mental_health", 3, "20 mins - 1 hour", new Date(), new Date(), true, 0),
            new Category("Daily", "R.drawable.daily", 0, "20 mins - 1 hour", new Date(), new Date(), true, 0),
            new Category("Weekly", "R.drawable.weekly", 0, "20 mins - 1 hour", new Date(), new Date(), true, 0),
            new Category("Monthly", "R.drawable.monthly", 0, "20 mins - 1 hour", new Date(), new Date(), true, 0),
            new Category("Self Care", "R.drawable.self_care", 2, "10 - 30 mins", new Date(), new Date(), true, 0),
        };
    }
}
