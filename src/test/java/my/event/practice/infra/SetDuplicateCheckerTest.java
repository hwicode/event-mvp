package my.event.practice.infra;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SetDuplicateCheckerTest {

    private static SetDuplicateChecker createDuplicateChecker() {
        return new SetDuplicateChecker();
    }

    @Test
    void 회원_아이디가_중복되지_않으면_false를_리턴한다() {
        // given
        String memberId = "memberId";
        SetDuplicateChecker setDuplicateChecker = createDuplicateChecker();

        // when
        boolean isDuplicate = setDuplicateChecker.check(memberId);

        // then
        assertThat(isDuplicate).isFalse();
    }

    @Test
    void 회원_아이디가_중복되면_예외가_발생한다() {
        // given
        String memberId = "memberId";
        SetDuplicateChecker setDuplicateChecker = createDuplicateChecker();

        // wnen
        setDuplicateChecker.check(memberId);

        // then
        assertThatThrownBy(() -> setDuplicateChecker.check(memberId))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void 동시에_100명의_유저가_중복_검사를_할_수_있다() throws InterruptedException {
        // given
        String memberId = "memberId";
        SetDuplicateChecker setDuplicateChecker = createDuplicateChecker();

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 1; i <= threadCount; i++) {
            String newMemberId = "memberId" + i;
            executorService.submit(() -> {
                try {
                    setDuplicateChecker.check(newMemberId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        assertThatThrownBy(() -> setDuplicateChecker.check(memberId + 1))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> setDuplicateChecker.check(memberId + 50))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> setDuplicateChecker.check(memberId + 100))
                .isInstanceOf(RuntimeException.class);
    }
}
