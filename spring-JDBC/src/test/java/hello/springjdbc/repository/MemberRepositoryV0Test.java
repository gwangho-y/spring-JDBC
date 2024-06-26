package hello.springjdbc.repository;

import hello.springjdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
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
        Member member = new Member("memberV3", 10000);
        repository.save(member);


        Member findMamber = repository.findById(member.getMemberId());
        log.info("findmemberid == {}", findMamber);
        log.info("member == findMember {}", member == findMamber);
        // Member의 @Data 롬복이 equals메서드를 자동으로 생성해서 값을 비교해 주기 때문에 true가 나온다.
        log.info("member == findMember {}", member.equals(findMamber));
        assertThat(findMamber).isEqualTo(member);
    }
}