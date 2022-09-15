package account.configuration;

import account.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {

    private UserService userDetailsService;

    @Autowired
    public WebSecurityConfigurerImpl(UserService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // moramo da odradimo "override" nad metodom koja prima "AuthenticationManagerBuilder"
    // nakon toga povezuje "UserDetailsService"
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(encoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .mvcMatchers(
                        "api/empl/payment",
                        "api/auth/changepass")
                .authenticated()
                .mvcMatchers(
                        "api/auth/signup",
                        "api/acct/payments",
                        "/actuator/shutdown",
                        "/h2-console/**")
                .permitAll()
                .and()
                .httpBasic()
                .and()
                .sessionManagement().disable();

        /*
            .mvcMatchers("/api/auth/signup", "/actuator/shutdown", "/h2-console/**").permitAll()
            .mvcMatchers("/api/empl/payment", "api/auth/changepass").authenticated()
         */
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}
