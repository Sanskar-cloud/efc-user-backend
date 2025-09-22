package com.example.efc_user.controllers;

import com.example.efc_user.collections.User;
import com.example.efc_user.exceptions.ApiException;
import com.example.efc_user.payloads.DeliveryBoyDto;
import com.example.efc_user.payloads.LoginRequest;
import com.example.efc_user.payloads.RegisterRequest;
import com.example.efc_user.payloads.UserDto;
import com.example.efc_user.repo.UserRepo;
import com.example.efc_user.response.BaseApiResponse;
import com.example.efc_user.response.LoginDeliveryResponse;
import com.example.efc_user.response.LoginResponse;
import com.example.efc_user.security.CustomUserDetailService;
import com.example.efc_user.security.JwtTokenHelper;
import com.example.efc_user.services.UserService;
//import com.example.efc_user.services.impl.EmailService;
//import com.example.efc_user.services.impl.OtpService;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private UserDetailsService userDetailsService;

//    @Autowired
//    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

//    @Autowired
//    private OtpService otpService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper mapper;

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.redirect-uri}")
    private String redirectUri;
    User savedUser;


    @PostMapping("/login")
    @Operation(
            summary = "Login API",
            description = "This endpoint allows a user to login and receive a JWT token."

    )
    public ResponseEntity<BaseApiResponse<LoginResponse>> createToken(@RequestBody LoginRequest request) throws Exception {
        authenticate(request.getUsername(), request.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        log.info(userDetails+"cvf fvg dcfv dcfv cdfv 1234556789");
        String token = jwtTokenHelper.generateToken(userDetails);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(mapper.map(userDetails, UserDto.class));

        return ResponseEntity.ok(
                BaseApiResponse.<LoginResponse>builder()
                        .success(true)
                        .message("Successful Login")
                        .data(response)
                        .build()
        );
    }

    private void authenticate(String username, String password) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new ApiException("Invalid username or password");
        }
    }
    @PostMapping("/login-delivery")
    @Operation(
            summary = "Login API",
            description = "This endpoint allows a user to login and receive a JWT token."

    )
    public ResponseEntity<BaseApiResponse<LoginDeliveryResponse>> createTokenDelivery(@RequestBody LoginRequest request) throws Exception {
        authenticate2(request.getUsername(), request.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        log.info(userDetails+"cvf fvg dcfv dcfv cdfv 1234556789");
        String token = jwtTokenHelper.generateToken(userDetails);

        LoginDeliveryResponse response = new LoginDeliveryResponse();
        response.setToken(token);
        response.setUser(mapper.map(userDetails, DeliveryBoyDto.class));

        return ResponseEntity.ok(
                BaseApiResponse.<LoginDeliveryResponse>builder()
                        .success(true)
                        .message("Successful Login")
                        .data(response)
                        .build()
        );
    }

    private void authenticate2(String username, String password) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new ApiException("Invalid username or password");
        }
    }

    @PostMapping(value = "/register", produces = "application/json")
    @Operation(
            summary = "Register API",
            description = "This endpoint allows a new user to register."

    )
    public ResponseEntity<BaseApiResponse<UserDto>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        UserDto registeredUser = this.userService.registerNewUser(registerRequest);

        return ResponseEntity.ok(
                BaseApiResponse.<UserDto>builder()
                        .success(true)
                        .message("User successfully registered")
                        .data(registeredUser)
                        .build()
        );
    }
    @PostMapping(value = "/register-delivery", produces = "application/json")
    @Operation(
            summary = "Register API",
            description = "This endpoint allows a new delivery boy to register."

    )
    public ResponseEntity<BaseApiResponse<DeliveryBoyDto>> registerDelivery(@Valid @RequestBody RegisterRequest registerRequest) {
      DeliveryBoyDto registeredUser = this.userService.registerNewDeliveryBoy(registerRequest);

        return ResponseEntity.ok(
                BaseApiResponse.<DeliveryBoyDto>builder()
                        .success(true)
                        .message("User successfully registered")
                        .data(registeredUser)
                        .build()
        );
    }

//    @PostMapping("/sendOtp")
//    @Operation(
//            summary = "Send OTP API",
//            description = "This endpoint allows sending an OTP to a given email address."
//
//    )
//    public ResponseEntity<BaseApiResponse<String>> sendOtp(@RequestParam String email) {
//        String email2 = email.trim();
//        String otp = otpService.generateOtp(email2);
//        emailService.sendSimpleMessage(email2, "Your OTP", "OTP is " + otp);
//
//        return ResponseEntity.ok(
//                BaseApiResponse.<String>builder()
//                        .success(true)
//                        .message("OTP Successfully Sent")
//                        .data(otp)
//                        .build()
//        );
//    }
    @GetMapping("/google-login")
    public ResponseEntity<BaseApiResponse<String>> googleLogin() {
        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code"
                + "&scope=openid%20email%20profile%20https://www.googleapis.com/auth/userinfo.profile";

        return ResponseEntity.ok(
                BaseApiResponse.<String>builder()
                        .success(true)
                        .message("Url Received")
                        .data(authUrl)
                        .build()
        );
    }

    @PostMapping("/oauth2/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam("code") String authorizationCode) {
        try {
            log.info("Received callback from Google with authorization code: {}", authorizationCode);

            // Exchange authorization code for tokens
//            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
//                    new NetHttpTransport(),
//                    JacksonFactory.getDefaultInstance(),
//                    "https://oauth2.googleapis.com/token",
//                    clientId,
//                    clientSecret,
//                    authorizationCode,
//                    redirectUri
//            ).execute();
//
//            log.info("Token response received: {}", tokenResponse);
//
//            // Decode and verify the ID token
//            String idTokenString = tokenResponse.getIdToken();
//            log.info("ID Token: {}", idTokenString);

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance()
            ).setAudience(Collections.singletonList(clientId)).build();

            GoogleIdToken idToken = verifier.verify(authorizationCode);
            if (idToken != null) {
                log.info("ID Token successfully verified");

                GoogleIdToken.Payload payload = idToken.getPayload();

                // Get user info
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String phoneNumber = (String) payload.get("phone_number");
                String profilePictureUrl = (String) payload.get("picture");


                if (phoneNumber != null) {
                    log.info("User's phone number: {}", phoneNumber);
                } else {
                    log.info("Phone number not available for this user.");
                }

                log.info("User info extracted: Email - {}, Name - {}", email, name);

                // Check if the user exists
                User user = userRepo.findByEmail(email).orElseGet(() -> {
                    // Register new user
                    log.info("User not found, registering new user with email: {}", email);
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setPassword("default");
                    newUser.setPhone(Objects.requireNonNullElse(phoneNumber, "default"));
                    newUser.setLogoPath(Objects.requireNonNullElse(profilePictureUrl, "default"));
                     // Assuming password is not needed for Google login
                    User savedUser = userRepo.save(newUser);
                    log.info("New user registered: {}", savedUser);
                    return savedUser;
                });

                log.info("User retrieved: {}", user);

                // Generate JWT token
                String jwt = JwtTokenHelper.generateToken((UserDetails) user);
                log.info("JWT Token generated: {}", jwt);

                LoginResponse response = new LoginResponse();
                response.setUser(mapper.map(user, UserDto.class));
                response.setToken(jwt);

                return ResponseEntity.ok(
                        BaseApiResponse.<LoginResponse>builder()
                                .success(true)
                                .message("User Registered Successfully!!")
                                .data(response)
                                .build());
            } else {
                log.error("Invalid ID token received");
                return ResponseEntity.status(401).body("Invalid ID token");
            }
        } catch (Exception e) {
            log.error("Error during Google authentication: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error during Google authentication: " + e.getMessage());
        }
    }


    @GetMapping("/current-user/")
    @Operation(
            summary = "Get Current User API",
            description = "This endpoint retrieves the currently logged-in user's data.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserDto> getUser(Principal principal) {
        User user = this.userRepo.findByEmail(principal.getName()).get();
        return new ResponseEntity<>(this.mapper.map(user, UserDto.class), HttpStatus.OK);
    }

    private boolean isValidEmailFormat(String email) {
        // Implement your email validation logic here (e.g., regex check)
        // Example regex for basic validation (not comprehensive)
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex);
    }
}
