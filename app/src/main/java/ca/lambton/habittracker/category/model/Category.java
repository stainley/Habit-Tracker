package ca.lambton.habittracker.category.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "CATEGORY_TBL")
public class Category implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "CATEGORY_ID")
    private Long id;
    private String name;
    private String imageName;

    private int duration;

    private String interval;

    private String frequencyUnit;

    private Date createdDate;

    private Date updatedDate;

    private Boolean isDefault;

    private int userId;

    public Category() {
    }

    @Ignore
    public Category(String name, String imageName, int Duration, String interval, Date createdDate, Date updatedDate, Boolean isDefault, int userId) {
        this.name = name;
        this.imageName = imageName;
        this.duration = Duration;
        this.interval = interval;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.isDefault = isDefault;
        this.userId = userId;
    }

    @Ignore
    public Category(String name, String imageName, int Duration, String interval, String frequencyUnit, Date createdDate, Date updatedDate, Boolean isDefault, int userId) {
        this.name = name;
        this.imageName = imageName;
        this.duration = Duration;
        this.interval = interval;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.isDefault = isDefault;
        this.userId = userId;
        this.frequencyUnit = frequencyUnit;
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

    public String getFrequencyUnit() {
        return frequencyUnit;
    }

    public void setFrequencyUnit(String frequencyUnit) {
        this.frequencyUnit = frequencyUnit;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
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
}
