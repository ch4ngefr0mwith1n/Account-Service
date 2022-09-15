package account.repositories;

import account.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // u slučajevima kad želim da izbjegnem "null"
    User findUserByEmail(String email); // u slučajevima kad mi treba provjeren "User" objekat
    //void deleteByEmail(String email);
}
