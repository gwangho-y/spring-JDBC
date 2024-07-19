package hello.springjdbc.repository;

import hello.springjdbc.domain.Member;

import java.sql.SQLException;

/**
 * packageName    : com.kovo.domain.ticketlink
 * fileName       : MemberRepository
 * author         : 이광호
 * date           : 2024-07-19
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-19        이광호       최초 생성
 */
public interface MemberRepository {
    Member save(Member member);
    Member findById(String memberId);
    void update(String memberId, int money);
    void delete(String memberId);
}
