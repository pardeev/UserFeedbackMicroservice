package pardeev.feedback;

import org.springframework.data.repository.Repository;

import com.google.common.base.Preconditions;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Resource interface defining operation on Feedback domain repository.
 *
 * @see Feedback
 */

public interface FeedbackRepository extends Repository<Feedback, UUID> {
    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity Feedback entity
     * @return the saved entity
     */
    Feedback save(Feedback entity);

    /**
     * Saves all given entities.
     *
     * @param entities Iterable Feedback entries
     * @return the saved entities
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    Iterable<Feedback> save(Iterable<Feedback> entities);

    /**
     * Returns all instances of the type.
     *
     * @return Stream of all entities
     */
    Stream<Feedback> findAll();

    /**
     * Retrieves an entities by its name.
     *
     * @param name must not be {@literal null}.
     * @return Stream of entities with the given name
     * @throws IllegalArgumentException if {@code name} is {@literal null}
     */
    default Stream<Feedback> findByName(String name) {
        Preconditions.checkNotNull(name);
        return findAll().filter(feedback -> name.equals(feedback.getName()));
    }

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id
     * @throws IllegalArgumentException if {@code id} is {@literal null}
     */
    Optional<Feedback> findOne(UUID id);

    /**
     * Deletes all entities managed by the repository.
     */
    void deleteAll();
}
