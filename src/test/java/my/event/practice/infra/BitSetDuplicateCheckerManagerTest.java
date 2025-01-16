package my.event.practice.infra;

import my.event.practice.support.error.CoreException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BitSetDuplicateCheckerManagerTest {

    private static BitSetDuplicateCheckerManager createDuplicateChecker(long memberSize, int bitSetSize) {
        return new BitSetDuplicateCheckerManager(memberSize, bitSetSize);
    }

    @MethodSource("provideMemberSizeAndResult")
    @ParameterizedTest
    void 비트셋_매니저는_비트셋의_생성_개수를_알맞게_조절할_수_있다(long memberSize, int result) {
        // given
        int bitSetSize = 64;
        BitSetDuplicateCheckerManager duplicateChecker = createDuplicateChecker(memberSize, bitSetSize);

        // when
        int idStoresSize = duplicateChecker.getIdStoresSize();

        // then
        assertThat(idStoresSize).isEqualTo(result);
    }

    private static Stream<Arguments> provideMemberSizeAndResult() {
        return Stream.of(
                arguments(10_000, 157),
                arguments(100, 2),
                arguments(30_000, 469),
                arguments(100_000_000, 1_562_501),
                arguments(640, 11),
                arguments(641, 11),
                arguments(639, 10)
        );
    }

    @ValueSource(longs = {101, -1, -50, 0})
    @ParameterizedTest
    void 회원_아이디가_유효하지_않으면_예외가_발생한다(long memberId) {
        // given
        long memberSize = 100;
        int bitSetSize = 64;
        BitSetDuplicateCheckerManager duplicateChecker = createDuplicateChecker(memberSize, bitSetSize);

        // when then
        assertThatThrownBy(() -> duplicateChecker.check(memberId))
                .isInstanceOf(CoreException.class);
    }

    @MethodSource("provideMemberIdAndCheckerIndex")
    @ParameterizedTest
    void 비트셋_매니저는_회원_아이디에_맞는_비트셋을_가져올_수_있다(long memberId, int checkerIndex) {
        // given
        long memberSize = 10_000;
        int bitSetSize = 64;
        BitSetDuplicateCheckerManager duplicateChecker = createDuplicateChecker(memberSize, bitSetSize);

        // when
        int selectedCheckerIndex = duplicateChecker.getSelectedCheckerIndex(memberId);

        // then
        assertThat(selectedCheckerIndex).isEqualTo(checkerIndex);
    }

    private static Stream<Arguments> provideMemberIdAndCheckerIndex() {
        return Stream.of(
                arguments(10_000, 156),
                arguments(100, 1),
                arguments(640, 10),
                arguments(641, 10),
                arguments(639, 9)
        );
    }

    @MethodSource("provideMemberIdAndMemberIndex")
    @ParameterizedTest
    void 비트셋_매니저는_회원_아이디에_맞는_비트셋내의_위치를_가져올_수_있다(long memberId, long memberIndex) {
        // given
        long memberSize = 10_000;
        int bitSetSize = 64;
        BitSetDuplicateCheckerManager duplicateChecker = createDuplicateChecker(memberSize, bitSetSize);

        // when
        long adjustedMemberId = duplicateChecker.getAdjustedMemberId(memberId);

        // then
        assertThat(adjustedMemberId).isEqualTo(memberIndex);
    }

    private static Stream<Arguments> provideMemberIdAndMemberIndex() {
        return Stream.of(
                arguments(10_000, 16),
                arguments(100, 36),
                arguments(640, 0),
                arguments(641, 1),
                arguments(639, 63)
        );
    }
}
