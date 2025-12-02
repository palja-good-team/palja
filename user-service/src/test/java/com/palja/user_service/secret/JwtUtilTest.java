package com.palja.user_service.secret;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.vo.UserRole;
import com.palja.user_service.infrastructure.security.util.JwtUtil;

import io.jsonwebtoken.Claims;

@SpringBootTest(properties = {
	"jwt.access.secret=tx2AlgHI1ib1JNQt5CI6rg8kQ/XlGa+IIRoS/EgcrMCtzsiu8Z/0BGHmRabW3ymYnRMtiWE7NOhlAAvmLhwNSA==",
	"jwt.access.expiration=1h",
	"jwt.refresh.secret=n0HQjV2XQV4JFWi+OqqpavImEKboXQh5eeysTP/lQ9vo/EFiB0iMvFzpMSNy6tGlj+bEI9jw/1HCqFsMxCJZ7Q==",
	"jwt.refresh.expiration=7d"
})
@DisplayName("JwtUtil 테스트")
public class JwtUtilTest {

	@Autowired
	private JwtUtil jwtUtil;

	private User manager;

	private final String BEARER_PREFIX = "Bearer ";

	@BeforeEach
	public void setUp() {
		manager = User.builder()
			.loginId("manager")
			.password("manager")
			.role(UserRole.MANAGER)
			.build();

		ReflectionTestUtils.setField(manager, "id", 1L);
	}

	@Nested
	@DisplayName("액세스 토큰")
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class AccessTokenTest {

		private static String accessToken;

		@Test
		@DisplayName("생성")
		@Order(1)
		void generate() {
			String token = jwtUtil.generateAccessToken(manager.getId(), manager.getRole().name());
			assertThat(token).startsWith(BEARER_PREFIX);
			accessToken = token;
		}

		@Test
		@DisplayName("추출")
		@Order(2)
		void substring() {
			String token = jwtUtil.substringToken(accessToken);
			assertThat(accessToken.substring(7)).isEqualTo(token);
			accessToken = token;
		}

		@Test
		@DisplayName("검증")
		@Order(3)
		void validate() {
			boolean validate = jwtUtil.validateAccessToken(accessToken);
			assertThat(validate).isTrue();
		}

		@Test
		@DisplayName("파싱")
		@Order(4)
		void parse() {
			Claims claims = jwtUtil.parseAccessToken(accessToken);
			assertThat(claims.getSubject()).isEqualTo(manager.getId().toString());
			assertThat(claims.get("role", String.class)).isEqualTo(manager.getRole().name());
		}

	}

	@Nested
	@DisplayName("리프레시 토큰")
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class RefreshTokenTest {

		private static String refreshToken;

		@Test
		@DisplayName("생성")
		@Order(1)
		void generate() {
			String token = jwtUtil.generateRefreshToken(manager.getId(), manager.getRole().name());
			assertThat(token).startsWith(BEARER_PREFIX);
			refreshToken = token;
		}

		@Test
		@DisplayName("추출")
		@Order(2)
		void substring() {
			String token = jwtUtil.substringToken(refreshToken);
			assertThat(refreshToken.substring(7)).isEqualTo(token);
			refreshToken = token;
		}

		@Test
		@DisplayName("검증")
		@Order(3)
		void validate() {
			boolean validate = jwtUtil.validateRefreshToken(refreshToken);
			assertThat(validate).isTrue();
		}

		@Test
		@DisplayName("파싱")
		@Order(4)
		void parse() {
			Claims claims = jwtUtil.parseRefreshToken(refreshToken);
			assertThat(claims.getSubject()).isEqualTo(manager.getId().toString());
			assertThat(claims.get("role", String.class)).isEqualTo(manager.getRole().name());
		}

	}

}
