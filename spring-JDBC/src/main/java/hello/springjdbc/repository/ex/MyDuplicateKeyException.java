package hello.springjdbc.repository.ex;

/**
 * packageName    : com.kovo.domain.ticketlink
 * fileName       : MyDuplicateKeyException
 * author         : 이광호
 * date           : 2024-07-21
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-21        이광호       최초 생성
 */
public class MyDuplicateKeyException extends MyDbException {

    public MyDuplicateKeyException() {

    }

    public MyDuplicateKeyException(String message) {
        super(message);
    }

    public MyDuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDuplicateKeyException(Throwable cause) {
        super(cause);
    }
}
