package com.daelim.guildbackend.controller;

import com.daelim.guildbackend.dto.request.LoginRequest;
import com.daelim.guildbackend.dto.request.UserRequest;
import com.daelim.guildbackend.dto.response.Response;
import com.daelim.guildbackend.dto.response.UserInfoResponse;
import com.daelim.guildbackend.entity.User;
import com.daelim.guildbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Tag(name = "User", description = "유저 API")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;

    @Operation(summary = "유저 정보")
    @GetMapping("/{userId}")
    public Response<UserInfoResponse> getUserInfo(@PathVariable String userId) {
        return userService.getUserInfo(userId);
    }

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public Response<UserInfoResponse> signUp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = UserRequest.class)
                    )
            )
            @RequestBody Map<String, Object> userObj, HttpSession session) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return userService.signUp(userObj, session);
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public Response<String> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class)
                    )
            )
            @RequestBody Map<String, Object> userObj, HttpSession session) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return userService.login(userObj, session);
    }

    @Operation(summary = "로그아웃")
    @GetMapping("/logout")
    public Response<Object> logout(HttpSession session) {
        Response<Object> res = new Response<>();
        session.removeAttribute("userId");
        return res;
    }

    @Operation(summary = "로그인 여부 확인")
    @ApiResponse(description = "로그인 상태: 해당 아이디 세션<br>로그아웃 상태: null")
    @GetMapping("/isLogin")
    public Response<Object> isLogin(HttpSession session) {
        Response<Object> res = new Response<>();
        res.setData(session.getAttribute("userId"));
        return res;
    }

    @Operation(summary = "유저 정보 수정")
    @PutMapping("/modify")
    public Response<UserInfoResponse> updateUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = UserRequest.class)
                    )
            )
            @RequestBody Map<String, Object> userObj, HttpSession session) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return userService.updateUser(userObj, session);
    }

    @Operation(summary = "회원 탈퇴")
    @ApiResponse(description = "탈퇴 성공: 2<br>탈퇴실패-로그인돼있는 id와 다름: 0<br>탈퇴실패-id는 맞지만 pw틀림: 1")
    @PostMapping("/delete")
    public Response<Object> deleteUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class)
                    )
            )
            @RequestBody Map<String, Object> userObj, HttpSession session) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return userService.deleteUser(userObj, session);
    }

    @Operation(summary = "아이디 중복 체크")
    @GetMapping("/isIdDup/{userId}")
    public Response<Boolean> isIdDup(@PathVariable String userId) {
        return userService.isIdDup(userId);
    }
}
