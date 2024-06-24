package hello.springjdbc.domain;

import lombok.Data;

/**
 * packageName    : com.kovo.domain.ticketlink
 * fileName       : Member
 * author         : gwangho
 * date           : 2024-06-24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-06-24        dlrhk       최초 생성
 */
@Data
public class Member {
    private String memberId;
    private int money;

    public Member() {

    }

    public Member(String memberId, int money) {
        this.memberId = memberId;
        this.money = money;
    }
}
