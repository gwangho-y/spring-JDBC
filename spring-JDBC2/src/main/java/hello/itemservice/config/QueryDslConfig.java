package hello.itemservice.config;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.jpa.JpaItemRepository;
import hello.itemservice.repository.jpa.JpaItemRepositoryV3;
import hello.itemservice.service.ItemService;
import hello.itemservice.service.ItemServiceV1;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * packageName    : com.kovo.domain.ticketlink
 * fileName       : QueryDslConfig
 * author         : 이광호
 * date           : 2024-12-18
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-12-18        이광호       최초 생성
 */
@Configuration
@RequiredArgsConstructor
public class QueryDslConfig {

    private final EntityManager em;


    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        return new JpaItemRepositoryV3(em);
    }
}
