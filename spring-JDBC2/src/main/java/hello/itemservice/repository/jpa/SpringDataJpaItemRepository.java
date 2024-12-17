package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * packageName    : com.kovo.domain.ticketlink
 * fileName       : SpringDataJpaItemRepository
 * author         : 이광호
 * date           : 2024-12-17
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-12-17        이광호       최초 생성
 */
public interface SpringDataJpaItemRepository extends JpaRepository<Item, Long> {

}
