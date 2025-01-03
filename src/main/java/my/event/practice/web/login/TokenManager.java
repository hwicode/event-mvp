package my.event.practice.web.login;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class TokenManager {

    private static final String MEMBER_ID = "memberId";
    private final long tokenExpiry;
    private final Key secretKey;

    public TokenManager(
            @Value("${jwt.token-expiry}") long tokenExpiry,
            @Value("${jwt.secret-key}") String secretKey
    ) {
        this.tokenExpiry = tokenExpiry;
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String memberId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenExpiry);

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(validity)
                .claim(MEMBER_ID, memberId)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getMemberId(String token) {
        Claims claims = getClaims(token);
        return claims.get(MEMBER_ID, String.class);
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException();
        }
    }
}
