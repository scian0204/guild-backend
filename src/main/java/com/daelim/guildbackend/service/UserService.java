package com.daelim.guildbackend.service;

import com.daelim.guildbackend.dto.request.LoginRequest;
import com.daelim.guildbackend.dto.response.Error;
import com.daelim.guildbackend.dto.response.Response;
import com.daelim.guildbackend.dto.response.UserInfoResponse;
import com.daelim.guildbackend.entity.User;
import com.daelim.guildbackend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService{
    ObjectMapper objMpr = new ObjectMapper();
    @Autowired()
    UserRepository userRepository;

    public Response<UserInfoResponse> signUp(Map<String, Object> userObj, HttpSession session) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Response<UserInfoResponse> res = new Response<>();
        User user = objMpr.convertValue(userObj, User.class);

        Optional<User> userS = userRepository.findByUserId(user.getUserId());
        if (userS.isEmpty()) {
            session.setAttribute("userId", user.getUserId());
            user.setPassword(encrypt(user.getPassword()));
            UserInfoResponse uir = new UserInfoResponse(userRepository.save(user));
            res.setData(uir);
        } else {
            Error error = new Error();
            error.setErrorId(0);
            error.setMessage("아이디가 중복됨");
            res.setError(error);
        }

        return res;
    }

    public Response<String> login(Map<String, Object> userObj, HttpSession session) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Response<String> res = new Response<>();
        LoginRequest lr = objMpr.convertValue(userObj, LoginRequest.class);
        String userId = lr.getUserId();
        String password = lr.getPassword();

        Optional<User> byUserId = userRepository.findByUserId(userId);
        if (byUserId.isPresent()) {
            User user = byUserId.get();
            if (user.getPassword().equals(encrypt(password))) {
                session.setAttribute("userId", userId);
                res.setData(userId);
            } else {
                Error error = new Error();
                error.setErrorId(1);
                error.setMessage("id는 존재하지만 패스워드가 틀림");
                res.setError(error);
            }
        } else {
            Error error = new Error();
            error.setErrorId(0);
            error.setMessage("id가 존재하지 않음");
            res.setError(error);
        }

        return res;
    }

    public Response<UserInfoResponse> updateUser(Map<String, Object> userObj, HttpSession session) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Response<UserInfoResponse> res = new Response<>();
        User user = objMpr.convertValue(userObj, User.class);
        if (session.getAttribute("userId") == null) {
            Error error = new Error();
            error.setErrorId(0);
            error.setMessage("로그인상태가 아님");
            res.setError(error);
        } else if (!session.getAttribute("userId").equals(user.getUserId())) {
            Error error = new Error();
            error.setErrorId(1);
            error.setMessage("해당 아이디와 로그인된 아이디가 다름");
            res.setError(error);
        } else {
            Optional<User> optUser = userRepository.findByUserId(user.getUserId());
            User user1 = optUser.get();
            user.setRegDate(user1.getRegDate());
            user.setPassword(encrypt(user.getPassword()));
            res.setData(new UserInfoResponse(userRepository.save(user)));
        }
        
        return res;
    }

    public Response<Object> deleteUser(Map<String, Object> userObj, HttpSession session) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Response<Object> res = new Response<>();
        String userId = (String) userObj.get("userId");
        String password = (String) userObj.get("password");

        Optional<User> byUserId = userRepository.findByUserId(userId);
        if (session.getAttribute("userId").equals(userId)) {
            User user = byUserId.get();
            if (user.getPassword().equals(encrypt(password))) {
                session.removeAttribute("userId");
                userRepository.deleteById(userId);
            } else {
                Error error = new Error();
                error.setErrorId(1);
                error.setMessage("id는 존재하지만 패스워드가 틀림");
                res.setError(error);
            }
        } else {
            Error error = new Error();
            error.setErrorId(1);
            error.setMessage("해당 id가 로그인 돼있는 id와 다름");
            res.setError(error);
        }

        return res;
    }

    public String encrypt(String pw) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(pw.getBytes("utf-8"));
        return bytesToHex(md.digest());
    }
    public String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b: bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    public Response<Boolean> isIdDup(String userId) {
        Response<Boolean> res = new Response<>();
        Boolean result = false;
        Optional<User> userS = userRepository.findByUserId(userId);
        if (userS.isPresent()) {
            result = true;
        }

        res.setData(result);
        return res;
    }

    public Response<UserInfoResponse> getUserInfo(String userId) {
        Response<UserInfoResponse> res = new Response<>();
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isPresent()) {
            res.setData(new UserInfoResponse(user.get()));
        } else {
            Error error = new Error();
            error.setErrorId(0);
            error.setMessage("해당 아이디가 존재하지 않음");
            res.setError(error);
        }

        return res;
    }
}
