package my.event.practice.infra;

import my.event.practice.support.error.CoreException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BitSetDuplicateCheckerTest {

    private static BitSetDuplicateChecker createDuplicateChecker(int size) {
        return new BitSetDuplicateChecker(size);
    }

    @ValueSource(longs = {1L, 50L, 99L})
    @ParameterizedTest
    void 회원_아이디가_중복되지_않으면_false를_리턴한다(Long memberId) {
        // given
        int maxSize = 100;
        BitSetDuplicateChecker duplicateChecker = createDuplicateChecker(maxSize);

        // when
        boolean isDuplicate = duplicateChecker.check(memberId);

        // then
        assertThat(isDuplicate).isFalse();
    }

    @ValueSource(longs = {100L, 101L, -1L, -50L})
    @ParameterizedTest
    void 회원_아이디가_유효하지_않으면_예외가_발생한다(Long memberId) {
        // given
        int maxSize = 100;
        BitSetDuplicateChecker duplicateChecker = createDuplicateChecker(maxSize);

        // when then
        assertThatThrownBy(() -> duplicateChecker.check(memberId))
                .isInstanceOf(CoreException.class);
    }

    @Test
    void 회원_아이디가_중복되면_예외가_발생한다() {
        // given
        Long memberId = 1L;
        int maxSize = 100;
        BitSetDuplicateChecker duplicateChecker = createDuplicateChecker(maxSize);

        // when
        duplicateChecker.check(memberId);

        // then
        assertThatThrownBy(() -> duplicateChecker.check(memberId))
                .isInstanceOf(CoreException.class);
    }

    @Test
    void 동시에_100명의_유저가_중복_검사를_할_수_있다() throws InterruptedException {
        // given
        long memberId = 1L;
        int maxSize = 101;
        BitSetDuplicateChecker duplicateChecker = createDuplicateChecker(maxSize);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 1; i <= threadCount; i++) {
            Long newMemberId = memberId + i;
            executorService.submit(() -> {
                try {
                    duplicateChecker.check(newMemberId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        assertThatThrownBy(() -> duplicateChecker.check(memberId + 1))
                .isInstanceOf(CoreException.class);
        assertThatThrownBy(() -> duplicateChecker.check(memberId + 50))
                .isInstanceOf(CoreException.class);
        assertThatThrownBy(() -> duplicateChecker.check(memberId + 100))
                .isInstanceOf(CoreException.class);
    }

}
