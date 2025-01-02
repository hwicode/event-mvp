package my.event.practice.infra;

import my.event.practice.domain.DuplicateChecker;
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
            throw new RuntimeException();
        }
        idStore.add(memberId);
        return false;
    }
}
