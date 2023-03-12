package ca.lambton.habittracker.category.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import ca.lambton.habittracker.common.model.Picture;

public class CategoryImages {

    @Embedded
    public Category category;

    @Relation(
            parentColumn = "CATEGORY_ID",
            entityColumn = "PARENT_CATEGORY_ID"
    )
    public List<Picture> pictures;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }
}