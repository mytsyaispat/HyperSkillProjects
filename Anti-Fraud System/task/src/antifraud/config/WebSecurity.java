package antifraud.config;

import antifraud.entity.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;

    public WebSecurity(RestAuthenticationEntryPoint restAuthenticationEntryPoint, UserDetailsService userDetailsService) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().authenticationEntryPoint(restAuthenticationEntryPoint)
                .and().authorizeRequests()

                .mvcMatchers("/actuator/shutdown", "/h2", "/h2/**").permitAll()
                .mvcMatchers(HttpMethod.POST, "/api/auth/user", "/api/auth/user/**").permitAll()
                .mvcMatchers(HttpMethod.DELETE, "/api/auth/user", "/api/auth/user/**").hasRole(Role.ADMINISTRATOR.name())
                .mvcMatchers(HttpMethod.PUT, "/api/auth/role", "/api/auth/role/**").hasRole(Role.ADMINISTRATOR.name())
                .mvcMatchers(HttpMethod.PUT, "/api/auth/access", "/api/auth/access/**").hasRole(Role.ADMINISTRATOR.name())
                .mvcMatchers(HttpMethod.GET, "/api/auth/list", "/api/auth/list/**").hasAnyRole(Role.ADMINISTRATOR.name(), Role.SUPPORT.name())
                .mvcMatchers(HttpMethod.POST, "/api/antifraud/transaction", "/api/antifraud/transaction/**").hasRole(Role.MERCHANT.name())
                .mvcMatchers(HttpMethod.PUT, "/api/antifraud/transaction", "/api/antifraud/transaction/**").hasRole(Role.SUPPORT.name())
                .mvcMatchers("/api/antifraud/suspicious-ip", "/api/antifraud/stolencard", "/api/antifraud/history", "/api/antifraud/suspicious-ip/**", "/api/antifraud/stolencard/**", "/api/antifraud/history/**").hasRole(Role.SUPPORT.name())
                .mvcMatchers("api/antifraud/suspicious-ip", "api/antifraud/suspicious-ip/**",
                        "api/antifraud/stolencard", "api/antifraud/stolencard/**").hasRole(Role.SUPPORT.name())
                .mvcMatchers("/**").denyAll()

                .and().csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }

}
