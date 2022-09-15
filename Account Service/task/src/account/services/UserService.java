package account.services;

import account.exceptions.BreachedPasswordException;
import account.exceptions.PasswordLengthException;
import account.exceptions.SamePasswordException;
import account.exceptions.UserExistsException;
import account.models.User;
import account.repositories.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Data
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    Set<String> breachedPasswords = Set.of(
            "PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"
    );

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optUser = userRepository.findByEmail(username.toLowerCase()); // pretraga mejla ide u "lowercase"

        if (optUser.isEmpty()) {
            throw new UsernameNotFoundException("Not found:" + username);
        }

        // provjera za breached:
        if (breachedPasswords.contains(optUser.get().getPassword())) {
            throw new BreachedPasswordException();
        }

        return optUser.get();
    }

    // potrebna je posebna metoda za registraciju, a posebna za ažuriranje
    public User register(User user) {
        // odmah prebacujemo mejl u lowercase:
        user.setEmail(user.getEmail().toLowerCase());
        Optional<User> optUser = userRepository.findByEmail(user.getEmail());
        // ukoliko korisnik već postoji, registracija nije moguća:
        if (optUser.isPresent()) {
            throw new UserExistsException();
        }

        // enkodiranje lozinke:
        user.setPassword(getEncoder().encode(user.getPassword()));
        // postavljanje uloge:
        user.setRole("ROLE_USER");

        return userRepository.save(user);
    }

    public void setNewPassword(String newPassword, User currentUser) {
        currentUser.setPassword(getEncoder().encode(newPassword));
    }

    public void samePasswordCheck(String newPassword, User currentUser) {
        if (getEncoder().matches(newPassword, currentUser.getPassword())) {
            throw new SamePasswordException();
        }
    }

    public void breachedPasswordCheck(String password, Set<String> breachedPasswords) {
        if (breachedPasswords.contains(password)) {
            throw new BreachedPasswordException();
        }
    }

    public void passwordLengthCheck(String password) {
        if (password.length() < 12 || password == null) {
            throw new PasswordLengthException();
        }
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(13);
    }

}
