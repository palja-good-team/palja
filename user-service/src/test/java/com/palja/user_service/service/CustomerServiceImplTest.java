package com.palja.user_service.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.palja.common.auditor.AuditorContext;
import com.palja.common.exception.BusinessException;
import com.palja.common.response.PageResponse;
import com.palja.common.vo.UserRole;
import com.palja.user_service.application.command.CreateCustomerCommand;
import com.palja.user_service.application.command.UpdateCustomerCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadCustomerDetailRes;
import com.palja.user_service.application.dto.response.ReadCustomerSummaryRes;
import com.palja.user_service.application.dto.response.UpdateCustomerDetailRes;
import com.palja.user_service.application.service.ReviewService;
import com.palja.user_service.application.service.impl.CustomerServiceImpl;
import com.palja.user_service.application.util.JwtUtil;
import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.TokenRepository;
import com.palja.user_service.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

	@InjectMocks private CustomerServiceImpl customerService;

	@Mock private UserRepository userRepository;
	@Mock private TokenRepository tokenRepository;
	@Mock private ReviewService reviewService;
	@Mock private PasswordEncoder passwordEncoder;
	@Mock private JwtUtil jwtUtil;

	private User customer1;
	private User customer2;
	private String currentUserLoginId;

	@BeforeEach
	void setUp() {
		customer1 = User.create(
			"loginId1", "password1", "name1",
			"email1@test.com", "address1", UserRole.CUSTOMER
		);

		customer2 = User.create(
			"loginId2", "password2", "name2",
			"email2@test.com", "address2", UserRole.CUSTOMER
		);

		currentUserLoginId = "loginId";
	}

	@Nested
	@DisplayName("일반 사용자 생성")
	class CreateCustomerTest {

		CreateCustomerCommand command = CreateCustomerCommand.builder()
			.loginId("loginId")
			.password("password")
			.name("name")
			.email("email@test.com")
			.address("address")
			.build();

		@Test
		@DisplayName("성공")
		void createCustomer_success() {
			// given
			given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);
			given(userRepository.existsByEmailAndDeletedAtIsNull(anyString())).willReturn(false);

			// when
			CreateUserRes responseDto = customerService.createCustomer(command);

			// then
			assertThat(responseDto.getLoginId()).isEqualTo("loginId");
			assertThat(responseDto.getName()).isEqualTo("name");
			then(userRepository).should(times(1)).existsByLoginIdAndDeletedAtIsNull(anyString());
			then(userRepository).should(times(1)).existsByEmailAndDeletedAtIsNull(anyString());
			then(passwordEncoder).should(times(1)).encode(anyString());
			then(userRepository).should(times(1)).save(any(User.class));
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("중복된 로그인 아이디")
			void createCustomer_duplicateLoginId_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);

				// when & then
				assertThatThrownBy(() -> customerService.createCustomer(command))
					.isInstanceOf(BusinessException.class).hasMessage("이미 존재하는 로그인 아이디입니다.");
			}

			@Test
			@DisplayName("중복된 이메일")
			void createCustomer_duplicateEmail_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);
				given(userRepository.existsByEmailAndDeletedAtIsNull(anyString())).willReturn(true);

				// when & then
				assertThatThrownBy(() -> customerService.createCustomer(command))
					.isInstanceOf(BusinessException.class).hasMessage("이미 존재하는 이메일입니다.");
			}

		}

	}

	@Nested
	@DisplayName("일반 사용자 목록 조회")
	class GetAllCustomersTest {

		@Nested
		@DisplayName("성공")
		class Success {

			Pageable pageable = PageRequest.of(0, 20);

			@Test
			@DisplayName("데이터 존재")
			void getAllCustomers_all_success() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.searchAllCustomers(
					nullable(String.class), nullable(String.class), nullable(String.class), any(Pageable.class))
				).willReturn(new PageImpl<>(List.of(customer1, customer2), pageable, 2));

				// when
				PageResponse<ReadCustomerSummaryRes> pagedResponseDto = customerService.getAllCustomers(
					currentUserLoginId, null, null, null, pageable
				);

				// then
				assertThat(pagedResponseDto.getPageSize()).isEqualTo(20);
				assertThat(pagedResponseDto.getTotalElements()).isEqualTo(2);
				assertThat(pagedResponseDto.getContent().size()).isEqualTo(2);
				assertThat(pagedResponseDto.getContent().get(0).getLoginId()).isEqualTo("loginId1");
				assertThat(pagedResponseDto.getContent().get(1).getLoginId()).isEqualTo("loginId2");
				then(userRepository).should(times(1)).existsByLoginIdAndDeletedAtIsNull(anyString());
				then(userRepository).should(times(1)).searchAllCustomers(
					nullable(String.class), nullable(String.class), nullable(String.class), any(Pageable.class)
				);
			}

			@Test
			@DisplayName("데이터 미존재")
			void getAllCustomers_empty_success() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.searchAllCustomers(
					nullable(String.class), nullable(String.class), nullable(String.class), any(Pageable.class))
				).willReturn(new PageImpl<>(List.of(), pageable, 0));

				// when
				PageResponse<ReadCustomerSummaryRes> pagedResponseDto = customerService.getAllCustomers(
					currentUserLoginId, null, null, null, pageable
				);

				// then
				assertThat(pagedResponseDto.getPageSize()).isEqualTo(20);
				assertThat(pagedResponseDto.getTotalElements()).isEqualTo(0);
				assertThat(pagedResponseDto.getContent().size()).isEqualTo(0);
				then(userRepository).should(times(1)).existsByLoginIdAndDeletedAtIsNull(anyString());
				then(userRepository).should(times(1)).searchAllCustomers(
					nullable(String.class), nullable(String.class), nullable(String.class), any(Pageable.class)
				);
			}

		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void getAllCustomers_notFoundLoginUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> customerService.getAllCustomers(currentUserLoginId, null, null, null, null))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("일반 사용자 조회 - loginId")
	class GetCustomerByLoginIdTest {

		@Test
		@DisplayName("성공")
		void getCustomerByLoginId_success() {
			// given
			given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
			given(userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class)))
				.willReturn(Optional.of(customer1));

			// when
			ReadCustomerDetailRes responseDto = customerService.getCustomerByLoginId(currentUserLoginId, customer1.getLoginId());

			// then
			assertThat(responseDto.getLoginId()).isEqualTo("loginId1");
			then(userRepository).should(times(1)).existsByLoginIdAndDeletedAtIsNull(anyString());
			then(userRepository).should(times(1)).findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class));
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void getCustomerByLoginId_notFoundLoginUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> customerService.getCustomerByLoginId(currentUserLoginId, customer1.getLoginId()))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void getCustomerByLoginId_notFoundUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class)))
					.willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> customerService.getCustomerByLoginId(currentUserLoginId, customer1.getLoginId()))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("일반 사용자 조회 - userId")
	class GetCustomerByUserIdTest {

		Long userId = 1L;

		@Test
		@DisplayName("성공")
		void getCustomerByUserId_success() {
			// given
			given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
			given(userRepository.findByIdAndRoleAndDeletedAtIsNull(anyLong(), any(UserRole.class))).willReturn(Optional.of(customer1));

			// when
			ReadCustomerDetailRes responseDto = customerService.getCustomerByUserId(currentUserLoginId, userId);

			// then
			assertThat(responseDto.getLoginId()).isEqualTo("loginId1");
			then(userRepository).should(times(1)).existsByLoginIdAndDeletedAtIsNull(anyString());
			then(userRepository).should(times(1)).findByIdAndRoleAndDeletedAtIsNull(anyLong(), any(UserRole.class));
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void getCustomerByUserId_notFoundLoginUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> customerService.getCustomerByUserId(currentUserLoginId, userId))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

			@Test
			@DisplayName("존재하지 않는 회원")
			void getCustomerByUserId_notFoundUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.findByIdAndRoleAndDeletedAtIsNull(anyLong(), any(UserRole.class))).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> customerService.getCustomerByUserId(currentUserLoginId, userId))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("일반 사용자 조회 - me")
	class GetMeTest {

		@Test
		@DisplayName("성공")
		void getMe_success() {
			// given
			given(userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class))).willReturn(Optional.of(customer1));

			// when
			ReadCustomerDetailRes responseDto = customerService.getMe(currentUserLoginId);

			// then
			assertThat(responseDto.getLoginId()).isEqualTo("loginId1");
			then(userRepository).should(times(1)).findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class));
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void getMe_notFoundLoginUser_failure() {
				// given
				given(userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class))).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> customerService.getMe(currentUserLoginId))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("일반 사용자 수정 - loginId")
	class UpdateCustomerByLoginIdTest {

		UpdateCustomerCommand command = UpdateCustomerCommand.builder()
			.address("address")
			.build();

		@Test
		@DisplayName("성공")
		void updateCustomerByLoginId_success() {
			// given
			given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
			given(userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class))).willReturn(Optional.of(customer1));

			// when
			UpdateCustomerDetailRes responseDto = customerService.updateCustomerByLoginId(currentUserLoginId, customer1.getLoginId(), command);

			// then
			assertThat(responseDto.getLoginId()).isEqualTo("loginId1");
			assertThat(responseDto.getAddress()).isEqualTo("address");
			then(userRepository).should(times(1)).findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class));
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void updateCustomerByLoginId_notFoundLoginUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> customerService.updateCustomerByLoginId(currentUserLoginId, customer1.getLoginId(), command))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

			@Test
			@DisplayName("존재하지 않는 회원")
			void updateCustomerByLoginId_notFoundUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class))).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> customerService.updateCustomerByLoginId(currentUserLoginId, customer1.getLoginId(), command))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("일반 사용자 수정 - me")
	class UpdateMeTest {

		UpdateCustomerCommand command = UpdateCustomerCommand.builder()
			.address("address")
			.build();

		@Test
		@DisplayName("성공")
		void updateMe_success() {
			// given
			given(userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class))).willReturn(Optional.of(customer1));

			// when
			UpdateCustomerDetailRes responseDto = customerService.updateMe(currentUserLoginId, command);

			// then
			assertThat(responseDto.getLoginId()).isEqualTo("loginId1");
			assertThat(responseDto.getAddress()).isEqualTo("address");
			then(userRepository).should(times(1)).findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class));
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void updateMe_notFoundLoginUser_failure() {
				// given
				given(userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class))).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> customerService.updateMe(currentUserLoginId, command))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("일반 사용자 삭제 - loginId")
	class DeleteCustomerByLoginIdTest {

		@Test
		@DisplayName("성공")
		void deleteCustomerByLoginId_success() {
			// given
			given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
			given(userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class))).willReturn(Optional.of(customer1));
			AuditorContext.set(currentUserLoginId, UserRole.MANAGER);

			// when
			customerService.deleteCustomerByLoginId(currentUserLoginId, customer1.getLoginId());

			// then
			assertThat(customer1.isDeleted()).isTrue();
			then(userRepository).should(times(1)).findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class));
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void deleteCustomerByLoginId_notFoundLoginUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> customerService.deleteCustomerByLoginId(currentUserLoginId, customer1.getLoginId()))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

			@Test
			@DisplayName("존재하지 않는 회원")
			void deleteCustomerByLoginId_notFoundUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class))).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> customerService.deleteCustomerByLoginId(currentUserLoginId, customer1.getLoginId()))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("일반 사용자 삭제 - me")
	class DeleteMeTest {

		String accessToken = "accessToken";

		@Test
		@DisplayName("성공")
		void deleteMe_success() {
			// given
			given(userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class))).willReturn(Optional.of(customer1));
			AuditorContext.set(currentUserLoginId, UserRole.CUSTOMER);
			given(jwtUtil.substringToken(anyString())).willReturn("substringAccessToken");
			given(jwtUtil.hashingTokenToSHA256(anyString())).willReturn("hashKey");

			// when
			customerService.deleteMe(accessToken, currentUserLoginId);

			// then
			assertThat(customer1.isDeleted()).isTrue();
			then(userRepository).should(times(1)).findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class));
			then(tokenRepository).should(times(1)).save(anyString(), anyString(), anyLong());
			then(tokenRepository).should(times(1)).remove(anyString());
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void deleteMe_notFoundLoginUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> customerService.deleteCustomerByLoginId(currentUserLoginId, customer1.getLoginId()))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

}
