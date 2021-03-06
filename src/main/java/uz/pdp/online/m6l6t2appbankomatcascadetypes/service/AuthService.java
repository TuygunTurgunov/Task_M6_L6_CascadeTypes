package uz.pdp.online.m6l6t2appbankomatcascadetypes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Card;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Role;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.User;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.RoleName;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.ApiResponse;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.LoginDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.RegisterDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.CardRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.RoleRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.UserRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.security.JwtProvider;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;//parollani encode qilishga

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private  JavaMailSender javaMailSender;//Mail bilan ishlash ga

    @Autowired
    private AuthenticationManager authenticationManager;//sistemaga login qilinvotganda parol va login larni
    //tekshirib solishtirib ketadi login method da ishlatdim


    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private  CardRepository cardRepository;
    @Autowired
    private  UserType userType;

    public ApiResponse registerUser(RegisterDto registerDto) {

         boolean existsByEmail = userRepository.existsByEmail(registerDto.getEmail());
        if (existsByEmail)
            return new ApiResponse("Such kind of email already exists", false);


        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setEmail(registerDto.getEmail());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Integer userType = this.userType.getUserType(currentUser);
        if (userType!=1)
            return new ApiResponse("Faqt director ishchi qo'sholidi",false);


        //Password ni database ga shifrlab saqlash kere encode qilib
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));


        //Save qilishdan oldin email ga habar boradi linkga bosganida enable = true bo'ladi keyin save qilamiz
        user.setEmailCode(UUID.randomUUID().toString());
        userRepository.save(user);


        //Emailga yuborish metodi
        sendEmail(user.getEmail(), user.getEmailCode());
        return new ApiResponse("muvaffaqiyatli ro'yxatdan o'tdingi.Accont aktivlashtirish uchun email ni tasdiqlang emailga xabar yuborildi", true);


    }

    public Boolean sendEmail(String sendingEmail, String emailCode) {
        try {

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("test@pdp.com");
            mailMessage.setTo(sendingEmail);
            mailMessage.setSubject("Account ni Tasdiqlash");
            mailMessage.setText("<a href='http://localhost:8080/api/auth/verifyEmail?emailCode=" + emailCode + "&email=" + sendingEmail + "'>Tasdiqlang</a>");
            javaMailSender.send(mailMessage);
            return true;

        } catch (Exception e) {

            return false;

        }
    }

    public ApiResponse verifyEmail(String emailCode, String email, LoginDto loginDto) {

        Optional<User> optionalUser = userRepository.findByEmailAndEmailCode(email, emailCode);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEnabled(true);
            user.setEmailCode(null);

            if (loginDto.getUsername() != null) {
                Optional<User> optionalUser2 = userRepository.findByEmail(loginDto.getUsername());
                if (optionalUser2.isPresent())
                    return new ApiResponse("bunday username bor", false);
                user.setEmail(loginDto.getUsername());
            }
            if (loginDto.getPassword() != null)
                user.setPassword(passwordEncoder.encode(loginDto.getPassword()));

            userRepository.save(user);
            return new ApiResponse("Account tasdiqlandi", true);

        }
        return new ApiResponse("account allaqachon tasdiqlangan", false);


    }

     public ApiResponse login(LoginDto loginDto) {
        try {

            //loadUserByUsername() => shu method ni avtomat o'zi qidiradi
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(),
                    loginDto.getPassword()));


            User user = (User) authentication.getPrincipal();

            String token = jwtProvider.generateToken(loginDto.getUsername(), user.getRoles());
            return new ApiResponse("Token", true, token);


        } catch (BadCredentialsException badCredentialsException) {

            return new ApiResponse("parol yoki login hato", false);
        }

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username + "topilmadi"));

    }
    public UserDetails loadClientByCardNumber(Long cardNumber){

        return cardRepository.findByCardNumber(cardNumber).orElseThrow(() -> new UsernameNotFoundException(cardNumber.toString()));

    }
}