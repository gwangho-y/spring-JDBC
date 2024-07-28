package hello.springjdbc.repository;

import hello.springjdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * packageName    : com.kovo.domain.ticketlink
 * fileName       : MemberRepositoryV0
 * author         : gwangho
 * date           : 2024-06-24
 * description    : JdbcTemplate 사용

 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-06-24        dlrhk       최초 생성
 */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository {

    private final JdbcTemplate template;

    public MemberRepositoryV5(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        // sql 인젝션 예방 , 파라미터 바인딩
        String sql = "insert into member(member_id, money) values (?, ?)";
        int update = template.update(sql, member.getMemberId(), member.getMoney());

        return member;
    }
    @Override
    public Member findById(String memberId)  {
        String sql = "select * from member where member_id=?";
        return template.queryForObject(sql, memberRowMapper(), memberId);
    }


    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";
        template.update(sql, money, memberId);
    }


    public void delete(String memberId)  {
        String sql = "delete from member where member_id=?";
        template.update(sql, memberId);
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
    }
}
