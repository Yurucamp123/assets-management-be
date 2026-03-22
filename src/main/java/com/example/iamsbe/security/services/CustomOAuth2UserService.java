package com.example.iamsbe.security.services;

import com.example.iamsbe.models.entities.User;
import com.example.iamsbe.models.enums.Role;
import com.example.iamsbe.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Facebook luôn trả về "id", Google trả về "sub"
        String providerId = attributes.get("id") != null ?
                attributes.get("id").toString() :
                attributes.get("sub").toString();

        String email = (String) attributes.get("email");
        String fullName = (String) attributes.get("name");

        // Xử lý case Facebook không có email
        if (email == null) {
            // Tạo email ảo dựa trên ID và Provider để tránh trùng lặp
            email = providerId + "@" + provider + ".com";
        }

        String finalEmail = email;
        User user = userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, fullName, provider))
                .orElseGet(() -> registerNewUser(finalEmail, fullName, provider));

        return UserDetailsImpl.build(user);
    }

    private User registerNewUser(String email, String fullName, String provider) {
        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setUsername(email);
        user.setProvider(provider);
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, String fullName, String provider) {
        existingUser.setFullName(fullName);
        existingUser.setProvider(provider);
        return userRepository.save(existingUser);
    }
}