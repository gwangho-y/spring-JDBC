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