package com.fpt.cpdm.services.impl;

import com.fpt.cpdm.entities.RoleEntity;
import com.fpt.cpdm.entities.UserEntity;
import com.fpt.cpdm.exceptions.NotAllowException;
import com.fpt.cpdm.exceptions.users.RoleNameNotFoundException;
import com.fpt.cpdm.exceptions.users.UserEmailDuplicateException;
import com.fpt.cpdm.exceptions.users.UserIdNotFoundException;
import com.fpt.cpdm.models.User;
import com.fpt.cpdm.repositories.RoleRepository;
import com.fpt.cpdm.repositories.UserRepository;
import com.fpt.cpdm.services.UserService;
import com.fpt.cpdm.utils.ModelConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final String ROLE_PREFIX = "ROLE_";

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserEntity> optional = userRepository.findByEmail(username);
        return optional.orElseThrow(
                () -> new UsernameNotFoundException("User with email " + username + " not found!")
        );
    }

    @Override
    public User save(User user) {

        // check id exist
        if (user.getId() != null && userRepository.existsById(user.getId()) == false) {
            throw new UserIdNotFoundException(user.getId());
        }

        // check email duplicate
        if (user.getId() == null && userRepository.existsByEmail(user.getEmail())) {
            throw new UserEmailDuplicateException(user.getEmail());
        }

        // encode password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        UserEntity userEntity = ModelConverter.userModelToEntity(user);

        // check role
        String roleName = ROLE_PREFIX + user.getRole().getName();
        RoleEntity roleEntity = roleRepository.findByName(roleName).orElseThrow(
                () -> new RoleNameNotFoundException(roleName)
        );
        userEntity.setRole(roleEntity);

        UserEntity savedUserEntity = userRepository.save(userEntity);
        User savedUser = ModelConverter.userEntityToModel(savedUserEntity);

        return savedUser;
    }


    @Override
    public List<User> saveAll(List<User> users) {

        // check id exist
        for (User user : users) {
            if (user.getId() != null && userRepository.existsById(user.getId())) {
                throw new UserIdNotFoundException(user.getId());
            }
        }

        // check email duplicate
        for (User user : users) {
            if (user.getId() == null && userRepository.existsByEmail(user.getEmail())) {
                throw new UserEmailDuplicateException(user.getEmail());
            }
        }

        // encode password
        for (User user : users) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }

        // convert user models to entities and set roles
        List<UserEntity> userEntities = new ArrayList<>();
        for (User user : users) {
            UserEntity userEntity = ModelConverter.userModelToEntity(user);
            String roleName = ROLE_PREFIX + user.getRole().getName();
            RoleEntity roleEntity = roleRepository.findByName(roleName).orElseThrow(
                    () -> new RoleNameNotFoundException(roleName)
            );
            userEntity.setRole(roleEntity);
            userEntities.add(userEntity);
        }

        List<UserEntity> savedUserEntities = userRepository.saveAll(userEntities);

        // convert all user entities to models
        List<User> savedUsers = getUsersConverted(savedUserEntities);

        return savedUsers;
    }

    @Override
    public User findById(Integer id) {

        Optional<UserEntity> optional = userRepository.findById(id);
        UserEntity userEntity = optional.orElseThrow(
                () -> new UserIdNotFoundException(id)
        );
        User user = ModelConverter.userEntityToModel(userEntity);

        return user;
    }

    @Override
    public boolean existsById(Integer id) {

        return userRepository.existsById(id);
    }

    @Override
    public List<User> findAll() {

        List<UserEntity> userEntities = userRepository.findAll();

        // convert all user entities to models
        List<User> users = getUsersConverted(userEntities);

        return users;
    }

    @Override
    public List<User> findAllById(List<Integer> ids) {

        List<UserEntity> userEntities = userRepository.findAllById(ids);

        // convert all user entities to models
        List<User> users = getUsersConverted(userEntities);

        return users;
    }

    @Override
    public long count() {

        return userRepository.count();
    }

    @Override
    public void deleteById(Integer id) {

        userRepository.deleteById(id);
    }

    @Override
    public void delete(User user) {

        UserEntity userEntity = ModelConverter.userModelToEntity(user);

        userRepository.delete(userEntity);
    }

    @Override
    public void deleteAll(List<User> users) {

        // convert all user models to entities
        List<UserEntity> userEntities = getUserEntitiesConverted(users);

        userRepository.deleteAll(userEntities);
    }

    @Override
    public void deleteAll() {

        throw new NotAllowException("This method is not allowed");
    }

    private List<User> getUsersConverted(List<UserEntity> userEntities) {

        List<User> users = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            User user = ModelConverter.userEntityToModel(userEntity);
            users.add(user);
        }

        return users;
    }

    private List<UserEntity> getUserEntitiesConverted(List<User> users) {

        List<UserEntity> userEntities = new ArrayList<>();
        for (User user : users) {
            UserEntity userEntity = ModelConverter.userModelToEntity(user);
            userEntities.add(userEntity);
        }

        return userEntities;
    }

}
