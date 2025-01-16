package my.event.practice.infra;

import lombok.extern.slf4j.Slf4j;
import my.event.practice.domain.DuplicateChecker;
import my.event.practice.support.error.CoreException;
import my.event.practice.support.error.ErrorType;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
//@Component -> 스프링 컨테이너에 없음
public class SetDuplicateChecker implements DuplicateChecker {

    private final Set<Long> idStore;

    public SetDuplicateChecker() {
        this.idStore = new HashSet<>();
    }

    public synchronized boolean check(Long memberId) {
        if (idStore.contains(memberId)) {
            throw new CoreException(ErrorType.DUPLICATED_MEMBER_ERROR);
        }
        idStore.add(memberId);
        log.info("[SetDuplicateChecker][check] Duplicated checker save memberId={}", memberId);
        return false;
    }
}
