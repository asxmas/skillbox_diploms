package ru.skillbox.team13.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.skillbox.team13.entity.Like;
import ru.skillbox.team13.entity.Person;
import ru.skillbox.team13.repository.projection.Liker;

import java.util.List;

public interface RepoLike extends CrudRepository<Like, Integer> {
    @Query("select l.person.id as likerId from Like l where l.postOrComment.id = :itemId")
    List<Liker> findLikersProjectionsForItemId(int itemId);

    @Query("select count(l) from Like l  where l.person = :person and l.postOrComment.id = :id")
    Integer countByLikerAndItemId(Person person, int id);

    @Modifying
    @Query("delete from Like l where l.person = :person and l.postOrComment.id = :id")
    void deleteByLikerAndId(Person person, int id);
}
