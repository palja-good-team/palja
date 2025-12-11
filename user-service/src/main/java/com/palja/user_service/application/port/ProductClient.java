package com.palja.user_service.application.port;

import java.util.UUID;

public interface ProductClient {

	void deleteAllProducts(UUID companyUserId);

}
