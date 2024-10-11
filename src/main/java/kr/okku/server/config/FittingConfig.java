package kr.okku.server.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import kr.okku.server.adapters.persistence.repository.fitting.FittingRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackageClasses = FittingRepository.class, mongoTemplateRef = "fittingMongoTemplate")
@EnableConfigurationProperties
public class FittingConfig {

    @Value("${spring.data.mongodb.fitting.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.fitting.uri}")
    private String mongoUri;

    protected String getDatabaseName() {
        return databaseName;
    }

    @Bean(name = "fittingMongoClient")
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Bean(name = "fittingMongoDBFactory")
    public MongoDatabaseFactory mongoDatabaseFactory(
            @Qualifier("fittingMongoClient") MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, getDatabaseName());
    }

    @Bean(name = "fittingMongoTemplate")
    public MongoTemplate mongoTemplate(@Qualifier("fittingMongoDBFactory") MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}