package com.mcphub.domain.mcp.error;

import com.mcphub.global.common.exception.code.BaseCodeDto;
import com.mcphub.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum McpErrorStatus implements BaseCodeInterface {
	//MCP 관련 에러
	_FORBIDDEN(HttpStatus.BAD_REQUEST, "MCP404", "해당 MCP에 대한 권한이 없습니다"),
	_NOT_PUBLISH(HttpStatus.BAD_REQUEST, "MCP406", "배포되지 않은 MCP 입니다"),
	_NOT_FOUND(HttpStatus.NOT_FOUND, "MCP407", "해당 MCP가 존재하지 않습니다"),
	_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "MCP407", "유저에 대한 정보를 못찾았습니다(grpc 혹은 redis문제)"),
	_NOT_FOUND_INFO(HttpStatus.NOT_FOUND, "MCP408", "해당 MCP 정보가 없습니다"),
	_ALREADY_SAVED_MCP(HttpStatus.CONFLICT, "MCP409", "이미 저장된 MCP입니다."),
	_ALREADY_DELETED_MCP(HttpStatus.CONFLICT, "MCP405", "이미 삭제된 MCP입니다."),
	_NAME_PLEASE(HttpStatus.BAD_REQUEST, "MCP410", "저정 시 MCP 이름 필수"),
	_CATEGORY_PLEASE(HttpStatus.BAD_REQUEST, "MCP411", "저정 시 카테고리 필수"),
	_LICENCE_PLEASE(HttpStatus.BAD_REQUEST, "MCP412", "저정 시 라이선스 필수"),
	;

	private final HttpStatus httpStatus;
	private final boolean isSuccess = false;
	private final String code;
	private final String message;

	@Override
	public BaseCodeDto getCode() {
		return BaseCodeDto.builder()
		                  .httpStatus(httpStatus)
		                  .isSuccess(isSuccess)
		                  .code(code)
		                  .message(message)
		                  .build();
	}
}
