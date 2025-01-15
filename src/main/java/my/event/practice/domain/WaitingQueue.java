package my.event.practice.domain;

import java.util.List;

public interface WaitingQueue {

    int add(Long memberId);

    int getOrder(Long memberId);

    List<Long> pollMemberIds(int repeatCount);

    boolean close();

    int getRepeatLimit();

    int getSleepMs();
}
