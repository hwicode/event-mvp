package my.event.practice.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.event.practice.domain.Coupon;
import my.event.practice.domain.CouponRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class JdbcCouponRepository implements CouponRepository {

    private final JdbcTemplate jdbcTemplate;
    private final Time time;

    @Override
    public List<Coupon> saveAll(List<Coupon> coupons) {
        String sql = "INSERT INTO coupon (member_id, created_at) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(
                sql, coupons, coupons.size(),
                (ps, coupon) -> {
                    ps.setLong(1, coupon.getMemberId());
                    ps.setTimestamp(2, Timestamp.valueOf(time.now()));
                }
        );
        log.info("[JdbcCouponRepository][saveAll] Coupons are saved");
        return coupons;
    }

    public Optional<Coupon> findByMemberId(Long memberId) {
        String sql = "SELECT member_id FROM coupon WHERE member_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, couponRowMapper(), memberId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Coupon> findAll() {
        String sql = "SELECT member_id FROM coupon";
        return jdbcTemplate.query(sql, couponRowMapper());
    }

    private RowMapper<Coupon> couponRowMapper() {
        return (rs, rowNum) -> new Coupon(rs.getLong("member_id"));
    }
}
