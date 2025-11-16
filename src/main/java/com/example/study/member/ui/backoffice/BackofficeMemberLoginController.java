package com.example.study.member.ui.backoffice;

import com.example.study.common.authentication.backoffice.BackofficeTokenManager;
import com.example.study.member.command.application.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;

import static com.example.study.common.authentication.backoffice.BackoffIceAuthenticationConstant.BACKOFFICE_AUTHENTICATION;

@Slf4j
@Tag(name = "Backoffice Login API", description = "백오피스 로그인 및 인증 관련 기능 제공")
@Controller
@RequestMapping("/backoffice")
@RequiredArgsConstructor
public class BackofficeMemberLoginController {

    private final BackofficeMemberLoginService backofficeMemberLoginService;
    private final BackofficeTokenManager backofficeTokenManager;

    @GetMapping("/login")
    @Operation(summary = "로그인 화면 호출", description = "로그인 메인 화면을 호출한다")
    public String loginView(
            Model model,
            @ModelAttribute("loginError") String error
    ){
        if(!model.containsAttribute("login")){
            model.addAttribute("login", new BackofficeMemberLoginSaveForm());
        }
        return "backoffice/login";
    }

    @PostMapping("/login")
    @Operation(summary = "백 오피스 사용자 로그인", description = "백 오피스 사용자를 로그인 한다.")
    public String login(
            @Validated @ModelAttribute("login") BackofficeMemberLoginSaveForm loginSaveForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpServletResponse response
    ) {
        // View validation
        if(bindingResult.hasErrors()){
            log.debug("erros={}", bindingResult);
            return "backoffice/login";
        }

        // Main process
        try{
            BackofficeMemberLoginDto loginDto = new BackofficeMemberLoginDto(
                    loginSaveForm.getLoginId()
                    , loginSaveForm.getPassword());
            Long loginedMemberId = backofficeMemberLoginService.login(loginDto);

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
            redirectAttributes.addFlashAttribute("login", loginSaveForm);
            redirectAttributes.addFlashAttribute("loginError", e.getMessage());
            return "redirect:/backoffice/login";
        }
    }

    @GetMapping("/home")
    @Operation(summary = "메인 화면 호출", description = "로그인 성공시 백 오피스 메인 화면으로 호출한다.")
    public String home() {
        return "backoffice/home";
    }
}
