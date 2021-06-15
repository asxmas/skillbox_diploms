package ru.skillbox.team13.jpatest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.team13.entity.Friendship;
import ru.skillbox.team13.entity.FriendshipStatus;
import ru.skillbox.team13.entity.Person;
import ru.skillbox.team13.entity.enums.FriendshipStatusCode;
import ru.skillbox.team13.entity.enums.PersonMessagePermission;
import ru.skillbox.team13.repository.PersonRepo;
import ru.skillbox.team13.repository.RepoFriendship;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static ru.skillbox.team13.entity.enums.FriendshipStatusCode.FRIEND;
import static ru.skillbox.team13.entity.enums.FriendshipStatusCode.REQUEST;

@DataJpaTest
//@SpringBootTest  //undcomment to to enable debugging in H2, (disable '@DataJpaTest')
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FriendsRepositoryTest {

    @Autowired
    RepoFriendship friendshipRepo;

    @Autowired
    PersonRepo personRepo;

    @Autowired
    EntityManagerFactory emf;

    int bobId;
    int jimId;
    int timId;

    @Test
    void sanityCheck() {
        assertNotNull(friendshipRepo);
        assertNotNull(personRepo);
        assertNotNull(emf);
        EntityManager em = emf.createEntityManager();
        assertNotNull(em);
        em.close();
    }

    @BeforeAll
    void setupPersons() {
        Person person1 = new Person();
        person1.setFirstName("Bob");
        person1.setLastName("Bobson");
        person1.setRegDate(LocalDateTime.now());
        person1.setEmail("@1");
        person1.setMessagesPermission(PersonMessagePermission.ALL);
        person1.setBlocked(false);

        Person person2 = new Person();
        person2.setFirstName("Jim");
        person2.setLastName("Jimson");
        person2.setRegDate(LocalDateTime.now());
        person2.setEmail("@2");
        person2.setMessagesPermission(PersonMessagePermission.ALL);
        person2.setBlocked(false);

        Person person3 = new Person();
        person3.setFirstName("Tim");
        person3.setLastName("Timson");
        person3.setRegDate(LocalDateTime.now());
        person3.setEmail("@3");
        person3.setMessagesPermission(PersonMessagePermission.ALL);
        person3.setBlocked(false);

        bobId = personRepo.save(person1).getId();
        jimId = personRepo.save(person2).getId();
        timId = personRepo.save(person3).getId();
    }

    @Test
    //disable '@Transactional' to enable H2 debugging
    @Transactional
    void testCountRequestedFriendships() {
        makeFriendship(bobId, jimId, FRIEND, "from bob to jim");
        makeFriendship(bobId, timId, FRIEND, "from bob to tim");

        Person fromDB = personRepo.findById(bobId).get();
        int count = friendshipRepo.countRequestedFriendships(fromDB.getId(), FRIEND);
        assertEquals(2, count);
    }


    @Test
    @Transactional
    void testCountRequestedFriendshipsForFriendName() {
        makeFriendship(bobId, jimId, FRIEND, "from bob to jim");
        makeFriendship(bobId, timId, FRIEND, "from bob to tim");

        Person fromDB = personRepo.findById(bobId).get();
        int count = friendshipRepo.countRequestedFriendships(fromDB.getId(), FRIEND, "tim%");

        assertEquals(1, count);
    }

    @Test
    @Transactional
    void testCountReceivedFriendships() {
        makeFriendship(jimId, bobId, FRIEND, "from jim to bob");
        makeFriendship(timId, bobId, FRIEND, "from tim to bob");

        Person fromDB = personRepo.findById(bobId).get();
        int count = friendshipRepo.countReceivedFriendships(fromDB.getId(), FRIEND);

        assertEquals(2, count);
    }

    @Test
    @Transactional
    void testCountReceivedFriendshipsForFriendName() {
        makeFriendship(jimId, bobId, FRIEND, "from jim to bob");
        makeFriendship(timId, bobId, FRIEND, "from tim to bob");

        Person fromDB = personRepo.findById(bobId).get();
        int count = friendshipRepo.countReceivedFriendships(fromDB.getId(), FRIEND, "jimson");
        assertEquals(1, count);
    }

    @Test
    @Transactional
    void testFindRequestedFriendships() {
        makeFriendship(bobId, jimId, REQUEST, "from bob to jim");
        makeFriendship(bobId, timId, REQUEST, "from bob to tim");

        Person fromDB = personRepo.findById(bobId).get();
        List<Friendship> f = friendshipRepo.findRequestedFriendships(PageRequest.of(0, 10), fromDB.getId(), REQUEST);
        assertEquals(2, f.size());
        assertTrue(f.stream().map(fr -> fr.getStatus()).anyMatch(s -> s.getName().equals("from bob to tim")));
    }

    @Test
    @Transactional
    void testFindRequestedFriendshipsForName() {
        makeFriendship(bobId, jimId, REQUEST, "from bob to jim");
        makeFriendship(bobId, timId, REQUEST, "from bob to tim");

        Person fromDB = personRepo.findById(bobId).get();
        List<Friendship> f = friendshipRepo.findRequestedFriendships(PageRequest.of(0, 10), fromDB.getId(), REQUEST, "%tim%");
        assertEquals(1, f.size());
    }

    @Test
    @Transactional
    void testFindRequestedFriendship() {
        makeFriendship(bobId, jimId, FRIEND, "from bob to jim");

        assertTrue(friendshipRepo.findRequestedFriendship(bobId, jimId).isPresent());
    }

    @Test
    @Transactional
    void testFindNonexistingFriendship() {
        makeFriendship(bobId, jimId, FRIEND, "from bob to jim");

        assertFalse(friendshipRepo.findRequestedFriendship(bobId, jimId + 100500).isPresent());
    }

    @Test
    @Transactional
    void testFindFriendshipForCode() {
        makeFriendship(bobId, jimId, REQUEST, "from bob to jim");

        assertTrue(friendshipRepo.findRequestedFriendship(bobId, jimId, REQUEST).isPresent());
    }

    @Test
    @Transactional
    void testFindReceivedFriendship() {
        makeFriendship(bobId, jimId, REQUEST, "from bob to jim");
        makeFriendship(timId, jimId, REQUEST, "from tim to jim");

        List<Friendship> f1 = friendshipRepo.findReceivedFriendships(PageRequest.of(0, 10), jimId, REQUEST, "%son");
        List<Friendship> f2 = friendshipRepo.findReceivedFriendships(PageRequest.of(0, 10), jimId, REQUEST, "bob");
        assertEquals(2, f1.size());
        assertEquals(1, f2.size());
    }

    @Test
    @Transactional
    void testFriendshipsInSrcIds() {
        makeFriendship(bobId, jimId, REQUEST, "from bob to jim");
        makeFriendship(timId, jimId, FRIEND, "from tim to jim");

        List<Friendship> f = friendshipRepo.findFriendshipsFromIdsToId(jimId, new int[]{bobId, timId});

        assertEquals(1, f.stream().filter(fr -> fr.getStatus().getCode().equals(FRIEND)).count());
        assertEquals(1, f.stream().filter(fr -> fr.getStatus().getCode().equals(REQUEST)).count());
    }

    private void makeFriendship(int from, int to, FriendshipStatusCode code, String statusName) {
        List<Person> p = getPersonsFromDB(from, to);

        FriendshipStatus friendshipStatus = new FriendshipStatus(LocalDateTime.now(), statusName, code);
        Friendship friendship = new Friendship(friendshipStatus, p.get(0), p.get(1));
        friendshipRepo.save(friendship);
    }

    private List<Person> getPersonsFromDB(int... ids) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        List<Person> l = Arrays.stream(ids).mapToObj(id -> em.find(Person.class, id)).collect(Collectors.toList());

        em.getTransaction().commit();
        em.close();
        return l;
    }
}
