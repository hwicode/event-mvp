package my.event.practice.domain;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class WaitingQueuePollerTest {

    private static WaitingQueue createWaitingQueue(int repeatLimit) {
        WaitingQueue waitingQueue = mock(WaitingQueue.class);
        when(waitingQueue.getRepeatLimit()).thenReturn(repeatLimit);
        when(waitingQueue.getSleepMs()).thenReturn(1);
        return waitingQueue;
    }

    @Test
    void 대기열_poller에서_회원들을_가져올_수_있다() {
        // given
        WaitingQueue waitingQueue = createWaitingQueue(3);
        when(waitingQueue.pollMemberIds(1)).thenReturn(List.of("member1"));

        WaitingQueuePoller waitingQueuePoller = new WaitingQueuePoller(waitingQueue);

        // when
        List<String> memberIds = waitingQueuePoller.poll();

        // then
        assertThat(memberIds).hasSize(1);
        verify(waitingQueue, times(1)).pollMemberIds(1);
    }

    @Test
    void 대기열_poller에서_재시도를_진행하여_회원들을_가져올_수_있다() {
        // given
        WaitingQueue waitingQueue = createWaitingQueue(3);
        when(waitingQueue.pollMemberIds(1)).thenReturn(Collections.emptyList());
        when(waitingQueue.pollMemberIds(2)).thenReturn(List.of("member1"));

        WaitingQueuePoller waitingQueuePoller = new WaitingQueuePoller(waitingQueue);

        // when
        List<String> memberIds = waitingQueuePoller.poll();

        // then
        assertThat(memberIds).hasSize(1);
        verify(waitingQueue, times(1)).pollMemberIds(1);
        verify(waitingQueue, times(1)).pollMemberIds(2);
    }

    @Test
    void 대기열_poller에서_재시도가_한계치에_도달하면_빈_리스트를_가져온다() {
        // given
        WaitingQueue waitingQueue = createWaitingQueue(3);
        when(waitingQueue.pollMemberIds(anyInt())).thenReturn(Collections.emptyList());

        WaitingQueuePoller waitingQueuePoller = new WaitingQueuePoller(waitingQueue);

        // when
        List<String> memberIds = waitingQueuePoller.poll();

        // then
        assertThat(memberIds).isEmpty();
        verify(waitingQueue, times(3)).pollMemberIds(anyInt());
    }

}
