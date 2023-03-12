package ca.lambton.habittracker.category.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import ca.lambton.habittracker.category.dao.CategoryDao;
import ca.lambton.habittracker.category.model.Category;
import ca.lambton.habittracker.common.db.AppDatabase;
import ca.lambton.habittracker.common.repository.Repository;

public interface CategoryRepository extends Repository<Category> {

}
