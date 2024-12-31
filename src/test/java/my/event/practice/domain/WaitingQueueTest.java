package my.event.practice.domain;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

class WaitingQueueTest {

    private static WaitingQueue createWaitingQueue(int repeatCount, int pollingSize) {
        return new WaitingQueue(repeatCount, pollingSize);
    }

    @Test
    void 대기열에_회원_아이디를_추가할_수_있다() {
        // given
        String memberId = "memberId";
        WaitingQueue waitingQueue = createWaitingQueue(3, 1);

        // when
        int peopleAhead = waitingQueue.add(memberId);

        // then
        assertThat(peopleAhead).isEqualTo(1);
    }

    @Test
    void 대기열에_회원_아이디를_추가하면_대기_순번을_알_수_있다() {
        // given
        int waitingPerson = 3;
        String memberId = "memberId";
        WaitingQueue waitingQueue = createWaitingQueue(3, 1);

        for (int i = 0; i < waitingPerson; i++) {
            waitingQueue.add(memberId + i);
        }

        // when
        int waitingNumber = waitingQueue.add(memberId);

        // then
        assertThat(waitingNumber).isEqualTo(waitingPerson + 1);
    }

    @Test
    void 회원_아이디를_추가할_때_대기열이_닫혀있으면_에러가_발생한다() {
        // given
        String memberId = "memberId";
        WaitingQueue waitingQueue = createWaitingQueue(3, 1);

        // when
        waitingQueue.close();

        // then
        assertThatThrownBy(() -> waitingQueue.add(memberId))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void 대기열에서_회원의_순서를_대략적으로_가져올_수_있다() {
        // given
        String memberId = "memberId";
        int count = 100;

        WaitingQueue waitingQueue = createWaitingQueue(3, 1);

        // when
        for (int i = 1; i <= count; i++) {
            waitingQueue.add(memberId + i);
        }

        // then
        String firstMember = memberId + 1;
        String lastMember = memberId + 100;
        assertThat(waitingQueue.getOrder(firstMember)).isZero();
        assertThat(waitingQueue.getOrder(lastMember)).isEqualTo(99);
    }

    @Test
    void 대기열에서_회원의_순서를_가져올_때_회원이_존재하지_않으면_에러가_발생한다() {
        // given
        String memberId = "memberId";

        WaitingQueue waitingQueue = createWaitingQueue(3, 1);

        // when then
        assertThatThrownBy(() -> waitingQueue.getOrder(memberId))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void 대기열에서_회원의_순서를_가져올_때_대기열이_닫혀있으면_에러가_발생한다() {
        // given
        String memberId = "memberId";

        WaitingQueue waitingQueue = createWaitingQueue(3, 1);
        waitingQueue.add(memberId);

        // when
        waitingQueue.close();

        // then
        assertThatThrownBy(() -> waitingQueue.getOrder(memberId))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void 대기열에서_회원들을_꺼낼_수_있다() {
        // given
        int count = 3;
        int repeatCount = 1;
        String memberId = "memberId";

        WaitingQueue waitingQueue = createWaitingQueue(repeatCount, count);
        for (int i = 0; i < count; i++) {
            waitingQueue.add(memberId + i);
        }

        // when
        List<String> memberIds = waitingQueue.pollMemberIds(repeatCount);

        // then
        assertThat(memberIds).hasSize(count);
    }

    @Test
    void 대기열에서_회원들을_꺼낼_때_대기열이_닫혀있으면_에러가_발생한다() {
        // given
        int count = 3;
        int repeatCount = 1;
        String memberId = "memberId";

        WaitingQueue waitingQueue = createWaitingQueue(repeatCount, count);
        for (int i = 0; i < count; i++) {
            waitingQueue.add(memberId + i);
        }

        waitingQueue.close();

        // when then
        assertThatThrownBy(() -> waitingQueue.pollMemberIds(repeatCount))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void 대기열에서_회원들을_꺼낼_때_한계_반복_횟수에_도달하지_않고_대기_인원이_적으면_빈_배열을_가져온다() {
        // given
        int repeatCount = 1;
        int pollSize = 3;
        int lessPollSize = pollSize - 1;
        String memberId = "memberId";

        WaitingQueue waitingQueue = createWaitingQueue(repeatCount, pollSize);
        for (int i = 0; i < lessPollSize; i++) {
            waitingQueue.add(memberId + i);
        }

        // when
        List<String> memberIds = waitingQueue.pollMemberIds(0);

        // then
        assertThat(memberIds).isEmpty();
    }

    @Test
    void 대기열에서_회원들을_꺼낼_때_한계_반복_횟수에_도달하지_않고_대기_인원이_충분하면_회원들을_가져온다() {
        // given
        int repeatCount = 1;
        int pollSize = 3;
        String memberId = "memberId";

        WaitingQueue waitingQueue = createWaitingQueue(repeatCount, pollSize);
        for (int i = 0; i < pollSize; i++) {
            waitingQueue.add(memberId + i);
        }

        // when
        List<String> memberIds = waitingQueue.pollMemberIds(0);

        // then
        assertThat(memberIds).hasSize(pollSize);
    }

    @Test
    void 대기열에서_회원들을_꺼낼_때_한계_반복_횟수에_도달하면_대기_인원이_적어도_가져온다() {
        // given
        int repeatCount = 1;
        int pollSize = 3;
        int lessPollSize = pollSize - 1;
        String memberId = "memberId";

        WaitingQueue waitingQueue = createWaitingQueue(repeatCount, pollSize);
        for (int i = 0; i < lessPollSize; i++) {
            waitingQueue.add(memberId + i);
        }

        // when
        List<String> memberIds = waitingQueue.pollMemberIds(repeatCount);

        // then
        assertThat(memberIds).hasSize(lessPollSize);
    }

    @Test
    void 대기열을_닫을_수_있다() {
        // given
        int repeatCount = 1;
        int pollSize = 3;

        WaitingQueue waitingQueue = createWaitingQueue(repeatCount, pollSize);

        // when
        boolean isClose = waitingQueue.close();

        // then
        assertThat(isClose).isTrue();
    }

    @Test
    void 대기열을_닫혀_있을_때_또_다시_닫을_수_없다() {
        // given
        int repeatCount = 1;
        int pollSize = 3;

        WaitingQueue waitingQueue = createWaitingQueue(repeatCount, pollSize);
        waitingQueue.close();

        // when
        boolean isClose = waitingQueue.close();

        // then
        assertThat(isClose).isFalse();
    }

    @Test
    void 동시에_100명의_유저를_순서대로_대기열에_추가할_수_있다() throws InterruptedException {
        // given
        int repeatCount = 1;
        int pollSize = 3;
        String memberId = "memberId";

        WaitingQueue waitingQueue = createWaitingQueue(repeatCount, pollSize);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 1; i <= threadCount; i++) {
            String newMemberId = "memberId" + i;
            executorService.submit(() -> {
                try {
                    waitingQueue.add(newMemberId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        int waitingNumber = waitingQueue.add(memberId);
        assertThat(waitingNumber).isEqualTo(threadCount + 1);
    }

    @Test
    void 동시에_100명의_유저를_순서대로_대기열에_추가하며_대기열에서_유저를_꺼낼_수_있다() throws InterruptedException {
        // given
        int repeatCount = 1;
        int pollSize = 3;
        String memberId = "memberId";

        WaitingQueue waitingQueue = createWaitingQueue(repeatCount, pollSize);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        int pollCount = 2;

        CountDownLatch latch = new CountDownLatch(threadCount + pollCount);

        // when
        for (int i = 1; i <= threadCount; i++) {
            String newMemberId = "memberId" + i;
            // 대기열에 유저 추가
            executorService.submit(() -> {
                try {
                    waitingQueue.add(newMemberId);
                } finally {
                    latch.countDown();
                }
            });
        }

        // 유저를 꺼냄
        for (int i = 0; i < 2; i++) {
            executorService.submit(() -> {
                try {
                    waitingQueue.pollMemberIds(0);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        int waitingNumber = waitingQueue.add(memberId);
        assertThat(waitingNumber).isEqualTo(threadCount + 1 - (pollCount * pollSize));
    }

    @Test
    void 동시에_3명의_유저가_대기열을_닫으면_최초의_유저의_닫기만_처리된다() throws InterruptedException {
        // given
        int repeatCount = 1;
        int pollSize = 3;

        WaitingQueue waitingQueue = createWaitingQueue(repeatCount, pollSize);

        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 1; i <= threadCount; i++) {
            Future<Boolean> submit = executorService.submit(() -> {
                try {
                    return waitingQueue.close();
                } finally {
                    latch.countDown();
                }
            });
            futures.add(submit);
        }

        latch.await();

        // then
        List<Boolean> result = futures.stream()
                        .map(future -> {
                            try {
                                return future.get();
                            } catch (Exception e) {
                                return null;
                            }
                        }).toList();

        assertThat(result).hasSize(threadCount)
                .containsOnlyOnce(Boolean.TRUE)
                .contains(Boolean.FALSE)
                .doesNotContain((Boolean) null);

        assertThat(waitingQueue.isClose()).isTrue();
    }

}
