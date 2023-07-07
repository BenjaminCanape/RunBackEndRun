package canape.benjamin.runflutterrun.services;

public interface ICrudService<T> {

    /**
     * Get all entities.
     *
     * @return an iterable collection of entities
     */
    Iterable<T> getAll();

    /**
     * Create an entity.
     *
     * @param t the entity to create
     * @return the created entity
     */
    T create(T t);

    /**
     * Get an entity by its ID.
     *
     * @param id the ID of the entity
     * @return the entity with the specified ID
     */
    T getById(long id);

    /**
     * Update an entity.
     *
     * @param t the entity to update
     * @return the updated entity
     */
    T update(T t);

    /**
     * Delete an entity by its ID.
     *
     * @param id the ID of the entity to delete
     */
    void delete(long id);
}
