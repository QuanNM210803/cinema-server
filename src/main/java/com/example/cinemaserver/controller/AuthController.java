package com.example.cinemaserver.controller;

import com.example.cinemaserver.exception.UserAlreadyExistsException;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.request.LoginRequest;
import com.example.cinemaserver.request.OTPVerificationRequest;
import com.example.cinemaserver.request.RegisterUserRequest;
import com.example.cinemaserver.response.JwtResponse;
import com.example.cinemaserver.response.OTPVerificationResponse;
import com.example.cinemaserver.response.UserResponse;
import com.example.cinemaserver.security.jwt.JwtUtils;
import com.example.cinemaserver.security.user.CinemaUserDetails;
import com.example.cinemaserver.service.ForgotPasswordService;
import com.example.cinemaserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.module.FindException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final ForgotPasswordService forgotPasswordService;
    @PostMapping("/registerUser")
    public ResponseEntity<?> addNewUser(@ModelAttribute RegisterUserRequest userRequest){
        try{
            User user=userService.registerUser(userRequest);
            UserResponse userResponse=userService.getUserResponse(user);
            return ResponseEntity.ok(userResponse);
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (SQLException | IOException | FindException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        //xac thuc
        try{
            Authentication authentication= authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));

            //cho phép Spring Security biết được người dùng hiện tại đã xác thực là ai và có quyền truy cập gì trong suốt quá trình xử lý yêu cầu
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt= jwtUtils.generateJwtTokenForUser(authentication);
            CinemaUserDetails userDetails= (CinemaUserDetails) authentication.getPrincipal();
            List<String> roles=userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).toList();
            return ResponseEntity.ok(new JwtResponse(
                    userDetails.getId(),
                    userDetails.getEmail(),
                    jwt,
                    roles
            ));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Invalid email or password");
        }
    }

    @PutMapping("/sendOtp/{email}")
    public ResponseEntity<?> sendOTP(@PathVariable("email") String email){
        try {
            OTPVerificationResponse otpVerification=forgotPasswordService.sendOtpByEmail(email);
            return ResponseEntity.ok(otpVerification);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/otpVerification")
    public ResponseEntity<?> otpVerification(@ModelAttribute OTPVerificationRequest otpVerificationRequest){
        Boolean otpVerification=ForgotPasswordService.otpVerification(otpVerificationRequest);
        return ResponseEntity.ok(otpVerification);
    }

    @PutMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody LoginRequest loginRequest) {
        try{
            User user=forgotPasswordService.resetPassword(loginRequest.getEmail(),loginRequest.getPassword());
            UserResponse userResponse=userService.getUserResponse(user);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
