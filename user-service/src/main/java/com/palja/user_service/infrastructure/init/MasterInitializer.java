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
			User master = User.create(
				"master", passwordEncoder.encode("master"),
				"master", "master@master.com", "서울시 강남구 테헤란로 123", UserRole.MASTER
			);

			userRepository.save(master);
		}
	}

}
