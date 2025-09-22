package com.example.efc_user.services.impl;



import com.example.efc_user.collections.DeliveryBoy;
import com.example.efc_user.collections.User;
import com.example.efc_user.exceptions.EmailAlreadyExistsException;
import com.example.efc_user.exceptions.ResourceNotFoundException;
import com.example.efc_user.payloads.*;
import com.example.efc_user.repo.DeliveryBoyRepo;
import com.example.efc_user.repo.UserRepo;
import com.example.efc_user.response.UserResponse;
import com.example.efc_user.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    UserRepo userRepo;
//    @Autowired
    @Autowired
    DeliveryBoyRepo deliveryBoyRepo;
//    OtpService otpService;
//    @Autowired
//    EmailService emailService;



    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;


    @Override
    public UserDto registerNewUser(RegisterRequest registerRequest) {
        // Check if the email is already registered
        if (this.userRepo.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email " + registerRequest.getEmail() + " is already registered.");
        }
        log.info(registerRequest.getPassword()+registerRequest.getConfirmPassword());
        if(!Objects.equals(registerRequest.getPassword(), registerRequest.getConfirmPassword())){
            throw new EmailAlreadyExistsException("Paasword and Confirmpassword not same");

        }
//        boolean isOtpValid = otpService.verifyOtp(registerRequest.getEmail(), String.valueOf(registerRequest.getOtp()));
//        if(isOtpValid){
            User user = this.modelMapper.map(registerRequest, User.class);

            // Explicitly set fields with different names
            user.setPhone(String.valueOf(registerRequest.getPhone()));

            // Encode password
            user.setPassword(this.passwordEncoder.encode(registerRequest.getPassword()));

            // Save user
            User newUser = this.userRepo.save(user);

            // Map User to UserDto
            return this.modelMapper.map(newUser, UserDto.class);

//        }
//        else{
//            throw new EmailAlreadyExistsException("Invalid Otp!!");
//
//        }

        // Map RegisterRequest to User

    }

    @Override
    public DeliveryBoyDto registerNewDeliveryBoy(RegisterRequest registerRequest) {
        // Check if the email is already registered
        if (this.deliveryBoyRepo.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email " + registerRequest.getEmail() + " is already registered.");
        }
        log.info(registerRequest.getPassword()+registerRequest.getConfirmPassword());
        if(!Objects.equals(registerRequest.getPassword(), registerRequest.getConfirmPassword())){
            throw new EmailAlreadyExistsException("Paasword and Confirmpassword not same");

        }
//        boolean isOtpValid = otpService.verifyOtp(registerRequest.getEmail(), String.valueOf(registerRequest.getOtp()));
//        if(isOtpValid){
        DeliveryBoy user = this.modelMapper.map(registerRequest, DeliveryBoy.class);

        // Explicitly set fields with different names
        user.setPhone(String.valueOf(registerRequest.getPhone()));

        // Encode password
        user.setPassword(this.passwordEncoder.encode(registerRequest.getPassword()));

        // Save user
        DeliveryBoy newUser = this.deliveryBoyRepo.save(user);

        // Map User to UserDto
        return this.modelMapper.map(newUser, DeliveryBoyDto.class);

//        }
//        else{
//            throw new EmailAlreadyExistsException("Invalid Otp!!");
//
//        }

        // Map RegisterRequest to User

    }

    @Override
    public UserDto createUser(UserDto userDto) {

        User user = this.dtoToUser(userDto);
        User savedUser = this.userRepo.save(user);
        return this.userToDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto user, String userId) {
        return null;
    }

    //    @Override
//    public UserDto updateUser(UserDto userDto, Integer userId) {
//
//        User user = this.userRepo.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User", " Id ", userId));
//
//        user.setName(userDto.getName());
//        user.setEmail(userDto.getEmail());
//        user.setPassword(userDto.getPassword());
//        user.setBatch(userDto.getBatch());
//        user.setPhoneNumber(userDto.getPhoneNumber());
//        user.setRegdNo(userDto.getRegdNo());
//
//
//        User updatedUser = this.userRepo.save(user);
//        UserDto userDto1 = this.userToDto(updatedUser);
//        return userDto1;
//    }
    @Override


    public UserDto getUserById(String userId) {

        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", " Id ", userId));

        return this.userToDto(user);
    }

    @Override
    public UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable p = PageRequest.of(pageNumber, pageSize, sort);

        Page<User> user = this.userRepo.findAll(p);

        List<User> allUsers = user.getContent();

        List<UserDto> userDtos = allUsers.stream().map((user1) -> this.modelMapper.map(user1, UserDto.class))
                .collect(Collectors.toList());

        UserResponse userResponse = new UserResponse();

        userResponse.setContent(userDtos);
        userResponse.setPageNumber(user.getNumber());
        userResponse.setPageSize(user.getSize());
        userResponse.setTotalElements(user.getTotalElements());

        userResponse.setTotalPages(user.getTotalPages());
        userResponse.setLastPage(user.isLast());

        return userResponse;



    }

    @Override
    public void deleteUser(String userId) {
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
        this.userRepo.delete(user);

    }
    public UserDto updateUserProfile(Principal principal, UpdateUserProfileRequest request) {
        User user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getPhoneNumber() != null) {
            user.setPhone(request.getPhoneNumber());
        }

        if (request.getProfilePicturePath() != null) {
            user.setLogoPath(request.getProfilePicturePath());
        }

        User savedUser=userRepo.save(user);
        return modelMapper.map(savedUser,UserDto.class);
    }

//    public void updateEmail(String currentEmail, UpdateEmailRequest request) {
//        // Generate and send OTP to old email
//        String oldEmailOtp = otpService.generateOtp(currentEmail);
//        emailService.sendSimpleMessage(currentEmail, "OTP for Email Update", "Your OTP: " + oldEmailOtp);
//
//        // Generate and send OTP to new email
//        String newEmailOtp = otpService.generateOtp(request.getNewEmail());
//        emailService.sendSimpleMessage(request.getNewEmail(), "OTP for Email Registration", "Your OTP: " + newEmailOtp);
//
//        // Validate old email OTP
//        if (!otpService.verifyOtp(currentEmail, request.getOldEmailOtp())) {
//            throw new IllegalArgumentException("Invalid OTP for registered email.");
//        }
//
//        // Validate new email OTP
//        if (!otpService.verifyOtp(request.getNewEmail(), request.getNewEmailOtp())) {
//            throw new IllegalArgumentException("Invalid OTP for new email.");
//        }
//
//        // Update email
//        User user = userRepo.findByEmail(currentEmail)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        user.setEmail(request.getNewEmail());
//        userRepo.save(user);
//    }
//    public void updatePassword(String email, UpdatePasswordRequest request) {
//        User user = userRepo.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        // Verify OTP (This logic assumes an OTP verification service exists)
//        if (!otpService.verifyOtp(email, request.getOtp())) {
//            throw new IllegalArgumentException("Invalid OTP");
//        }
//
//        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
//            throw new IllegalArgumentException("Incorrect current password");
//        }
//
//        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
//            throw new IllegalArgumentException("Passwords do not match");
//        }
//
//        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
//        userRepo.save(user);
//    }



    public User dtoToUser(UserDto userDto) {
        User user = this.modelMapper.map(userDto, User.class);

        // user.setId(userDto.getId());
        // user.setName(userDto.getName());
        // user.setEmail(userDto.getEmail());
        // user.setAbout(userDto.getAbout());
        // user.setPassword(userDto.getPassword());
        return user;
    }

    public UserDto userToDto(User user) {
        UserDto userDto = this.modelMapper.map(user, UserDto.class);
        return userDto;
    }
}
