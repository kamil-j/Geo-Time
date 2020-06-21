package pl.edu.agh.geotime.config;

import io.github.jhipster.config.JHipsterProperties;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@AutoConfigureAfter(value = { MetricsConfiguration.class })
@AutoConfigureBefore(value = { WebConfigurer.class, DatabaseConfiguration.class })
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache =
            jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                .withExpiry(Expirations.timeToLiveExpiration(Duration.of(ehcache.getTimeToLiveSeconds(), TimeUnit.SECONDS)))
                .build());
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            cm.createCache(pl.edu.agh.geotime.repository.UserRepository.USERS_BY_LOGIN_CACHE, jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.repository.UserRepository.USERS_BY_EMAIL_CACHE, jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.repository.UserExtRepository.USERS_EXT_BY_LOGIN_CACHE, jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.User.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.Authority.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.Location.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.RoomType.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.Room.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.Department.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.UserExt.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.StudyField.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.ClassUnit.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.ClassType.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.ScheduleUnit.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.Semester.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.repository.SemesterRepository.ACTIVE_SEMESTER_CACHE, jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.AcademicUnit.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.UserGroup.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.SchedulingTimeFrame.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.ClassUnitGroup.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.BookingUnit.class.getName(), jcacheConfiguration);
            cm.createCache(pl.edu.agh.geotime.domain.Subdepartment.class.getName(), jcacheConfiguration);
            // jhipster-needle-ehcache-add-entry
        };
    }
}
