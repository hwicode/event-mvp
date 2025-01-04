package my.event.practice.infra;

import my.event.practice.domain.DuplicateChecker;
import my.event.practice.support.error.CoreException;
import my.event.practice.support.error.ErrorType;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

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
        return false;
    }
}
