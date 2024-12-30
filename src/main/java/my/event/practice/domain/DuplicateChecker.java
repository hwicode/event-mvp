package my.event.practice.domain;

import java.util.HashSet;
import java.util.Set;

public class DuplicateChecker {

    private final Set<String> idStore;

    public DuplicateChecker() {
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
