package hello.springjdbc.repository;

import hello.springjdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * packageName    : com.kovo.domain.ticketlink
 * fileName       : MemberRepositoryV0
 * author         : gwangho
 * date           : 2024-06-24
 * description    : 커넥션을 파라메터로
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-06-24        dlrhk       최초 생성
 */
@Slf4j
public class MemberRepositoryV2 {

    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
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
            log.error("db error", e);
            throw e;
        }finally {
            // 리소스 해제는 반드시 finally에서 실행해줘야지 위에서 해버리면 에러 터졌을 때
            // 클로즈 못 한다.
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
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
            log.error("db error", e);
            throw e;
        }
        finally {
            close(con, pstmt, rs);
        }
    }

    public Member findById(Connection con, String memberId) throws SQLException {
        String sql = "select * from member where member_id=?";

        PreparedStatement pstmt = null;

        ResultSet rs= null;

        try {
//            con = getConnection();   // 새로운 커넥션을 호출하고, 새로운 트랜잭션 시작 되기 때문에 이건 getConnection 쓰면 클남.
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
            log.error("db error", e);
            throw e;
        }
        finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
        }
    }

    public void update(String memberId, int money) throws SQLException {
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
            log.error("db error", e);
            throw e;
        }finally {
            // 리소스 해제는 반드시 finally에서 실행해줘야지 위에서 해버리면 에러 터졌을 때
            // 클로즈 못 한다.
            close(con, pstmt, null);
        }
    }

    public void update(Connection con, String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";
        PreparedStatement pstmt = null;

        try{
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();

            // executeUpdate는 DB에 영향 받은 row의 수만큼 반환해준다.
            log.error("resultSize={}", resultSize);

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally {
            JdbcUtils.closeStatement(pstmt);
        }
    }

    public void delete(String memberId) throws SQLException {
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
            log.error("db error", e);
            throw e;
        }finally {
            // 리소스 해제는 반드시 finally에서 실행해줘야지 위에서 해버리면 에러 터졌을 때
            // 클로즈 못 한다.
            close(con, pstmt, null);
        }

    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        // JdbcUtils 의 closeConnection은 커넥션을 종료하지만 커넥션 풀에 의해 관리되는 커넥션의 경우는 종료가 아니라
        // 커넥션 풀에 안전하게 반환하는 동작을 수행한다.
//        JdbcUtils.closeConnection(con); // 트랜잭션 사용시 커넥션도 켜기서 닫지 않는다. 반환하면 안 되니깐
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}",con, con.getClass());
        return con;
    }
}
