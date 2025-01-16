package my.event.practice.infra;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.BitSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BitSetTest {

    @ValueSource(ints = {0, 10, 50, 63})
    @ParameterizedTest
    void 비트셋_자료구조의_크기가_64면_0부터_63까지의_크기를_가진다(int index) {
        // given
        int size = 64;
        BitSet bitSet = new BitSet(size);

        // when
        bitSet.set(index);

        // then
        assertThat(bitSet.size()).isEqualTo(size);
    }

    @ValueSource(ints = {64, 65, 100, 200, 500})
    @ParameterizedTest
    void 비트셋_자료구조에서_크기를_넘어가는_값을_set하면_크기가_64의_배수로_커진다(int index) {
        // given
        int size = 64;
        BitSet bitSet = new BitSet(size);

        // when
        bitSet.set(index);

        // then
        int expectedSizeMultiplier = index / size + 1;
        assertThat(bitSet.size()).isEqualTo(size * expectedSizeMultiplier);
    }

    @ValueSource(ints = {64, 65, 100, 200, 500})
    @ParameterizedTest
    void 비트셋_자료구조에서_get은_비트셋의_크기와_관련이_없다(int index) {
        // given
        int size = 64;
        BitSet bitSet = new BitSet(size);

        // when
        boolean result = bitSet.get(index);

        // then
        assertThat(bitSet.size()).isEqualTo(size);
        assertThat(result).isFalse();
    }

    @ValueSource(ints = {-1, -50})
    @ParameterizedTest
    void 비트셋_자료구조에_음수를_get_하거나_set_하면_에러가_발생한다(int index) {
        // given
        int size = 64;
        BitSet bitSet = new BitSet(size);

        // when then
        assertThatThrownBy(() -> bitSet.set(index))
                .isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> bitSet.get(index))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

}
