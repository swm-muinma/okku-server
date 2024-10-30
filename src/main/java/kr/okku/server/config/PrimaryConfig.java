package kr.okku.server.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import kr.okku.server.adapters.persistence.repository.cart.CartRepository;
import kr.okku.server.adapters.persistence.repository.fittinglog.FittingLogRepository;
import kr.okku.server.adapters.persistence.repository.item.ItemRepository;
import kr.okku.server.adapters.persistence.repository.pick.PickRepository;
import kr.okku.server.adapters.persistence.repository.refresh.RefreshRepository;
import kr.okku.server.adapters.persistence.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableMongoRepositories(basePackageClasses = {UserRepository.class, CartRepository.class, ItemRepository.class, PickRepository.class, RefreshRepository.class, FittingLogRepository.class}, mongoTemplateRef = "primaryMongoTemplate")
@EnableConfigurationProperties
public class PrimaryConfig {
    @Value("${spring.data.mongodb.primary.database}")
    private String databaseName;

    // MongoDB URI를 application.properties에서 가져옵니다.
    @Value("${spring.data.mongodb.primary.uri}")
    private String mongoUri;

    protected String getDatabaseName() {
        return databaseName;
    }


    @Bean(name = "primaryMongoClient")
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Primary
    @Bean(name = "primaryMongoDBFactory")
    public MongoDatabaseFactory mongoDatabaseFactory(
            @Qualifier("primaryMongoClient") MongoClient mongoClient) {

        return new SimpleMongoClientDatabaseFactory(mongoClient, getDatabaseName());
    }


    @Primary
    @Bean(name = "primaryMongoTemplate")
    public MongoTemplate mongoTemplate(@Qualifier("primaryMongoDBFactory") MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}
