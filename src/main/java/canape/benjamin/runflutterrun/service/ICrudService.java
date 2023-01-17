package canape.benjamin.runflutterrun.service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface ICrudService<T> {

    Iterable<T> getAll();
    T create(T t);

    T getById(long id);

    T update(T t);

    void delete(long id);
}