package my.event.practice.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.event.practice.domain.CouponIssueService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@EnableScheduling
@Component
public class ScheduledWorker {

    private final CouponIssueService couponIssueService;

    // 단일 스레드에서 스케줄링 작업들을 순차적으로 처리
    // 쿠폰 발행이 끝나면, 1초 후에 다시 쿠폰 발행 실행
    @Scheduled(fixedDelay = 1000)
    public void issueCoupons() {
        log.info("[ScheduledWorker][issueCoupons] Start scheduled coupon issue");
        couponIssueService.issueCoupons();
        log.info("[ScheduledWorker][issueCoupons] End scheduled coupon issue");
    }
}
