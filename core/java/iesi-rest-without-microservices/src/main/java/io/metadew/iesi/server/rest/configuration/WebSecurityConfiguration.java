package io.metadew.iesi.server.rest.configuration;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.sql.DataSource;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Order(2)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;

    public WebSecurityConfiguration(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @Bean
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }

    // User for oauth
    @Bean
    public JdbcUserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
        manager.setDataSource(dataSource);
        manager.getDataSource();
        return manager;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> cfg = auth.jdbcAuthentication()
                .passwordEncoder(passwordEncoder()).dataSource(dataSource);
        cfg.getUserDetailsService().setEnableAuthorities(true);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static final String[] HTTPS = {"/api/environments/**", "/api/impersonations/**", "/api/connections/**",
            "/api/components/**", "api/scripts/**", "api/users/**", "api/myaccount/**"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        //Set HTTPS
        http.headers().httpStrictTransportSecurity().disable().and().httpBasic().and().formLogin().and()
                .authorizeRequests().anyRequest().authenticated();
        http.requiresChannel().antMatchers(HTTPS).requiresSecure();
        http.requestMatcher(EndpointRequest.toAnyEndpoint())
                .authorizeRequests()
                .anyRequest().hasAuthority("AUTHORIZED_ADMIN")
                .and()
                .httpBasic();

    }

}
