package ca.lambton.habittracker.habit.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "HABIT_TBL", indices = {@Index(name = "IDX_HABIT", value = "HABIT_ID")})
public class Habit implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "HABIT_ID")
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
    private String userId;
    @ColumnInfo(name = "DURATION")
    private int duration;
    @ColumnInfo(name = "DURATION_UNIT")
    private String durationUnit;
    @ColumnInfo(name = "FREQUENCY")
    private int frequency;

    @ColumnInfo(name = "FREQUENCY_UNIT")
    private String frequencyUnit;

    @ColumnInfo(name = "CATEGORY_ID")
    private long categoryId;
    @ColumnInfo(name = "IMAGE")
    private String imagePath;

    @ColumnInfo(name = "HABIT_TYPE")
    private String habitType;

    @ColumnInfo(name = "START_DATE")
    private long startDate;

    @ColumnInfo(name = "END_DATE")
    private long endDate;

    @ColumnInfo(name = "SCORE")
    private int score = 0;

    public Habit() {
    }

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


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
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

    public String getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
    }

    public String getFrequencyUnit() {
        return frequencyUnit;
    }

    public void setFrequencyUnit(String frequencyUnit) {
        this.frequencyUnit = frequencyUnit;
    }

    public String getHabitType() {
        return habitType;
    }

    public void setHabitType(String habitType) {
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Habit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", creationDate=" + creationDate +
                ", isPredefined=" + isPredefined +
                ", userId='" + userId + '\'' +
                ", duration=" + duration +
                ", durationUnit='" + durationUnit + '\'' +
                ", frequency=" + frequency +
                ", frequencyUnit='" + frequencyUnit + '\'' +
                ", categoryId=" + categoryId +
                ", imagePath='" + imagePath + '\'' +
                ", habitType='" + habitType + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", score=" + score +
                '}';
    }
}
