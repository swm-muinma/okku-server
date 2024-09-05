package kr.okku.server.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import kr.okku.server.adapters.persistence.repository.review.ReviewRepository;
import kr.okku.server.adapters.persistence.repository.reviewInsight.ReviewInsightRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collections;

@Configuration
@EnableMongoRepositories(basePackageClasses = ReviewInsightRepository.class, mongoTemplateRef = "reviewinsightMongoTemplate")
@EnableConfigurationProperties
public class ReviewInsightConfig {

    @Value("${spring.data.mongodb.review-insight.database}")
    private String databaseName;

    // MongoDB URI를 application.properties에서 가져옵니다.
    @Value("${spring.data.mongodb.review-insight.uri}")
    private String mongoUri;

    protected String getDatabaseName() {
        return databaseName;
    }

    @Bean(name = "reviewinsightMongoClient")
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Bean(name = "reviewinsightMongoDBFactory")
    public MongoDatabaseFactory mongoDatabaseFactory(
            @Qualifier("reviewinsightMongoClient") MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, getDatabaseName());
    }

    @Bean(name = "reviewinsightMongoTemplate")
    public MongoTemplate mongoTemplate(@Qualifier("reviewinsightMongoDBFactory") MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}