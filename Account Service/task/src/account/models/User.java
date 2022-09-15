package account.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)

@JsonIgnoreProperties({"enabled", "username","accountNonLocked","authorities","credentialsNonExpired","accountNonExpired"})
@Entity
@Table(name = "accounts")
public class User implements UserDetails {

    private static final String emailCheck = "([a-zA-Z0-9]+)([.{1}])?([a-zA-Z0-9]+)@acme([.])com";

    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;
    @NotBlank
    private String lastname;

    @NotBlank
    @Email
    @Pattern(regexp = emailCheck)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    @Size(min = 12, message = "The password length must be at least 12 chars!")
    private String password;

    @JsonIgnore
    private String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    // "getUsername" će da vraća email:
    @Override
    public String getUsername() {
        return email;
    }

    // ---------------------> ostale metode vraćaju "true" <---------------------
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
