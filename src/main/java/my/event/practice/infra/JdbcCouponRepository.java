package my.event.practice.infra;

import lombok.RequiredArgsConstructor;
import my.event.practice.domain.Coupon;
import my.event.practice.domain.CouponRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

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
                    ps.setString(1, coupon.memberId());
                    ps.setTimestamp(2, Timestamp.valueOf(time.now()));
                }
        );
        return coupons;
    }

    public Optional<Coupon> findByMemberId(String memberId) {
        String sql = "SELECT member_id, created_at FROM coupon WHERE member_id = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, couponRowMapper(), memberId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private RowMapper<Coupon> couponRowMapper() {
        return (rs, rowNum) -> new Coupon(rs.getString("member_id"));
    }
}
