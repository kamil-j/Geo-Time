package pl.edu.agh.geotime.config;

import pl.edu.agh.geotime.security.*;
import pl.edu.agh.geotime.security.jwt.*;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import javax.annotation.PostConstruct;

@Configuration
@Import(SecurityProblemSupport.class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserDetailsService userDetailsService;

    private final TokenProvider tokenProvider;

    private final CorsFilter corsFilter;

    private final SecurityProblemSupport problemSupport;

    public SecurityConfiguration(AuthenticationManagerBuilder authenticationManagerBuilder, UserDetailsService userDetailsService,TokenProvider tokenProvider,CorsFilter corsFilter, SecurityProblemSupport problemSupport) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDetailsService = userDetailsService;
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.problemSupport = problemSupport;
    }

    @PostConstruct
    public void init() {
        try {
            authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        } catch (Exception e) {
            throw new BeanInitializationException("Security configuration failed", e);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/app/**/*.{js,html}")
            .antMatchers("/i18n/**")
            .antMatchers("/content/**")
            .antMatchers("/swagger-ui/index.html")
            .antMatchers("/test/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint(problemSupport)
            .accessDeniedHandler(problemSupport)
        .and()
            .csrf()
            .disable()
            .headers()
            .frameOptions()
            .disable()
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
            .antMatchers("/api/activate").permitAll()
            .antMatchers("/api/authenticate").permitAll()
            .antMatchers("/api/account/reset-password/init").permitAll()
            .antMatchers("/api/account/reset-password/finish").permitAll()
            .antMatchers("/api/profile-info").permitAll()
            .antMatchers("/api/departments-info/**").permitAll()
            .antMatchers("/api/schedule-info/**").permitAll()
            .antMatchers("/api/academic-units-info/**").permitAll()
            .antMatchers("/api/semester-info/**").permitAll()
            .antMatchers("/management/health").permitAll()
            .antMatchers("/swagger-resources/configuration/ui").permitAll()
            .antMatchers("/swagger-ui/index.html").permitAll()
            .antMatchers("/v2/api-docs/**").permitAll()
            .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/api/departments/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers(HttpMethod.GET, "/api/user-groups/**").hasAnyAuthority(AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)
            .antMatchers("/api/user-groups/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers(HttpMethod.GET, "/api/room-types/**").hasAnyAuthority(AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)
            .antMatchers("/api/room-types/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers(HttpMethod.GET, "/api/rooms/**").hasAnyAuthority(AuthoritiesConstants.MANAGER, AuthoritiesConstants.PLANNER)
            .antMatchers("/api/rooms/**").hasAuthority(AuthoritiesConstants.MANAGER)
            .antMatchers(HttpMethod.GET, "/api/class-types/**").hasAnyAuthority(AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)
            .antMatchers("/api/class-types/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers(HttpMethod.GET, "/api/semesters/**").hasAnyAuthority(AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)
            .antMatchers("/api/semesters/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers(HttpMethod.GET, "/api/users/**").hasAnyAuthority(AuthoritiesConstants.MANAGER, AuthoritiesConstants.PLANNER)
            .antMatchers("/api/users/**").hasAuthority(AuthoritiesConstants.MANAGER)
            .antMatchers("/api/subdepartments/**").hasAuthority(AuthoritiesConstants.MANAGER)
            .antMatchers("/api/locations/**").hasAuthority(AuthoritiesConstants.MANAGER)
            .antMatchers("/api/study-fields/**").hasAuthority(AuthoritiesConstants.MANAGER)
            .antMatchers(HttpMethod.GET, "/api/class-units/**").hasAnyAuthority(AuthoritiesConstants.MANAGER, AuthoritiesConstants.PLANNER)
            .antMatchers(HttpMethod.PUT, "/api/class-units/assign").hasAnyAuthority(AuthoritiesConstants.PLANNER)
            .antMatchers("/api/class-units/**").hasAuthority(AuthoritiesConstants.MANAGER)
            .antMatchers("/api/class-unit-groups/**").hasAuthority(AuthoritiesConstants.MANAGER)
            .antMatchers("/api/booking-units/**").hasAnyAuthority(AuthoritiesConstants.MANAGER, AuthoritiesConstants.PLANNER)
            .antMatchers("/api/schedule-units/**").hasAuthority(AuthoritiesConstants.MANAGER)
            .antMatchers("/api/academic-units/**").hasAuthority(AuthoritiesConstants.MANAGER)
            .antMatchers(HttpMethod.GET, "/api/scheduling-time-frames/user/**").hasAnyAuthority(AuthoritiesConstants.PLANNER, AuthoritiesConstants.USER)
            .antMatchers("/api/scheduling-time-frames/**").hasAuthority(AuthoritiesConstants.MANAGER)
            .antMatchers("/api/planning-metrics/subdepartment/**").hasAnyAuthority(AuthoritiesConstants.MANAGER, AuthoritiesConstants.PLANNER)
            .antMatchers("/api/planning-metrics/**").hasAuthority(AuthoritiesConstants.MANAGER)
            .antMatchers("/api/timetable-management/**").hasAuthority(AuthoritiesConstants.MANAGER)
            .antMatchers("/api/timetable-management-reserved/**").hasAuthority(AuthoritiesConstants.MANAGER)
            .antMatchers("/api/booking-conflicts/**").hasAnyAuthority(AuthoritiesConstants.MANAGER, AuthoritiesConstants.PLANNER, AuthoritiesConstants.USER)
            .antMatchers("/api/timetable-bookings/**").hasAnyAuthority(AuthoritiesConstants.MANAGER, AuthoritiesConstants.PLANNER, AuthoritiesConstants.USER)
            .antMatchers("/api/timetable-bookings/lock").hasAnyAuthority(AuthoritiesConstants.MANAGER, AuthoritiesConstants.PLANNER)
            .antMatchers("/api/timetable/**").hasAnyAuthority(AuthoritiesConstants.MANAGER, AuthoritiesConstants.PLANNER, AuthoritiesConstants.USER)
            .antMatchers("/api/timetable-reserved/**").hasAuthority(AuthoritiesConstants.USER)
            .antMatchers("/api/**").authenticated()
            .and()
            .apply(securityConfigurerAdapter());
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }
}
