package com.example.study.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * 애플리케이션 전역에서 발생하는 예외를 처리하는 글로벌 예외 처리기 클래스입니다.
 *
 * 이 클래스는 {@code @RestControllerAdvice}로 등록되어,
 * 컨트롤러에서 발생하는 예외들을 포착하여
 * 적절한 HTTP 응답 코드와 함께
 * 표준화된 {@link ApiErrorResponse} 객체를 반환합니다.
 *
 * <p>주요 처리 예외:
 * <ul>
 *   <li>{@link CustomException} - 사용자 정의 예외로, 내부에 HTTP 상태 코드와 메시지를 포함하여 응답</li>
 *   <li>{@link MethodArgumentNotValidException} - @RequestBody 스프링 유효성 검사 실패 시 발생하는 예외를 처리하여 상세한 필드 에러 메시지 반환</li>
 *   <li>{@link HandlerMethodValidationException} - @RequestParam 스프링 유효성 검사 실패 시 발생하는 예외를 처리하여 상세한 필드 에러 메시지 반환</li>
 *   <li>{@link Exception} - 그 외 모든 예외에 대해 500 내부 서버 오류로 응답하며, 내부 로그를 기록</li>
 * </ul>
 *
 * 이 클래스를 통해 API 클라이언트에 일관된 에러 응답 구조를 제공하고,
 * 예외 처리 로직의 중복을 제거하며,
 * 보안상 내부 예외 메시지 노출을 방지합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * CustomException 처리
     * 커스텀 예외는 내부에 HttpStatus와 메시지를 포함하고 있으므로,
     * 이를 이용해 적절한 상태 코드와 메시지를 응답한다.
     */
    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleCustomException(CustomException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                ex.getMessage(),
                ex.getClass().getSimpleName(),
                ex.getHttpStatus().value()
        );
        return new ResponseEntity<>(error, ex.getHttpStatus());
    }

    /**
     * 유효성 검사 실패 처리
     * 필드별 에러 메시지를 모두 합쳐서 반환한다.
     */
    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        String errorMessages = bindingResult.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("잘못된 요청입니다.");

        ApiErrorResponse error = new ApiErrorResponse(
                errorMessages,
                "VALIDATION_ERROR",
                HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * 유효성 검사 실패 처리
     * 필드별 에러 메시지를 모두 합쳐서 반환한다.
     */
    @ExceptionHandler
    public ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException exception) {

        // 각 파라미터별 검증 결과
        String errorMessages = exception.getParameterValidationResults().stream()
                .flatMap(paramResult ->
                        paramResult.getResolvableErrors().stream()
                                .map(resolvable -> {
                                    // 파라미터 이름 + 메시지 조합
                                    String paramName = paramResult.getMethodParameter().getParameterName();
                                    String message = resolvable.getDefaultMessage();
                                    return paramName + ": " + message;
                                })
                )
                .collect(Collectors.joining(", "));

        ApiErrorResponse error = new ApiErrorResponse(
                errorMessages,
                "VALIDATION_ERROR",
                HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * 그 외 모든 예외 처리
     * 내부 에러 메시지는 노출하지 않고, 서버 오류 메시지를 대신 응답한다.
     */
    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {

        // 브라우저 자동 요청 무시
        if(ex instanceof NoResourceFoundException nrf){
            String path = nrf.getResourcePath();
            if (path.startsWith(".well-known") ||
                    path.endsWith(".css.map") ||
                    path.equals("favicon.ico")) {
                return null;
            }
        }

        // 내부 로그 남기기
        log.error("Unexpected error occurred", ex);

        ApiErrorResponse error = new ApiErrorResponse(
                "서버 오류가 발생했습니다.",
                "INTERNAL_SERVER_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}