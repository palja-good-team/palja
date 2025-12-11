package com.palja.user_service.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import org.springframework.test.util.ReflectionTestUtils;

import com.palja.common.auditor.AuditorContext;
import com.palja.common.exception.BusinessException;
import com.palja.common.response.PageResponse;
import com.palja.common.vo.UserRole;
import com.palja.user_service.application.command.CreateCompanyUserCommand;
import com.palja.user_service.application.command.UpdateCompanyUserCommand;
import com.palja.user_service.application.command.UpdateCompanyUserStatusCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadCompanyUserDetailRes;
import com.palja.user_service.application.dto.response.ReadCompanyUserSummaryRes;
import com.palja.user_service.application.dto.response.UpdateCompanyUserDetailRes;
import com.palja.user_service.application.port.ProductClient;
import com.palja.user_service.application.port.TimeDealClient;
import com.palja.user_service.application.service.impl.CompanyUserServiceImpl;
import com.palja.user_service.application.util.JwtUtil;
import com.palja.user_service.domain.entity.CompanyUser;
import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.CompanyUserRepository;
import com.palja.user_service.domain.repository.TokenRepository;
import com.palja.user_service.domain.repository.UserRepository;
import com.palja.user_service.domain.vo.UserStatus;

@ExtendWith(MockitoExtension.class)
public class CompanyUserServiceImplTest {

	@InjectMocks CompanyUserServiceImpl companyUserService;

	@Mock private CompanyUserRepository companyUserRepository;
	@Mock private UserRepository userRepository;
	@Mock private TokenRepository tokenRepository;
	@Mock private TimeDealClient timeDealClient;
	@Mock private ProductClient productClient;
	@Mock private PasswordEncoder passwordEncoder;
	@Mock private JwtUtil jwtUtil;

	private User user1;
	private User user2;
	private CompanyUser companyUser1;
	private String currentUserLoginId;

	@BeforeEach
	void setUp() {
		user1 = User.create(
			"loginId1", "password1", "name1",
			"email1@test.com", "address1", UserRole.COMPANY_USER
			);

		user2 = User.create(
			"loginId2", "password2", "name2",
			"email2@test.com", "address2", UserRole.COMPANY_USER
		);

		companyUser1 = CompanyUser.create(user1, "companyName1", "companyNumber1");

		currentUserLoginId = "loginId";
	}

	@Nested
	@DisplayName("업체 판매자 생성")
	class CreateCompanyUserTest {

		CreateCompanyUserCommand command = CreateCompanyUserCommand.builder()
			.loginId("loginId")
			.password("password")
			.name("name")
			.companyName("companyName")
			.companyNumber("companyNumber")
			.email("email@test.com")
			.address("address")
			.build();

		@Test
		@DisplayName("성공")
		void createCompanyUser_success() {
			// given
			given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);
			given(userRepository.existsByEmailAndDeletedAtIsNull(anyString())).willReturn(false);

			// when
			CreateUserRes responseDto = companyUserService.createCompanyUser(command);

			// then
			assertThat(responseDto.getLoginId()).isEqualTo("loginId");
			assertThat(responseDto.getName()).isEqualTo("name");
			then(userRepository).should(times(1)).existsByLoginIdAndDeletedAtIsNull(anyString());
			then(userRepository).should(times(1)).existsByEmailAndDeletedAtIsNull(anyString());
			then(passwordEncoder).should(times(1)).encode(anyString());
			then(companyUserRepository).should(times(1)).save(any(CompanyUser.class));
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("중복된 로그인 아이디")
			void createCompanyUser_duplicateLoginId_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);

				// when & then
				assertThatThrownBy(() -> companyUserService.createCompanyUser(command))
					.isInstanceOf(BusinessException.class).hasMessage("이미 존재하는 로그인 아이디입니다.");
			}

			@Test
			@DisplayName("중복된 이메일")
			void createCompanyUser_duplicateEmail_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);
				given(userRepository.existsByEmailAndDeletedAtIsNull(anyString())).willReturn(true);

				// when & then
				assertThatThrownBy(() -> companyUserService.createCompanyUser(command))
					.isInstanceOf(BusinessException.class).hasMessage("이미 존재하는 이메일입니다.");
			}

		}

	}

	@Nested
	@DisplayName("업체 판매자 목록 조회")
	class GetAllCompanyUsersTest {

		@Nested
		@DisplayName("성공")
		class Success {

			Pageable pageable = PageRequest.of(0, 20);

			@Test
			@DisplayName("데이터 존재")
			void getAllCompanyUsers_all_success() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.searchAllCompanyUsers(
					nullable(String.class), nullable(String.class), nullable(String.class), nullable(UserStatus.class), any(Pageable.class))
				).willReturn(new PageImpl<>(List.of(user1, user2), pageable, 2));

				// when
				PageResponse<ReadCompanyUserSummaryRes> pagedResponseDto = companyUserService.getAllCompanyUsers(
					currentUserLoginId, null, null, null, null, pageable
				);

				// then
				assertThat(pagedResponseDto.getPageSize()).isEqualTo(20);
				assertThat(pagedResponseDto.getTotalElements()).isEqualTo(2);
				assertThat(pagedResponseDto.getContent().size()).isEqualTo(2);
				assertThat(pagedResponseDto.getContent().get(0).getLoginId()).isEqualTo("loginId1");
				assertThat(pagedResponseDto.getContent().get(1).getLoginId()).isEqualTo("loginId2");
				then(userRepository).should(times(1)).existsByLoginIdAndDeletedAtIsNull(anyString());
				then(userRepository).should(times(1)).searchAllCompanyUsers(
					nullable(String.class), nullable(String.class), nullable(String.class), nullable(UserStatus.class), any(Pageable.class)
				);
			}

			@Test
			@DisplayName("데이터 미존재")
			void getAllCompanyUsers_empty_success() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.searchAllCompanyUsers(
					nullable(String.class), nullable(String.class), nullable(String.class), nullable(UserStatus.class), any(Pageable.class))
				).willReturn(new PageImpl<>(List.of(), pageable, 0));

				// when
				PageResponse<ReadCompanyUserSummaryRes> pagedResponseDto = companyUserService.getAllCompanyUsers(
					currentUserLoginId, null, null, null, null, pageable
				);

				// then
				assertThat(pagedResponseDto.getPageSize()).isEqualTo(20);
				assertThat(pagedResponseDto.getTotalElements()).isEqualTo(0);
				assertThat(pagedResponseDto.getContent().size()).isEqualTo(0);
				then(userRepository).should(times(1)).existsByLoginIdAndDeletedAtIsNull(anyString());
				then(userRepository).should(times(1)).searchAllCompanyUsers(
					nullable(String.class), nullable(String.class), nullable(String.class), nullable(UserStatus.class),any(Pageable.class)
				);
			}

		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void getAllCompanyUsers_notFoundLoginUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> companyUserService.getAllCompanyUsers(currentUserLoginId, null, null, null, null, null))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("업체 판매자 조회 - loginId")
	class GetCompanyUserByLoginIdTest {

		@Test
		@DisplayName("성공")
		void getCompanyUserByLoginId_success() {
			// given
			given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
			given(companyUserRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(companyUser1));

			// when
			ReadCompanyUserDetailRes responseDto = companyUserService.getCompanyUserByLoginId(currentUserLoginId, companyUser1.getUser().getLoginId());

			// then
			assertThat(responseDto.getLoginId()).isEqualTo("loginId1");
			then(userRepository).should(times(1)).existsByLoginIdAndDeletedAtIsNull(anyString());
			then(companyUserRepository).should(times(1)).findByLoginIdAndDeletedAtIsNull(anyString());
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void getCompanyUserByLoginId_notFoundLoginUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> companyUserService.getCompanyUserByLoginId(currentUserLoginId, companyUser1.getUser().getLoginId()))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

			@Test
			@DisplayName("존재하지 않는 회원")
			void getCompanyUserByLoginId_notFoundUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(companyUserRepository.findByLoginIdAndDeletedAtIsNull(anyString()))
					.willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> companyUserService.getCompanyUserByLoginId(currentUserLoginId, companyUser1.getUser().getLoginId()))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("업체 판매자 조회 - companyUserId")
	class GetCompanyUserByCompanyUserIdTest {

		UUID companyUserId = UUID.randomUUID();

		@Test
		@DisplayName("성공")
		void getCompanyUserByCompanyUserId_success() {
			// given
			given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
			given(companyUserRepository.findByIdAndDeletedAtIsNull(any(UUID.class))).willReturn(Optional.of(companyUser1));

			// when
			ReadCompanyUserDetailRes responseDto = companyUserService.getCompanyUserByCompanyUserId(currentUserLoginId, companyUserId);

			// then
			assertThat(responseDto.getLoginId()).isEqualTo("loginId1");
			then(userRepository).should(times(1)).existsByLoginIdAndDeletedAtIsNull(anyString());
			then(companyUserRepository).should(times(1)).findByIdAndDeletedAtIsNull(any(UUID.class));
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void getCompanyUserByCompanyUserId_notFoundLoginUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> companyUserService.getCompanyUserByCompanyUserId(currentUserLoginId, companyUserId))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

			@Test
			@DisplayName("존재하지 않는 회원")
			void getCompanyUserByCompanyUserId_notFoundUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(companyUserRepository.findByIdAndDeletedAtIsNull(any(UUID.class))).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> companyUserService.getCompanyUserByCompanyUserId(currentUserLoginId, companyUserId))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("업체 판매자 조회 - me")
	class GetMeTest {

		@Test
		@DisplayName("성공")
		void getMe_success() {
			// given
			given(companyUserRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(companyUser1));

			// when
			ReadCompanyUserDetailRes responseDto = companyUserService.getMe(currentUserLoginId);

			// then
			assertThat(responseDto.getLoginId()).isEqualTo("loginId1");
			then(companyUserRepository).should(times(1)).findByLoginIdAndDeletedAtIsNull(anyString());
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void getMe_notFoundLoginUser_failure() {
				// given
				given(companyUserRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> companyUserService.getMe(currentUserLoginId))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("업체 판매자 수정 - loginId")
	class UpdateCompanyUserByLoginIdTest {

		UpdateCompanyUserCommand command = UpdateCompanyUserCommand.builder()
			.address("address")
			.companyName("companyName")
			.build();

		@Test
		@DisplayName("성공")
		void updateCompanyUserByLoginId_success() {
			// given
			given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
			given(companyUserRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(companyUser1));

			// when
			UpdateCompanyUserDetailRes responseDto = companyUserService.updateCompanyUserByLoginId(
				currentUserLoginId, companyUser1.getUser().getLoginId(), command
			);

			// then
			assertThat(responseDto.getLoginId()).isEqualTo("loginId1");
			assertThat(responseDto.getAddress()).isEqualTo("address");
			assertThat(responseDto.getCompanyName()).isEqualTo("companyName");
			then(companyUserRepository).should(times(1)).findByLoginIdAndDeletedAtIsNull(anyString());
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void updateCompanyUserByLoginId_notFoundLoginUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> companyUserService.updateCompanyUserByLoginId(
					currentUserLoginId, companyUser1.getUser().getLoginId(), command)
				).isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

			@Test
			@DisplayName("존재하지 않는 회원")
			void updateCompanyUserByLoginId_notFoundUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(companyUserRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> companyUserService.updateCompanyUserByLoginId(
					currentUserLoginId, companyUser1.getUser().getLoginId(), command)
				).isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("업체 판매자 수정 - me")
	class UpdateMeTest {

		UpdateCompanyUserCommand command = UpdateCompanyUserCommand.builder()
			.address("address")
			.companyName("companyName")
			.build();

		@Test
		@DisplayName("성공")
		void updateMe_success() {
			// given
			given(companyUserRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(companyUser1));

			// when
			UpdateCompanyUserDetailRes responseDto = companyUserService.updateMe(currentUserLoginId, command);

			// then
			assertThat(responseDto.getLoginId()).isEqualTo("loginId1");
			assertThat(responseDto.getAddress()).isEqualTo("address");
			assertThat(responseDto.getCompanyName()).isEqualTo("companyName");
			then(companyUserRepository).should(times(1)).findByLoginIdAndDeletedAtIsNull(anyString());
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void updateMe_notFoundLoginUser_failure() {
				// given
				given(companyUserRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> companyUserService.updateMe(currentUserLoginId, command))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("업체 판매자 상태 수정")
	class UpdateCompanyUserStatusTest {

		UpdateCompanyUserStatusCommand command = UpdateCompanyUserStatusCommand.builder()
			.status("active")
			.build();

		@Test
		@DisplayName("성공")
		void updateCompanyUserStatus_success() {
			// given
			given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
			given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(user1));

			// when
			companyUserService.updateCompanyUserStatus(currentUserLoginId, companyUser1.getUser().getLoginId(), command);

			// then
			assertThat(user1.getStatus()).isEqualTo(UserStatus.ACTIVE);
			then(userRepository).should(times(1)).findByLoginIdAndDeletedAtIsNull(anyString());
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void updateCompanyUserStatus_notFoundLoginUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> companyUserService.updateCompanyUserStatus(
					currentUserLoginId, companyUser1.getUser().getLoginId(), command)
				).isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

			@Test
			@DisplayName("존재하지 않는 회원")
			void updateCompanyUserStatus_notFoundUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> companyUserService.updateCompanyUserStatus(
					currentUserLoginId, companyUser1.getUser().getLoginId(), command)
				).isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

			@Test
			@DisplayName("유효하지 않은 상태 값")
			void updateCompanyUserStatus_invalidStatus_failure() {
				// given
				UpdateCompanyUserStatusCommand command = UpdateCompanyUserStatusCommand.builder()
					.status("status")
					.build();

				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(user1));

				// when & then
				assertThatThrownBy(() -> companyUserService.updateCompanyUserStatus(
					currentUserLoginId, companyUser1.getUser().getLoginId(), command)
				).isInstanceOf(IllegalArgumentException.class).hasMessage("유효하지 않은 상태 값 입니다.");
			}

			@Test
			@DisplayName("대기 상태에서 대기 상태로 변경")
			void updateCompanyUserStatus_pendingToPending_failure() {
				// given
				UpdateCompanyUserStatusCommand errorCommand = UpdateCompanyUserStatusCommand.builder()
					.status("pending")
					.build();

				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(user1));

				// when & then
				assertThatThrownBy(() -> companyUserService.updateCompanyUserStatus(
					currentUserLoginId, companyUser1.getUser().getLoginId(), errorCommand)
				).isInstanceOf(IllegalArgumentException.class).hasMessage("대기 상태에서 대기 상태로 변경할 수 없습니다.");
			}

			@Test
			@DisplayName("활성 상태에서 대기 상태로 변경")
			void updateCompanyUserStatus_activeToPending_failure() {
				// given
				companyUser1.updateStatus("active");
				UpdateCompanyUserStatusCommand errorCommand = UpdateCompanyUserStatusCommand.builder()
					.status("pending")
					.build();

				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(user1));

				// when & then
				assertThatThrownBy(() -> companyUserService.updateCompanyUserStatus(
					currentUserLoginId, companyUser1.getUser().getLoginId(), errorCommand)
				).isInstanceOf(IllegalArgumentException.class).hasMessage("활성 상태에서 대기 상태로 변경할 수 없습니다.");
			}

			@Test
			@DisplayName("활성 상태에서 활성 상태로 변경")
			void updateCompanyUserStatus_activeToActive_failure() {
				// given
				companyUser1.updateStatus("active");
				UpdateCompanyUserStatusCommand errorCommand = UpdateCompanyUserStatusCommand.builder()
					.status("active")
					.build();

				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(user1));

				// when & then
				assertThatThrownBy(() -> companyUserService.updateCompanyUserStatus(
					currentUserLoginId, companyUser1.getUser().getLoginId(), errorCommand)
				).isInstanceOf(IllegalArgumentException.class).hasMessage("활성 상태에서 활성 상태로 변경할 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("업체 판매자 삭제 - loginId")
	class DeleteCompanyUserByLoginIdTest {

		@Test
		@DisplayName("성공")
		void deleteCompanyUserByLoginId_success() {
			// given
			given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
			given(companyUserRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(companyUser1));
			AuditorContext.set(currentUserLoginId, UserRole.MANAGER);
			ReflectionTestUtils.setField(companyUser1, "id", UUID.randomUUID());

			// when
			companyUserService.deleteCompanyUserByLoginId(currentUserLoginId, companyUser1.getUser().getLoginId());

			// then
			assertThat(companyUser1.getUser().isDeleted()).isTrue();
			then(companyUserRepository).should(times(1)).findByLoginIdAndDeletedAtIsNull(anyString());
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void deleteCompanyUserByLoginId_notFoundLoginUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> companyUserService.deleteCompanyUserByLoginId(currentUserLoginId, companyUser1.getUser().getLoginId()))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

			@Test
			@DisplayName("존재하지 않는 회원")
			void deleteCompanyUserByLoginId_notFoundUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(companyUserRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> companyUserService.deleteCompanyUserByLoginId(currentUserLoginId, companyUser1.getUser().getLoginId()))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("업체 판매자 삭제 - me")
	class DeleteMeTest {

		String accessToken = "accessToken";

		@Test
		@DisplayName("성공")
		void deleteMe_success() {
			// given
			given(companyUserRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(companyUser1));
			AuditorContext.set(currentUserLoginId, UserRole.MANAGER);
			given(jwtUtil.substringToken(anyString())).willReturn("substringAccessToken");
			given(jwtUtil.hashingTokenToSHA256(anyString())).willReturn("hashKey");

			// when
			companyUserService.deleteMe(accessToken, currentUserLoginId);

			// then
			assertThat(companyUser1.getUser().isDeleted()).isTrue();
			then(companyUserRepository).should(times(1)).findByLoginIdAndDeletedAtIsNull(anyString());
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
				given(companyUserRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> companyUserService.deleteMe(accessToken, currentUserLoginId))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("업체 판매자 가입 거절")
	class RejectCompanyUserTest {

		@Test
		@DisplayName("성공")
		void rejectCompanyUser_success() {
			// given
			given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
			given(companyUserRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(companyUser1));
			AuditorContext.set(currentUserLoginId, UserRole.MANAGER);

			// when
			companyUserService.rejectCompanyUser(currentUserLoginId, companyUser1.getUser().getLoginId());

			// then
			assertThat(companyUser1.getUser().isDeleted()).isTrue();
			then(companyUserRepository).should(times(1)).findByLoginIdAndDeletedAtIsNull(anyString());
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void rejectCompanyUser_notFoundLoginUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> companyUserService.rejectCompanyUser(currentUserLoginId, companyUser1.getUser().getLoginId()))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

			@Test
			@DisplayName("존재하지 않는 회원")
			void rejectCompanyUser_notFoundUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(companyUserRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> companyUserService.rejectCompanyUser(currentUserLoginId, companyUser1.getUser().getLoginId()))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

			@Test
			@DisplayName("가입이 승인된 회원")
			void rejectCompanyUser_alreadyActive_failure() {
				// given
				companyUser1.updateStatus("active");

				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(companyUserRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(companyUser1));

				// when & then
				assertThatThrownBy(() -> companyUserService.rejectCompanyUser(currentUserLoginId, companyUser1.getUser().getLoginId()))
					.isInstanceOf(BusinessException.class).hasMessage("이미 승인된 업체 판매자입니다.");
			}

		}

	}

}
