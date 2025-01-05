package my.event.practice.infra;

import lombok.extern.slf4j.Slf4j;
import my.event.practice.domain.DuplicateChecker;
import my.event.practice.support.error.CoreException;
import my.event.practice.support.error.ErrorType;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class SetDuplicateChecker implements DuplicateChecker {

    private final Set<String> idStore;

    public SetDuplicateChecker() {
        this.idStore = new HashSet<>();
    }

    public synchronized boolean check(String memberId) {
        if (idStore.contains(memberId)) {
            throw new CoreException(ErrorType.DUPLICATED_MEMBER_ERROR);
        }
        idStore.add(memberId);
        log.info("[SetDuplicateChecker][check] Duplicated checker save memberId={}", memberId);
        return false;
    }
}
