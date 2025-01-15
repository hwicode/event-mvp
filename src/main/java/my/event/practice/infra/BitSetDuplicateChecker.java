package my.event.practice.infra;

import lombok.extern.slf4j.Slf4j;
import my.event.practice.domain.DuplicateChecker;
import my.event.practice.support.error.CoreException;
import my.event.practice.support.error.ErrorType;

import java.util.BitSet;

@Slf4j
public class BitSetDuplicateChecker implements DuplicateChecker {

    private final BitSet idStore;
    private final int maxSize;

    public BitSetDuplicateChecker(int maxSize) {
        this.idStore = new BitSet(maxSize);
        this.maxSize = maxSize;
    }

    @Override
    public synchronized boolean check(Long memberId) {
        if (memberId <= 0 || memberId > maxSize) {
            throw new CoreException(ErrorType.INVALID_MEMBER_ID_ERROR);
        }

        int bitIndex = memberId.intValue();
        if (idStore.get(bitIndex)) {
            throw new CoreException(ErrorType.DUPLICATED_MEMBER_ERROR);
        }
        idStore.set(bitIndex);
        log.info("[BitSetDuplicateChecker][check] Duplicated checker save memberId={}", memberId);
        return false;
    }
}
