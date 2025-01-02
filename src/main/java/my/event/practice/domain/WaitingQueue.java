package my.event.practice.domain;

import java.util.List;

public interface WaitingQueue {

    int add(String memberId);

    int getOrder(String memberId);

    List<String> pollMemberIds(int repeatCount);

    boolean close();

    boolean isClose();

    int getRepeatLimit();

    int getSleepMs();
}
