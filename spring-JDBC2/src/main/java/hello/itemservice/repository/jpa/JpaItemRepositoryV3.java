package hello.itemservice.repository.jpa;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.itemservice.domain.Item;
import hello.itemservice.domain.QItem;
import static hello.itemservice.domain.QItem.item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * packageName    : com.kovo.domain.ticketlink
 * fileName       : JpaItemRepositoryV3
 * author         : 이광호
 * date           : 2024-12-18
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-12-18        이광호       최초 생성
 */
@Repository
@Transactional
public class JpaItemRepositoryV3 implements ItemRepository {


    private final EntityManager em;
    private final JPAQueryFactory query;

    public JpaItemRepositoryV3(
        EntityManager em
    ) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Item save(final Item item) {
        em.persist(item);
        return item;
    }

    @Override
    public void update(
        final Long itemId,
        final ItemUpdateDto updateParam
    ) {
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    @Override
    public Optional<Item> findById(final Long id) {
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> findAll(final ItemSearchCond cond) {

        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        QItem item = QItem.item;

        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(itemName)) {
            builder.and(likeItemName(itemName));
        }
        if (maxPrice != null) {
            // 작거나 같을 때, price <= maxPrice
            builder.and(item.price.loe(maxPrice));
        }

        return query
                .select(item)
                .from(item)
                .where(likeItemName(itemName), maxPrice(maxPrice))
                .fetch();
    }

    private Predicate maxPrice(final Integer maxPrice) {
        if (maxPrice != null) {
            return item.price.loe(maxPrice);
        }
        return null;
    }

    private BooleanExpression likeItemName(String itemName) {
        if (StringUtils.hasText(itemName)) {
            return item.itemName.like("%" + itemName + "%");
        }
        return null;
    }
}
