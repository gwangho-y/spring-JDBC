## JDBC 등장 이유

앱 서버와 mysql DB를 연결해서 데이터를 주고 받는다고 하면 보통 아래와 같은 로직일거다.

![/images/1.png](/images/1.png)

- 커넥션 연결

  주로 TCP/IP를 사용해서 커넥션을 연결한다.

- SQL 전달

  애플리케이션 서버는 DB가 이해할 수 있는 SQL을 연결된 커넥션을 통해 DB에 전달한다

- 결과 응답

  DB는 전달된 SQL을 수행하고 그 결과를 응답.


### DB를 교체해야할 때는 어떨까?


![/images/2.png](/images/2.png)
두 가지 문제가 발생

1. mysql → oracle 변경 됨에 따라 사용 코드도 함께 변경해야한다.
2. 각각의 db마다, 연결, sql 전달, 그리고 응답 받는 방법을 새로 학습해야한다.

이런 문제를 해결하기 위해 JDBC가 등장한다.

### JDBC 표준 인터페이스


![/images/3.png](/images/3.png)
JDBC는 3가지를 표준 인터페이스로 정의해서 제공한다.

- `java.sql.Connection` - 연결
- `java.sql.Statement` - SQL을 담은 내용
- `java.sql.ResultSet` - SQL 요청 응답

개발자는 이 표준 인터페이스만 사용해서 개발하면 된다.

또한 각각의 DB에 맞게 JDBC 인터페이스를 라이브러리로 제공한다. 이걸 JDBC 드라이버라고한다.

mysql jsbc 드라이버, oracle jdbc 드라이버가 따로 있다.

아래 이미지 보면 이해간다.


![/images/4.png](/images/4.png)
JDBC의 등장으로 인해 위에서 말한 DB 교체에 따른 2가지 문제를 해결할 수 있다.

### 한계

- 각각의 DB마다 SQL, 데이터 타입 등의 일부 사용법이 다르다.
- 실무에서 사용하는 페이징 SQL은 각각의 DB마다 사용법이 다르다고 한다.
- 즉, DB 변경시 JDBC코드는 변경하지 않아도 되지만 기능 구현을 위해 사용한 SQL은 해당 DB에 맞게 변경하는 상황이 발생.

## JDBC와 최신 데이터 접근 기술

순수 JDBC 는 1997년에 출시된 오래된 기술. 최근에는 SQL mapper와 ORM 기술 있다.

![/images/4.png](/images/5.png)

- SQL mapper
    - 장점
        - sql 응답 결과를 객체로 편리하게 변환해준다.
        - JDBC 반복 코드를 제거해준다.
    - 단점

      개발자가 SQL을 직접 작성해야한다.

    - 대표기술
        - 스프링 JSBC Template
        - Mybatis

![/images/4.png](/images/6.png)

- ORM 기술
    - RDBMS 테이블과 매핑해주며 SQL을 동적으로 만들어 실행해준다. 각각의 DB 마다 다른 sql을 사용하는 문제도 중간에서 해결해준다.
    - 대표기술
        - JPA
        - 하이버네이트
        - 이클립스링크


## JDBC DriverManager 연결 이해

### JDBC 커넥션 인터페이스와 구현

![/images/7.png](/images/7.png)
- JDBC는 `java.sql.Connection` 표준 커넥션 인터페이스를 정의한다
- H2 데이터베이스 드라이버는 JDBC Connection 인터페이스를 구현 `org.h2.jdbc.JdbcConnection`구현체를 제공한다

### DriverManager 커넥션 요청 흐름

![/images/8.png](/images/8.png)
**JDBC의 DriverManager** 는 라이브러리에 등록된 DB 드라이버들을 관리하고, 커넥션을 획득하는 기능을 제공한다.

1. 커넥션이 필요하면 `DriverManager.getConnection`호출
2. `DriverManager` 는 라이브러리에 등록된 드라이버 목록을 자동으로 인식. 아래 순서로 커넥션 획득 가능한지 확인한다.
    1. URL : `jdbc:h2:tcp://localhost/~/test`
    2. 이름, 비밀번호 등 접속에 필요한 추가 정보
    3. 각각의 드라이버는 URL 정보를 체크해서 본인이 처리할 수 있는 요청인지 확인한다.

       `jdbc:h2` 로 시작하면 h2 드라이버가 커넥션을 획득하고 클라이언트에 반환한다.

3. 2에서 찾은 커넥션 쿠현체가 클라이언트 반환된다.

## JDBC 개발 - 등록

```java
@Slf4j
public class MemberRepositoryV0 {
    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally {

            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {

        if (rs != null ) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
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
```

### save() - SQL 전달

- sql : 데이터베이스에 전달할 SQL을 정의한다. 여기서는 데이터를 등록해야 하므로 insert sql 을 준비했다.
- con.prepareStatement(sql) : 데이터베이스에 전달할 SQL과 파라미터로 전달할 데이터들을 준비한다.
    - `pstmt.setString(1, member.getMemberId())` : SQL의 첫번째 ? 에 값을 지정한다. 문자이므
      로 setString 을 사용한다
    - `pstmt.setInt(2, member.getMoney())` : SQL의 두번째 ? 에 값을 지정한다. Int 형 숫자이므로
      setInt 를 지정한다
    - pstmt.executeUpdate() : Statement 를 통해 준비된 SQL을 커넥션을 통해 실제 데이터베이스에 전달한다. 참고로 executeUpdate() 은 int 를 반환하는데 영향받은 DB row 수를 반환한다. 여기서는 하나의 row를 등록했으므로 1을 반환한다

  > 참고
  >
  >
  > PreparedStatement 는 Statement 의 자식 타입인데, ? 를 통한 파라미터 바인딩을 가능하게 해준다.
  > SQL Injection 공격을 예방하려면 PreparedStatement 를 통한 파라미터 바인딩 방식을 사용해야한다.
  >
  > 문자 더하기로 문자열을 그대로 집어 넣어버리면 Sql 인젝션 당하기 때문에 ?로 바인딩을 해야한다.



## JDBC 개발 - 조회

### ResultSet

- selecet 쿼리의 결과가 순서대로 들어간다.
    - select member_id, money 라고 지정하면 member_id , money 라는 이름으로 데이터
      가 저장된다.
- `ResultSet` 내부에 있는 커서( cursor )를 이동해서 다음 데이터를 조회할 수 있다
- `rs.next()` : 이것을 호출하면 커서가 다음으로 이동한다. 참고로 최초의 커서는 데이터를 가리키고 있지 않기 때문에 rs.next() 를 최초 한번은 호출해야 데이터를 조회할 수 있다.
    - rs.next() 의 결과가 true 면 커서의 이동 결과 데이터가 있다는 뜻이다


## JDBC 개발 - 수정, 삭제

```java
//update money: 10000 -> 20000
repository.update(member.getMemberId(), 20000);
Member updateMember = repository.findById(member.getMemberId());
assertThat(updateMember.getMoney()).isEqualTo(20000);

//delete
repository.delete(member.getMemberId());
// 삭제해서 찾지 못 하니 NoSuchElementException 에러가 터지면 삭제 된게 맞다고 판단
assertThatThrownBy(() -> repository.findById(member.getMemberId()))
        .isInstanceOf(NoSuchElementException.class);
```

- 삭제의 경우 테스트는 `assertThatThrownBy` 를 사용해서 `NoSuchElementException` 에러와 같으면 삭제가 된 것으로 판단한다.