package my.event.practice.infra;


import my.event.practice.support.error.CoreException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SynchronizedWaitingQueueTest {

    private static SynchronizedWaitingQueue createWaitingQueue(int repeatCount, int pollingSize) {
        return new SynchronizedWaitingQueue(repeatCount, pollingSize, 1000);
    }

    @Test
    void 대기열에_회원_아이디를_추가할_수_있다() {
        // given
        String memberId = "memberId";
        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(3, 1);

        // when
        int peopleAhead = synchronizedWaitingQueue.add(memberId);

        // then
        assertThat(peopleAhead).isEqualTo(1);
    }

    @Test
    void 대기열에_회원_아이디를_추가하면_대기_순번을_알_수_있다() {
        // given
        int waitingPerson = 3;
        String memberId = "memberId";
        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(3, 1);

        for (int i = 0; i < waitingPerson; i++) {
            synchronizedWaitingQueue.add(memberId + i);
        }

        // when
        int waitingNumber = synchronizedWaitingQueue.add(memberId);

        // then
        assertThat(waitingNumber).isEqualTo(waitingPerson + 1);
    }

    @Test
    void 회원_아이디를_추가할_때_대기열이_닫혀있으면_에러가_발생한다() {
        // given
        String memberId = "memberId";
        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(3, 1);

        // when
        synchronizedWaitingQueue.close();

        // then
        assertThatThrownBy(() -> synchronizedWaitingQueue.add(memberId))
                .isInstanceOf(CoreException.class);
    }

    @Test
    void 대기열에서_회원의_순서를_대략적으로_가져올_수_있다() {
        // given
        String memberId = "memberId";
        int count = 100;

        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(3, 1);

        // when
        for (int i = 1; i <= count; i++) {
            synchronizedWaitingQueue.add(memberId + i);
        }

        // then
        String firstMember = memberId + 1;
        String lastMember = memberId + 100;
        assertThat(synchronizedWaitingQueue.getOrder(firstMember)).isZero();
        assertThat(synchronizedWaitingQueue.getOrder(lastMember)).isEqualTo(99);
    }

    @Test
    void 대기열에서_회원의_순서를_가져올_때_회원이_존재하지_않으면_에러가_발생한다() {
        // given
        String memberId = "memberId";

        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(3, 1);

        // when then
        assertThatThrownBy(() -> synchronizedWaitingQueue.getOrder(memberId))
                .isInstanceOf(CoreException.class);
    }

    @Test
    void 대기열에서_회원의_순서를_가져올_때_대기열이_닫혀있으면_에러가_발생한다() {
        // given
        String memberId = "memberId";
        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(3, 1);

        // when
        synchronizedWaitingQueue.close();

        // then
        assertThatThrownBy(() -> synchronizedWaitingQueue.getOrder(memberId))
                .isInstanceOf(CoreException.class);
    }

    @Test
    void 대기열에서_회원들을_꺼낼_수_있다() {
        // given
        int count = 3;
        int repeatCount = 1;
        String memberId = "memberId";

        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(repeatCount, count);
        for (int i = 0; i < count; i++) {
            synchronizedWaitingQueue.add(memberId + i);
        }

        // when
        List<String> memberIds = synchronizedWaitingQueue.pollMemberIds(repeatCount);

        // then
        assertThat(memberIds).hasSize(count);
    }

    @Test
    void 대기열에서_회원들을_꺼낼_때_대기열이_닫혀있으면_에러가_발생한다() {
        // given
        int count = 3;
        int repeatCount = 1;

        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(repeatCount, count);

        synchronizedWaitingQueue.close();

        // when then
        assertThatThrownBy(() -> synchronizedWaitingQueue.pollMemberIds(repeatCount))
                .isInstanceOf(CoreException.class);
    }

    @Test
    void 대기열에서_회원들을_꺼낼_때_한계_반복_횟수에_도달하지_않고_대기_인원이_적으면_빈_배열을_가져온다() {
        // given
        int repeatCount = 1;
        int pollSize = 3;
        int lessPollSize = pollSize - 1;
        String memberId = "memberId";

        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(repeatCount, pollSize);
        for (int i = 0; i < lessPollSize; i++) {
            synchronizedWaitingQueue.add(memberId + i);
        }

        // when
        List<String> memberIds = synchronizedWaitingQueue.pollMemberIds(0);

        // then
        assertThat(memberIds).isEmpty();
    }

    @Test
    void 대기열에서_회원들을_꺼낼_때_한계_반복_횟수에_도달하지_않고_대기_인원이_충분하면_회원들을_가져온다() {
        // given
        int repeatCount = 1;
        int pollSize = 3;
        String memberId = "memberId";

        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(repeatCount, pollSize);
        for (int i = 0; i < pollSize; i++) {
            synchronizedWaitingQueue.add(memberId + i);
        }

        // when
        List<String> memberIds = synchronizedWaitingQueue.pollMemberIds(0);

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

        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(repeatCount, pollSize);
        for (int i = 0; i < lessPollSize; i++) {
            synchronizedWaitingQueue.add(memberId + i);
        }

        // when
        List<String> memberIds = synchronizedWaitingQueue.pollMemberIds(repeatCount);

        // then
        assertThat(memberIds).hasSize(lessPollSize);
    }

    @Test
    void 대기열에서_회원들을_꺼낼_때_한계_반복_횟수에_도달하고_대기_인원이_0명이면_빈_리스트를_가져온다() {
        // given
        int repeatCount = 1;
        int pollSize = 3;

        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(repeatCount, pollSize);

        // when
        List<String> memberIds = synchronizedWaitingQueue.pollMemberIds(repeatCount);

        // then
        assertThat(memberIds).isEmpty();
    }

    @Test
    void 대기열을_닫을_수_있다() {
        // given
        int repeatCount = 1;
        int pollSize = 3;

        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(repeatCount, pollSize);

        // when
        boolean isClose = synchronizedWaitingQueue.close();

        // then
        assertThat(isClose).isTrue();
    }

    @Test
    void 대기열을_닫혀_있을_때_또_다시_닫을_수_없다() {
        // given
        int repeatCount = 1;
        int pollSize = 3;

        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(repeatCount, pollSize);
        synchronizedWaitingQueue.close();

        // when
        boolean isClose = synchronizedWaitingQueue.close();

        // then
        assertThat(isClose).isFalse();
    }

    @Test
    void 동시에_100명의_유저를_순서대로_대기열에_추가할_수_있다() throws InterruptedException {
        // given
        int repeatCount = 1;
        int pollSize = 3;
        String memberId = "memberId";

        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(repeatCount, pollSize);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 1; i <= threadCount; i++) {
            String newMemberId = "memberId" + i;
            executorService.submit(() -> {
                try {
                    synchronizedWaitingQueue.add(newMemberId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        int waitingNumber = synchronizedWaitingQueue.add(memberId);
        assertThat(waitingNumber).isEqualTo(threadCount + 1);
    }

    @Test
    void 동시에_100명의_유저를_순서대로_대기열에_추가하며_대기열에서_유저를_꺼낼_수_있다() throws InterruptedException {
        // given
        int repeatCount = 1;
        int pollSize = 3;
        String memberId = "memberId";

        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(repeatCount, pollSize);

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
                    synchronizedWaitingQueue.add(newMemberId);
                } finally {
                    latch.countDown();
                }
            });
        }

        // 유저를 꺼냄
        for (int i = 0; i < 2; i++) {
            executorService.submit(() -> {
                try {
                    synchronizedWaitingQueue.pollMemberIds(0);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        int waitingNumber = synchronizedWaitingQueue.add(memberId);
        assertThat(waitingNumber).isEqualTo(threadCount + 1 - (pollCount * pollSize));
    }

    @Test
    void 동시에_3명의_유저가_대기열을_닫으면_최초의_유저의_닫기만_처리된다() throws InterruptedException {
        // given
        int repeatCount = 1;
        int pollSize = 3;

        SynchronizedWaitingQueue synchronizedWaitingQueue = createWaitingQueue(repeatCount, pollSize);

        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 1; i <= threadCount; i++) {
            Future<Boolean> submit = executorService.submit(() -> {
                try {
                    return synchronizedWaitingQueue.close();
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
    }

}
