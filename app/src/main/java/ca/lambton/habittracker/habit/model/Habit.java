package ca.lambton.habittracker.habit.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "HABIT_TBL")
public class Habit implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private long id;
    @ColumnInfo(name = "NAME")
    private String name;
    @ColumnInfo(name = "DESCRIPTION")
    private String description;
    @ColumnInfo(name = "CREATION_DATE")
    private long creationDate;
    @ColumnInfo(name = "PREDEFINED")
    private boolean isPredefined;
    @ColumnInfo(name = "USER_ID")
    private long userId;
    @ColumnInfo(name = "DURATION")
    private String duration;

    @ColumnInfo(name = "DURATION_UNIT")
    private int durationUnit;
    @ColumnInfo(name = "FREQUENCY")
    private String frequency;

    @ColumnInfo(name = "FREQUENCY_UNIT")
    private int frequencyUnit;
    @ColumnInfo(name = "TIME")
    private String time;
    @ColumnInfo(name = "CATEGORY_ID")
    private long categoryId;
    @ColumnInfo(name = "IMAGE")
    private String imagePath;

    @ColumnInfo(name = "HABIT_TYPE")
    private int habitType;

    @ColumnInfo(name = "START_DATE")
    private long startDate;

    @ColumnInfo(name = "END_DATE")
    private long endDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isPredefined() {
        return isPredefined;
    }

    public void setPredefined(boolean predefined) {
        isPredefined = predefined;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(int durationUnit) {
        this.durationUnit = durationUnit;
    }

    public int getFrequencyUnit() {
        return frequencyUnit;
    }

    public void setFrequencyUnit(int frequencyUnit) {
        this.frequencyUnit = frequencyUnit;
    }

    public int getHabitType() {
        return habitType;
    }

    public void setHabitType(int habitType) {
        this.habitType = habitType;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
}
