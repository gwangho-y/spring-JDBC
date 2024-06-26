package hello.springjdbc.repository;

import hello.springjdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

/**
 * packageName    : com.kovo.domain.ticketlink
 * fileName       : MemberRepositoryV0Test
 * author         : gwangho
 * date           : 2024-06-24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-06-24        dlrhk       최초 생성
 */
@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();
    @Test
    void crud() throws SQLException {
        // save
        Member member = new Member("memberV100", 10000);
        repository.save(member);

        // findById
        Member findMamber = repository.findById(member.getMemberId());
        log.info("findmemberid == {}", findMamber);
        log.info("member == findMember {}", member == findMamber);
        // Member의 @Data 롬복이 equals메서드를 자동으로 생성해서 값을 비교해 주기 때문에 true가 나온다.
        log.info("member == findMember {}", member.equals(findMamber));
        assertThat(findMamber).isEqualTo(member);

        //update money: 10000 -> 20000
        repository.update(member.getMemberId(), 20000);
        Member updateMember = repository.findById(member.getMemberId());
        assertThat(updateMember.getMoney()).isEqualTo(20000);

        //delete
        repository.delete(member.getMemberId());
        // 삭제해서 찾지 못 하니 NoSuchElementException 에러가 터지면 삭제 된게 맞다고 판단
        assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);

    }
}