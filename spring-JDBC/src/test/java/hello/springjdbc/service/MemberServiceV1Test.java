package hello.springjdbc.service;

import hello.springjdbc.domain.Member;
import hello.springjdbc.repository.MemberRepositoryV1;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.net.PasswordAuthentication;
import java.sql.SQLException;

import static hello.springjdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * packageName    : com.kovo.domain.ticketlink
 * fileName       : MemberServiceV1Test
 * author         : gwangho
 * date           : 2024-07-01
 * description    : 기본 동작, 트랜잭션이 없어서 문제 발생
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-01        dlrhk       최초 생성
 */
class MemberServiceV1Test {

    public static final String MEMBER_A= "memberA";
    public static final String MEMBER_B= "memberB";
    public static final String MEMBER_EX= "ex";


    private MemberRepositoryV1 memberRepositoryV1;
    private MemberServiceV1 memberServiceV1;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepositoryV1 = new MemberRepositoryV1(dataSource);
        memberServiceV1 = new MemberServiceV1(memberRepositoryV1);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepositoryV1.delete(MEMBER_A);
        memberRepositoryV1.delete(MEMBER_B);
        memberRepositoryV1.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepositoryV1.save(memberA);
        memberRepositoryV1.save(memberB);

        //  when
        memberServiceV1.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        // then
        Member findMemberA = memberRepositoryV1.findById(memberA.getMemberId());
        Member findMemberB = memberRepositoryV1.findById(memberB.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);

    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void accountTransferEx() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepositoryV1.save(memberA);
        memberRepositoryV1.save(memberEx);

        //  when
        assertThatThrownBy(() -> memberServiceV1.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(),2000));

        // then
        Member findMemberA = memberRepositoryV1.findById(memberA.getMemberId());
        Member findMemberB = memberRepositoryV1.findById(memberEx.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);

    }
}