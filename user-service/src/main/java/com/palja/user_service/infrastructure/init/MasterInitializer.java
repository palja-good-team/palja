package com.palja.user_service.infrastructure.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.palja.common.auditor.AuditorContext;
import com.palja.common.vo.UserRole;
import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MasterInitializer implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {
		AuditorContext.set("master", UserRole.MASTER);

		if (!userRepository.existsByLoginIdAndDeletedAtIsNull("master")) {
			User master = User.builder()
				.loginId("master")
				.password(passwordEncoder.encode("master"))
				.name("master")
				.email("master@master.com")
				.address("서울시 강남구 테헤란로 123")
				.role(UserRole.MASTER)
				.build();

			userRepository.save(master);
		}
	}

}
