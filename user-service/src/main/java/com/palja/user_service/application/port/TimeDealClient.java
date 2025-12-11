package com.palja.user_service.application.port;

import java.util.UUID;

public interface TimeDealClient {

	void deleteAllTimeDeals(UUID companyUserId);

}
