package ru.job4j.todo.persistence;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

@Repository
@AllArgsConstructor
public class HibernateTaskStore implements TaskStore {

    private final CrudStore crudStore;

    /**
     * Sohranit' zadachu v baze dannyh.
     * @param task zadacha.
     * @return zadacha s ID.
     */
    @Override
    public Task save(Task task) {
        crudStore.run(session -> session.save(task));
        return task;
    }

    /**
     * Najti zadachu po ID.
     * @param id ID zadachi.
     * @return Optional zadachi.
     */
    @Override
    public Optional<Task> findById(int id) {
        return crudStore.optional("FROM Task WHERE id = :tId", Task.class, Map.of("tId", id));
    }

    /**
     * Poluchit' spisok vseh zadach konkretnogo pol'zovatelja.
     * @param id ID pol'zovatelja.
     * @return spisok zadach.
     */
    @Override
    public Collection<Task> findAllByUserId(int id) {
        return crudStore.query("FROM Task WHERE user_id = :uId", Task.class, Map.of("uId", id));
    }

    /**
     * Poluchit' vypolnennye zadachi konkretnogo pol'zovatelja.
     * @param id ID pol'zovatelja.
     * @return spisok zadach.
     */
    @Override
    public Collection<Task> findDoneByUserId(int id) {
        return crudStore.query(
                "FROM Task WHERE done = true AND user_id = :uId",
                Task.class,
                Map.of("uId", id)
        );
    }

    /**
     * Poluchit' zadachi ne starshe odnoj nedeli dlja konkretnogo pol'zovatelja.
     * @param id ID pol'zovatelja
     * @return spisok zadach.
     */
    @Override
    public Collection<Task> findNewByUserId(int id) {
        LocalDate afterDate = LocalDate.now().minusWeeks(1);
        return crudStore.query(
                "FROM Task WHERE created > :afterDate AND user_id = :uId",
                Task.class,
                Map.of("afterDate", afterDate, "uId", id));
    }

    /**
     * Obnovit' v baze dannyh zadachu.
     * @param task zadacha.
     * @return true esli udalos' obnovit'.
     */
    @Override
    public boolean update(Task task) {
        return crudStore.run(session -> session.update(task));
    }

    /**
     * Otmetit' zadachu vypolnennoj.
     * @param id ID zadachi.
     * @return true esli udalos' obnovit'.
     */
    @Override
    public boolean getDone(int id) {
        return crudStore.run("UPDATE Task SET done = true WHERE id = :tId", Map.of("tId", id));
    }

    /**
     * Udalit' zadachu
     * @param id ID zadachi.
     * @return true esli udalos' udalit'.
     */
    @Override
    public boolean delete(int id) {
        return crudStore.run("DELETE Task WHERE id = :tId", Map.of("tId", id));
    }

}