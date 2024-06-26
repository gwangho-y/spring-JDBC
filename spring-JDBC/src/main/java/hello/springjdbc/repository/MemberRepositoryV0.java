package hello.springjdbc.repository;

import hello.springjdbc.connection.DBConnectionUtil;
import hello.springjdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * packageName    : com.kovo.domain.ticketlink
 * fileName       : MemberRepositoryV0
 * author         : gwangho
 * date           : 2024-06-24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-06-24        dlrhk       최초 생성
 */
@Slf4j
public class MemberRepositoryV0 {

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

    private void close(Connection con, Statement stmt, ResultSet rs) {

        if (rs != null ) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                log.info("error", e);
            }
        }

        if (con!=null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
                log.info("error", e);
            }
        }


    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
