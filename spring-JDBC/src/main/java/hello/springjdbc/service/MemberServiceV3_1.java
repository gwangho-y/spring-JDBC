package hello.springjdbc.service;

import hello.springjdbc.domain.Member;
import hello.springjdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

/**
 * packageName    : com.kovo.domain.ticketlink
 * fileName       : MemberServiceV1
 * author         : gwangho
 * date           : 2024-07-01
 * description    : 트랜잭션 - 파라메터 연동, 풀을 고려한 종료
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-01        dlrhk       최초 생성
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_1 {

//    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;

    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());


        try {

            bizLogic( fromId, toId, money);

            transactionManager.commit(status);

        } catch (Exception e) {

            transactionManager.rollback(status);

            throw new IllegalStateException(e);
        }


    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);

        Member toMember = memberRepository.findById( toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update( toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
