package com.ismail.todoapp.service;


import com.ismail.todoapp.dto.auth.AuthRequest;
import com.ismail.todoapp.dto.auth.AuthResponse;
import com.ismail.todoapp.entity.User;
import com.ismail.todoapp.exception.BadRequestException;
import com.ismail.todoapp.exception.ConflictException;
import com.ismail.todoapp.exception.ResourceNotFoundException;
import com.ismail.todoapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(AuthRequest request){
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new ConflictException("Kullanici adi alinmis: " + request.getUsername());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        userRepository.save(user);
        return "finito";
    }


    public AuthResponse login (AuthRequest request){
        // veritabanindan kullaniciyi bul
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanici bulunamadi: " + request.getUsername()));

        // sifre eslesiyor mu
        if(passwordEncoder.matches(request.getPassword(),user.getPassword())){
            // sifre dogruysa jwt service ile token uret ve DTO ile don
            String token = jwtService.generateToken(user.getUsername());
            AuthResponse response = new AuthResponse();
            response.setAccessToken(token);
            response.setTokenType("Bearer");
            return response;
        } else {
            throw new BadRequestException("Sifre hatali");
        }

    }


}
