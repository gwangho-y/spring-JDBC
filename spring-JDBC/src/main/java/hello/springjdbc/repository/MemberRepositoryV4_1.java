package hello.springjdbc.repository;

import hello.springjdbc.domain.Member;
import hello.springjdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * packageName    : com.kovo.domain.ticketlink
 * fileName       : MemberRepositoryV0
 * author         : gwangho
 * date           : 2024-06-24
 * description    : 예외 누수 문제 해결, 체크 -> 런타임

 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-06-24        dlrhk       최초 생성
 */
@Slf4j
public class MemberRepositoryV4_1 implements MemberRepository {

    private final DataSource dataSource;

    public MemberRepositoryV4_1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member) {
        // sql 인젝션 예방 , 파라미터 바인딩
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            // executeUpdate는 DB에 영향 받은 row의 수만큼 반환해준다.
            int count = pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            throw new MyDbException(e);
        }finally {
            // 리소스 해제는 반드시 finally에서 실행해줘야지 위에서 해버리면 에러 터졌을 때
            // 클로즈 못 한다.
            close(con, pstmt, null);
        }
    }
    @Override
    public Member findById(String memberId)  {
        String sql = "select * from member where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;

        ResultSet rs= null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }
        }catch (SQLException e) {
            throw new MyDbException(e);
        }
        finally {
            close(con, pstmt, rs);
        }
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();

            // executeUpdate는 DB에 영향 받은 row의 수만큼 반환해준다.
            log.error("resultSize={}", resultSize);

        } catch (SQLException e) {
            throw new MyDbException(e);
        }finally {
            // 리소스 해제는 반드시 finally에서 실행해줘야지 위에서 해버리면 에러 터졌을 때
            // 클로즈 못 한다.
            close(con, pstmt, null);
        }
    }


    public void delete(String memberId)  {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            int resultSize = pstmt.executeUpdate();

            // executeUpdate는 DB에 영향 받은 row의 수만큼 반환해준다.
            log.error("resultSize={}", resultSize);

        } catch (SQLException e) {
            throw new MyDbException(e);
        }finally {
            // 리소스 해제는 반드시 finally에서 실행해줘야지 위에서 해버리면 에러 터졌을 때
            // 클로즈 못 한다.
            close(con, pstmt, null);
        }

    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 함.
        DataSourceUtils.releaseConnection(con,dataSource);
    }

    private Connection getConnection() throws SQLException {
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용한다.
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}