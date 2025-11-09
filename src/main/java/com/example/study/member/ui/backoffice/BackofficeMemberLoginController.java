package com.example.study.member.ui.backoffice;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.backoffice.BackofficeTokenManager;
import com.example.study.member.command.application.BackofficeMemberLoginDto;
import com.example.study.member.command.application.BackofficeMemberLoginService;
import com.example.study.member.command.application.InvalidCredentialsException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.Duration;

import static com.example.study.common.authentication.backoffice.BackoffIceAuthenticationConstant.BACKOFFICE_AUTHENTICATION;

@Tag(name = "Backoffice Login API", description = "백오피스 로그인 및 인증 관련 기능 제공")
@Controller
@RequestMapping("/backoffice")
@RequiredArgsConstructor
public class BackofficeMemberLoginController {

    private final BackofficeMemberLoginService backofficeMemberLoginService;
    private final BackofficeTokenManager backofficeTokenManager;

    @GetMapping("/login")
    @Operation(summary = "로그인 화면 호출", description = "로그인 메인 화면을 호출한다")
    public String loginView(Model model){
        model.addAttribute("login", new BackofficeMemberLoginDto("",""));
        return "backoffice/login";
    }

    @PostMapping("/login")
    @Operation(summary = "백 오피스 사용자 로그인", description = "백 오피스 사용자를 로그인 한다.")
    public String login(
            @Valid @ModelAttribute BackofficeMemberLoginDto dto,
            Model model,
            HttpServletResponse response
    ) {
        try{
            Long loginedMemberId = backofficeMemberLoginService.login(dto);

            String token = backofficeTokenManager.generateToken(loginedMemberId);

            ResponseCookie cookie = ResponseCookie.from(BACKOFFICE_AUTHENTICATION, token)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(Duration.ofMinutes(60))
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());

            return "redirect:/backoffice/home";

        } catch (InvalidCredentialsException e) {
            model.addAttribute("login", dto);
            model.addAttribute("loginError", e.getMessage());
            return "backoffice/login";
        }
    }

    @GetMapping("/home")
    @Operation(summary = "메인 화면 호출", description = "로그인 성공시 백 오피스 메인 화면으로 호출한다.")
    public String home() {
        return "backoffice/home";
    }

    @PostMapping("/api/login")
    @ResponseBody
    @Operation(summary = "백 오피스 테스트 용 로그인 api", description = "테스트 용 로그인 api")
    public ApiSuccessResponse<Void> testLogin(
            @Valid @RequestBody BackofficeMemberLoginDto dto,
            HttpServletResponse response
    ) {
        Long loginedMemberId = backofficeMemberLoginService.login(dto);

        String token = backofficeTokenManager.generateToken(loginedMemberId);

        ResponseCookie cookie = ResponseCookie.from(BACKOFFICE_AUTHENTICATION, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(60))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ApiSuccessResponse.empty();
    }
}
