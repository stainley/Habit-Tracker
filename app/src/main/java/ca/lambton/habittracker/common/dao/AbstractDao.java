package ca.lambton.habittracker.common.dao;

import androidx.lifecycle.LiveData;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface AbstractDao<T extends Serializable> {

    void save(T type);

    void delete(T type);

    void update(T type);

    LiveData<List<T>> fetchAll();

    LiveData<Optional<T>> fetchById(Long id);

}
