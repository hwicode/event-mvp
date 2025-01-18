package my.event.practice.infra;

import lombok.extern.slf4j.Slf4j;
import my.event.practice.domain.DuplicateChecker;
import my.event.practice.support.error.CoreException;
import my.event.practice.support.error.ErrorType;

import java.util.ArrayList;
import java.util.List;

// 초기화 로직으로 인해 수동 빈 등록으로 변경
@Slf4j
public class BitSetDuplicateCheckerManager implements DuplicateChecker {

    private final List<BitSetDuplicateChecker> idStores;
    private final long totalSize;
    private final int bitSetSize;

    public BitSetDuplicateCheckerManager(long memberSize, int bitSetSize, List<Long> initMemberIds) {
        this.idStores = new ArrayList<>();

        // 비트셋은 0부터 시작하므로 마지막 값이 포함되지 않는다 -> 마지막 맴버를 포함하기 위해 1을 더함
        this.totalSize = memberSize + 1;
        this.bitSetSize = bitSetSize;
        setUp(this.totalSize, bitSetSize);

        // 초기화: 이미 쿠폰 발행한 유저 추가하기
        initMemberIds.forEach(this::check);
    }

    private void setUp(long memberSize, int bitSetSize) {
        int quotient = (int) (memberSize / bitSetSize);
        int remainder = (int) (memberSize % bitSetSize);

        // 몫만큼 BitSetDuplicateChecker 인스턴스를 추가
        for (int i = 0; i < quotient; i++) {
            idStores.add(new BitSetDuplicateChecker(bitSetSize));
        }

        // 나머지가 있으면 추가로 하나 더 생성
        if (remainder > 0) {
            idStores.add(new BitSetDuplicateChecker(remainder));
        }
    }

    @Override
    public boolean check(Long memberId) {
        if (memberId <= 0 || memberId > totalSize) {
            throw new CoreException(ErrorType.INVALID_MEMBER_ID_ERROR);
        }

        int selectedCheckerIndex = getSelectedCheckerIndex(memberId);
        BitSetDuplicateChecker duplicateChecker = idStores.get(selectedCheckerIndex);

        long adjustedMemberId = getAdjustedMemberId(memberId);
        return duplicateChecker.check(adjustedMemberId);
    }

    int getSelectedCheckerIndex(Long memberId) {
        return (int) (memberId / bitSetSize);
    }

    long getAdjustedMemberId(Long memberId) {
        return memberId % bitSetSize;
    }

    public int getIdStoresSize() {
        return idStores.size();
    }
}
