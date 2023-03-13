package ca.lambton.habittracker.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "QUOTE")
public class Quote implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private long id;
    @ColumnInfo(name = "DESCRIPTION")
    private String description;

    public Quote(long id, String description) {
        this.id = id;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

}
