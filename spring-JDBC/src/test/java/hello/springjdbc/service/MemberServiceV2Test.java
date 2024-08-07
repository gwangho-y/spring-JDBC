package hello.springjdbc.service;

import hello.springjdbc.domain.Member;
import hello.springjdbc.repository.MemberRepositoryV1;
import hello.springjdbc.repository.MemberRepositoryV2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.springjdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * packageName    : com.kovo.domain.ticketlink
 * fileName       : MemberServiceV1Test
 * author         : gwangho
 * date           : 2024-07-01
 * description    : 트랜잭션 커넥션 전달 방식
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-01        dlrhk       최초 생성
 */
@Slf4j
class MemberServiceV2Test {

    public static final String MEMBER_A= "memberA";
    public static final String MEMBER_B= "memberB";
    public static final String MEMBER_EX= "ex";


    private MemberRepositoryV2 memberRepositoryV2;
    private MemberServiceV2 memberServiceV2;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepositoryV2 = new MemberRepositoryV2(dataSource);
        memberServiceV2 = new MemberServiceV2(dataSource, memberRepositoryV2);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepositoryV2.delete(MEMBER_A);
        memberRepositoryV2.delete(MEMBER_B);
        memberRepositoryV2.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepositoryV2.save(memberA);
        memberRepositoryV2.save(memberB);

        //  when
        log.info("START TX");
        memberServiceV2.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);
        log.info("END EX");

        // then
        Member findMemberA = memberRepositoryV2.findById(memberA.getMemberId());
        Member findMemberB = memberRepositoryV2.findById(memberB.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);

    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void accountTransferEx() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepositoryV2.save(memberA);
        memberRepositoryV2.save(memberEx);

        //  when
        assertThatThrownBy(() -> memberServiceV2.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(),2000));

        // then
        Member findMemberA = memberRepositoryV2.findById(memberA.getMemberId());
        Member findMemberB = memberRepositoryV2.findById(memberEx.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);

    }
}