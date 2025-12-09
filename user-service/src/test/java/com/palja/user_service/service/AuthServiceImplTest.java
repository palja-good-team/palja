package com.palja.user_service.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.palja.common.exception.BusinessException;
import com.palja.common.vo.UserRole;
import com.palja.user_service.application.command.LoginUserCommand;
import com.palja.user_service.application.dto.response.TokenRes;
import com.palja.user_service.application.service.impl.AuthServiceImpl;
import com.palja.user_service.application.util.JwtUtil;
import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.TokenRepository;
import com.palja.user_service.domain.repository.UserRepository;

import io.jsonwebtoken.Claims;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

	@InjectMocks private AuthServiceImpl authService;

	@Mock private UserRepository userRepository;
	@Mock private TokenRepository tokenRepository;
	@Mock private PasswordEncoder passwordEncoder;
	@Mock private JwtUtil jwtUtil;

	private User customer;
	private User companyUser;

	@BeforeEach
	void setUp() {
		customer = User.create(
			"loginId", "password", "customer",
			"customer@gmail.com", "서울시 강남구 테헤란로 123", UserRole.CUSTOMER
		);

		companyUser = User.create(
			"loginId", "password", "company",
			"company@gmail.com", "서울시 강남구 테헤란로 123", UserRole.COMPANY_USER
		);
	}

	@Nested
	@DisplayName("로그인")
	class LoginTest {

		LoginUserCommand command = LoginUserCommand.builder()
			.loginId("loginId")
			.password("password")
			.build();

		@Test
		@DisplayName("성공")
		void login_success() {
			// given
			given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(customer));
			given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
			given(jwtUtil.generateAccessToken(anyString(), anyString())).willReturn("accessToken");
			given(jwtUtil.generateRefreshToken(anyString())).willReturn("refreshToken");
			given(jwtUtil.substringToken(anyString())).willReturn("substringAccessToken");
			given(jwtUtil.getRefreshKeyExpirationTime()).willReturn(3600L);

			// when
			TokenRes tokens = authService.login(command);

			// then
			assertThat(tokens.getAccessToken()).isEqualTo("accessToken");
			assertThat(tokens.getRefreshToken()).isEqualTo("refreshToken");
			assertThat(tokens.getRefreshKeyExpirationTime()).isEqualTo(3600L);
			then(userRepository).should(times(1)).findByLoginIdAndDeletedAtIsNull(anyString());
			then(tokenRepository).should(times(1)).save(anyString(), anyString(), anyLong());
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 회원")
			void login_notFoundUser_failure() {
				// given
				given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> authService.login(command))
					.isInstanceOf(BusinessException.class).hasMessage("로그인 정보가 잘못되었습니다.");
			}

			@Test
			@DisplayName("잘못된 비밀번호")
			void login_invalidPassword_failure() {
				// given
				given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(customer));
				given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> authService.login(command))
					.isInstanceOf(BusinessException.class).hasMessage("로그인 정보가 잘못되었습니다.");
			}

			@Test
			@DisplayName("가입 대기 상태")
			void login_statusIsPending_failure() {
				// given
				given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(companyUser));
				given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

				// when & then
				assertThatThrownBy(() -> authService.login(command))
					.isInstanceOf(BusinessException.class).hasMessage("가입 승인 대기 중인 계정입니다.");
			}

		}

	}

	@Nested
	@DisplayName("토큰 재발급")
	class RefreshAccessTokenTest {

		String accessToken = "accessToken";
		String refreshToken = "refreshToken";

		Claims claims = mock(Claims.class);

		@Nested
		@DisplayName("성공")
		class Success {

			@Test
			@DisplayName("액세스 토큰 존재")
			void refreshAccessToken_accessTokenIsNull_success() {
				// given
				given(jwtUtil.substringToken(anyString())).willReturn("substringRefreshToken");
				given(jwtUtil.validateRefreshToken(anyString())).willReturn(true);
				given(claims.getSubject()).willReturn("loginId");
				given(jwtUtil.parseRefreshToken(anyString())).willReturn(claims);
				given(tokenRepository.get(anyString())).willReturn("substringRefreshToken");
				given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(customer));
				given(jwtUtil.generateAccessToken(anyString(), anyString())).willReturn("newAccessToken");

				// when
				String token = authService.refreshAccessToken(accessToken, refreshToken);

				// then
				assertThat(token).isEqualTo("newAccessToken");
				then(userRepository).should(times(1)).findByLoginIdAndDeletedAtIsNull(anyString());
				then(tokenRepository).should(times(1)).get(anyString());
				then(tokenRepository).should(times(1)).save(anyString(), anyString(), anyLong());
			}

			@Test
			@DisplayName("액세스 토큰 미존재")
			void refreshAccessToken_accessTokenIsNotNull_success() {
				// given
				given(jwtUtil.substringToken(anyString())).willReturn("substringRefreshToken");
				given(jwtUtil.validateRefreshToken(anyString())).willReturn(true);
				given(claims.getSubject()).willReturn("loginId");
				given(jwtUtil.parseRefreshToken(anyString())).willReturn(claims);
				given(tokenRepository.get(anyString())).willReturn("substringRefreshToken");
				given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(customer));
				given(jwtUtil.generateAccessToken(anyString(), anyString())).willReturn("newAccessToken");

				// when
				String token = authService.refreshAccessToken(null, refreshToken);

				// then
				assertThat(token).isEqualTo("newAccessToken");
				then(userRepository).should(times(1)).findByLoginIdAndDeletedAtIsNull(anyString());
				then(tokenRepository).should(times(1)).get(anyString());
				then(tokenRepository).should(never()).save(anyString(), anyString(), anyLong());
			}

		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("유효하지 않은 리프레시 토큰")
			void refreshAccessToken_invalidRefreshToken_failure() {
				// given
				given(jwtUtil.substringToken(anyString())).willReturn("substringRefreshToken");
				given(jwtUtil.validateRefreshToken(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> authService.refreshAccessToken(accessToken, refreshToken))
					.isInstanceOf(BusinessException.class).hasMessage("다시 로그인해주세요.");
			}

			@Test
			@DisplayName("등록되지 않은 리프레시 토큰")
			void refreshAccessToken_notFoundRefreshTokenInRedis_failure() {
				// given
				given(jwtUtil.substringToken(anyString())).willReturn("substringRefreshToken");
				given(jwtUtil.validateRefreshToken(anyString())).willReturn(true);
				given(claims.getSubject()).willReturn("loginId");
				given(jwtUtil.parseRefreshToken(anyString())).willReturn(claims);
				given(tokenRepository.get(anyString())).willReturn("differentRefreshToken");

				// when & then
				assertThatThrownBy(() -> authService.refreshAccessToken(accessToken, refreshToken))
					.isInstanceOf(BusinessException.class).hasMessage("다시 로그인해주세요.");
			}

			@Test
			@DisplayName("존재하지 않는 회원")
			void refreshAccessToken_notFoundUser_failure() {
				// given
				given(jwtUtil.substringToken(anyString())).willReturn("substringRefreshToken");
				given(jwtUtil.validateRefreshToken(anyString())).willReturn(true);
				given(claims.getSubject()).willReturn("loginId");
				given(jwtUtil.parseRefreshToken(anyString())).willReturn(claims);
				given(tokenRepository.get(anyString())).willReturn("substringRefreshToken");
				given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> authService.refreshAccessToken(accessToken, refreshToken))
					.isInstanceOf(BusinessException.class).hasMessage("로그인 정보가 잘못되었습니다.");
			}

		}

	}

	@Nested
	@DisplayName("로그아웃")
	class LogoutTest {

		String accessToken = "accessToken";
		String refreshToken = "refreshToken";

		@Test
		@DisplayName("성공")
		void logout_success() {
			// given
			given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(customer));
			given(jwtUtil.substringToken(anyString())).willReturn("substringAccessToken");
			given(jwtUtil.hashingTokenToSHA256(anyString())).willReturn("hashKey");

			// when
			authService.logout(accessToken, refreshToken);

			// then
			then(userRepository).should(times(1)).findByLoginIdAndDeletedAtIsNull(anyString());
			then(tokenRepository).should(times(1)).save(anyString(), anyString(), anyLong());
			then(tokenRepository).should(times(1)).remove(anyString());
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void logout_notFoundLoginUser_failure() {
				// given
				given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> authService.logout(accessToken, refreshToken))
					.isInstanceOf(BusinessException.class).hasMessage("로그인 정보가 잘못되었습니다.");
			}

		}

	}

}
