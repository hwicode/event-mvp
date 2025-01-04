package my.event.practice.web.login;

import my.event.practice.support.error.CoreException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TokenManagerTest {

    @Test
    void 회원_아이디로_토큰을_만들_수_있다() {
        // given
        String memberId = "memberId";
        TokenManager tokenManager = new TokenManager(1000000, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        // when
        String token = tokenManager.createToken(memberId);

        // then
        String tokenMemberId = tokenManager.getMemberId(token);
        assertThat(tokenMemberId).isEqualTo(memberId);
    }

    @Test
    void 잘못된_토큰에서_회원_아이디를_꺼내면_에러가_발생한다() {
        // given
        String token = "token";
        TokenManager tokenManager = new TokenManager(1000000, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        // when then
        assertThatThrownBy(() -> tokenManager.getMemberId(token))
                .isInstanceOf(CoreException.class);
    }

}
