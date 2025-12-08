package com.palja.user_service.presentation.util;

public class AuthResponseExamples {

	public static final String LOGIN_SUCCESS = """
		{
			"success": true,
			"code": "OK",
			"message": "로그인 되었습니다."
		}
		""";

	public static final String REFRESH_SUCCESS = """
		{
			"success": true,
			"code": "OK",
			"message": "토큰이 재발급 되었습니다."
		}
		""";

	public static final String LOGOUT_SUCCESS = """
		{
		    "success": true,
		    "code": "OK",
		    "message": "로그아웃 되었습니다."
		}
		""";

	public static final String INVALID_BODY = """
		{
			"success": false,
			"code": "BAD_REQUEST",
			"message": "잘못된 입력값입니다 (형식을 확인해주세요.)"
		}
		""";

	public static final String EMPTY_LOGIN_ID = """
		{
		    "success": false,
		    "code": "BAD_REQUEST",
		    "message": "잘못된 입력값입니다 ({loginId=아이디를 입력해주세요.})"
		}
		""";

	public static final String EMPTY_PASSWORD = """
		{
		    "success": false,
		    "code": "BAD_REQUEST",
		    "message": "잘못된 입력값입니다 ({loginId=비밀번호를 입력해주세요.})"
		}
		""";

	public static final String MISMATCH_INFO = """
		{
		    "success": false,
		    "code": "UNAUTHORIZED",
		    "message": "로그인 정보가 잘못되었습니다."
		}
		""";

	public static final String STATUS_PENDING = """
		{
		    "success": false,
		    "code": "UNAUTHORIZED",
		    "message": "가입 승인 대기 중인 계정입니다."
		}
		""";

	public static final String FORBIDDEN = """
		{
		    "success": false,
		    "code": "FORBIDDEN",
		    "message": "권한이 없습니다"
		}
		""";

	public static final String INVALID_REFRESH_TOKEN = """
		{
		    "success": false,
		    "code": "UNAUTHORIZED",
		    "message": "다시 로그인해주세요."
		}
		""";

	public static final String INVALID_LOGIN_USER = """
		{
		    "success": false,
		    "code": "UNAUTHORIZED",
		    "message": "다시 로그인해주세요."
		}
		""";

}
