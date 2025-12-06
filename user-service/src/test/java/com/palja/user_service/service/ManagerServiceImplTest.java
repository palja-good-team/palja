package com.palja.user_service.service;

import static org.assertj.core.api.Assertions.*;
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
import com.palja.user_service.application.command.CreateManagerCommand;
import com.palja.user_service.application.command.UpdateManagerCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadManagerDetailRes;
import com.palja.user_service.application.dto.response.ReadManagerSummaryRes;
import com.palja.user_service.application.dto.response.UpdateManagerDetailRes;
import com.palja.user_service.application.service.impl.ManagerServiceImpl;
import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ManagerServiceImplTest {

	@InjectMocks private ManagerServiceImpl managerService;

	@Mock private UserRepository userRepository;
	@Mock private PasswordEncoder passwordEncoder;

	private User manager1;
	private User manager2;
	private String currentUserLoginId;

	@BeforeEach
	void setUp() {
		manager1 = User.builder()
			.loginId("loginId1")
			.password("password1")
			.name("name1")
			.email("email1@test.com")
			.address("address1")
			.role(UserRole.MANAGER)
			.build();

		manager2 = User.builder()
			.loginId("loginId2")
			.password("password2")
			.name("name2")
			.email("email2@test.com")
			.address("address2")
			.role(UserRole.MANAGER)
			.build();

		currentUserLoginId = "loginId";
	}

	@Nested
	@DisplayName("관리자 생성")
	class CreateManagerTest {

		CreateManagerCommand command = CreateManagerCommand.builder()
			.loginId("loginId")
			.password("password")
			.name("name")
			.email("email@test.com")
			.address("address")
			.build();

		@Test
		@DisplayName("성공")
		void createManager_success() {
			// given
			given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);
			given(userRepository.existsByEmailAndDeletedAtIsNull(anyString())).willReturn(false);

			// when
			CreateUserRes responseDto = managerService.createManager(command);

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
			void createManager_duplicateLoginId_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);

				// when & then
				assertThatThrownBy(() -> managerService.createManager(command))
					.isInstanceOf(BusinessException.class).hasMessage("이미 존재하는 로그인 아이디입니다.");
			}

			@Test
			@DisplayName("중복된 이메일")
			void createManager_duplicateEmail_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);
				given(userRepository.existsByEmailAndDeletedAtIsNull(anyString())).willReturn(true);

				// when & then
				assertThatThrownBy(() -> managerService.createManager(command))
					.isInstanceOf(BusinessException.class).hasMessage("이미 존재하는 이메일입니다.");
			}

		}

	}

	@Nested
	@DisplayName("관리자 목록 조회")
	class GetAllManagersTest {

		@Nested
		@DisplayName("성공")
		class Success {

			Pageable pageable = PageRequest.of(0, 20);

			@Test
			@DisplayName("데이터 존재")
			void getAllManagers_all_success() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.searchAllManagers(
					nullable(String.class), nullable(String.class), nullable(String.class), any(Pageable.class))
				).willReturn(new PageImpl<>(List.of(manager1, manager2), pageable, 2));

				// when
				PageResponse<ReadManagerSummaryRes> pagedResponseDto = managerService.getAllManagers(
					currentUserLoginId, null, null, null, pageable
				);

				// then
				assertThat(pagedResponseDto.getPageSize()).isEqualTo(20);
				assertThat(pagedResponseDto.getTotalElements()).isEqualTo(2);
				assertThat(pagedResponseDto.getContent().size()).isEqualTo(2);
				assertThat(pagedResponseDto.getContent().get(0).getLoginId()).isEqualTo("loginId1");
				assertThat(pagedResponseDto.getContent().get(1).getLoginId()).isEqualTo("loginId2");
				then(userRepository).should(times(1)).existsByLoginIdAndDeletedAtIsNull(anyString());
				then(userRepository).should(times(1)).searchAllManagers(
					nullable(String.class), nullable(String.class), nullable(String.class), any(Pageable.class)
				);
			}

			@Test
			@DisplayName("데이터 미존재")
			void getAllManagers_empty_success() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.searchAllManagers(
					nullable(String.class), nullable(String.class), nullable(String.class), any(Pageable.class))
				).willReturn(new PageImpl<>(List.of(), pageable, 0));

				// when
				PageResponse<ReadManagerSummaryRes> pagedResponseDto = managerService.getAllManagers(
					currentUserLoginId, null, null, null, pageable
				);

				// then
				assertThat(pagedResponseDto.getPageSize()).isEqualTo(20);
				assertThat(pagedResponseDto.getTotalElements()).isEqualTo(0);
				assertThat(pagedResponseDto.getContent().size()).isEqualTo(0);
				then(userRepository).should(times(1)).existsByLoginIdAndDeletedAtIsNull(anyString());
				then(userRepository).should(times(1)).searchAllManagers(
					nullable(String.class), nullable(String.class), nullable(String.class), any(Pageable.class)
				);
			}

		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void getAllManagers_notFoundLoginUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> managerService.getAllManagers(currentUserLoginId, null, null, null, null))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("관리자 조회 - loginId")
	class GetManagerByLoginIdTest {

		@Test
		@DisplayName("성공")
		void getManagerByLoginId_success() {
			// given
			given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
			given(userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class)))
				.willReturn(Optional.of(manager1));

			// when
			ReadManagerDetailRes responseDto = managerService.getManagerByLoginId(currentUserLoginId, manager1.getLoginId());

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
			void getManagerByLoginId_notFoundLoginUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> managerService.getManagerByLoginId(currentUserLoginId, manager1.getLoginId()))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void getManagerByLoginId_notFoundUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class)))
					.willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> managerService.getManagerByLoginId(currentUserLoginId, manager1.getLoginId()))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("관리자 조회 - userId")
	class GetManagerByUserIdTest {

		Long userId = 1L;

		@Test
		@DisplayName("성공")
		void getManagerByUserId_success() {
			// given
			given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
			given(userRepository.findByIdAndRoleAndDeletedAtIsNull(anyLong(), any(UserRole.class))).willReturn(Optional.of(manager1));

			// when
			ReadManagerDetailRes responseDto = managerService.getManagerByUserId(currentUserLoginId, userId);

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
			void getManagerByUserId_notFoundLoginUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(false);

				// when & then
				assertThatThrownBy(() -> managerService.getManagerByUserId(currentUserLoginId, userId))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

			@Test
			@DisplayName("존재하지 않는 회원")
			void getManagerByUserId_notFoundUser_failure() {
				// given
				given(userRepository.existsByLoginIdAndDeletedAtIsNull(anyString())).willReturn(true);
				given(userRepository.findByIdAndRoleAndDeletedAtIsNull(anyLong(), any(UserRole.class))).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> managerService.getManagerByUserId(currentUserLoginId, userId))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("관리자 조회 - me")
	class GetMeTest {

		@Test
		@DisplayName("성공")
		void getMe_success() {
			// given
			given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(manager1));

			// when
			ReadManagerDetailRes responseDto = managerService.getMe(currentUserLoginId);

			// then
			assertThat(responseDto.getLoginId()).isEqualTo("loginId1");
			then(userRepository).should(times(1)).findByLoginIdAndDeletedAtIsNull(anyString());
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void getMe_notFoundLoginUser_failure() {
				// given
				given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> managerService.getMe(currentUserLoginId))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("관리자 수정 - loginId")
	class UpdateManagerByLoginIdTest {

		UpdateManagerCommand command = UpdateManagerCommand.builder()
			.address("address")
			.build();

		@Test
		@DisplayName("성공")
		void updateManagerByLoginId_success() {
			// given
			given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(manager1));

			// when
			UpdateManagerDetailRes responseDto = managerService.updateManagerByLoginId(manager1.getLoginId(), command);

			// then
			assertThat(responseDto.getLoginId()).isEqualTo("loginId1");
			assertThat(responseDto.getAddress()).isEqualTo("address");
			then(userRepository).should(times(1)).findByLoginIdAndDeletedAtIsNull(anyString());
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 회원")
			void updateManagerByLoginId_notFoundUser_failure() {
				// given
				given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> managerService.updateManagerByLoginId(manager1.getLoginId(), command))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("관리자 수정 - me")
	class UpdateMeTest {

		UpdateManagerCommand command = UpdateManagerCommand.builder()
			.address("address")
			.build();

		@Test
		@DisplayName("성공")
		void updateMe_success() {
			// given
			given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.of(manager1));

			// when
			UpdateManagerDetailRes responseDto = managerService.updateMe(currentUserLoginId, command);

			// then
			assertThat(responseDto.getLoginId()).isEqualTo("loginId1");
			assertThat(responseDto.getAddress()).isEqualTo("address");
			then(userRepository).should(times(1)).findByLoginIdAndDeletedAtIsNull(anyString());
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 로그인 회원")
			void updateMe_notFoundLoginUser_failure() {
				// given
				given(userRepository.findByLoginIdAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> managerService.updateMe(currentUserLoginId, command))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

	@Nested
	@DisplayName("관리자 삭제 - loginId")
	class DeleteManagerByLoginIdTest {

		@Test
		@DisplayName("성공")
		void deleteManagerByLoginId_success() {
			// given
			given(userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class))).willReturn(Optional.of(manager1));
			AuditorContext.set(manager1.getLoginId(), UserRole.MANAGER);

			// when
			managerService.deleteManagerByLoginId(manager1.getLoginId());

			// then
			assertThat(manager1.isDeleted()).isTrue();
			then(userRepository).should(times(1)).findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class));
		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("존재하지 않는 회원")
			void deleteManagerByLoginId_notFoundUser_failure() {
				// given
				given(userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(anyString(), any(UserRole.class))).willReturn(Optional.empty());

				// when & then
				assertThatThrownBy(() -> managerService.deleteManagerByLoginId(manager1.getLoginId()))
					.isInstanceOf(BusinessException.class).hasMessage("회원을 찾을 수 없습니다.");
			}

		}

	}

}
