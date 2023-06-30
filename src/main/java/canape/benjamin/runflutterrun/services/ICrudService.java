package canape.benjamin.runflutterrun.services;

public interface ICrudService<T> {

    Iterable<T> getAll();

    T create(T t);

    T getById(long id);

    T update(T t);

    void delete(long id);
}