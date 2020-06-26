package pl.konradboniecki.budget.accountmanagement.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.konradboniecki.budget.accountmanagement.service.ActivationCodeRepository;
import pl.konradboniecki.budget.accountmanagement.service.DbCleanupService;

@Configuration
public class SchedulingConfig {
    @Autowired
    private ActivationCodeRepository activationCodeRepository;

    @Bean
    @Profile("operations")
    public DbCleanupService dbCleanupService() {
        return new DbCleanupService(activationCodeRepository);
    }
}
