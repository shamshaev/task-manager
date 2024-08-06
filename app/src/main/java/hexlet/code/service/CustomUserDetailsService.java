package hexlet.code.service;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

//@Service
//public class CustomUserDetailsService implements UserDetailsManager {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User with email '" + email + "' not found"));
//    }
//
//    @Override
//    public void createUser(UserDetails userData) {
//        var user = new User();
//        user.setEmail(userData.getUsername());
//        var hashedPassword = passwordEncoder.encode(userData.getPassword());
//        user.setPasswordDigest(hashedPassword);
//        userRepository.save(user);
//    }
//
//    @Override
//    public void updateUser(UserDetails user) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
//    }
//
//    @Override
//    public void deleteUser(String username) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
//    }
//
//    @Override
//    public void changePassword(String oldPassword, String newPassword) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
//    }
//
//    @Override
//    public boolean userExists(String username) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Unimplemented method 'userExists'");
//    }
//}
