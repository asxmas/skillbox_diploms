package ru.skillbox.team13.repository;

import org.springframework.data.repository.CrudRepository;
import ru.skillbox.team13.entity.Friendship;

public interface RepoFriendship extends CrudRepository<Friendship, Integer> {
}
