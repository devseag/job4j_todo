package ru.job4j.todo.persistence;

import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

@Repository
@AllArgsConstructor
public class HibernateUserStore implements UserStore {

    private final CrudStore crudStore;

    /**
     * Sohranit' pol'zovatelja v baze dannyh.
     * @param user pol'zovatel'.
     * @return pol'zovatel' s ID.
     */
    @Override
    public Optional<User> save(User user) {
        Optional<User> result = Optional.empty();
        if (crudStore.run(session -> session.save(user))) {
            result = Optional.of(user);
        }
        return result;
    }

    /**
     * Poluchit' pol'zovatelja po loginu i parolju.
     * @param login login pol'zovatelja.
     * @param password parol' pol'zovatelja.
     * @return Optional pol'zovatelja.
     */
    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        return crudStore.optional(
                "FROM User WHERE login = :uLogin AND password = :uPassword",
                User.class,
                Map.of("uLogin", login, "uPassword", password)
        );
    }

}