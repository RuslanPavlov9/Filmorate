//package ru.yandex.practicum.filmorate.storage;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import ru.yandex.practicum.filmorate.repository.dao.User;
//import ru.yandex.practicum.filmorate.repository.UserRepository;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class UserDbStorage implements UserStorage {
//    private final UserRepository userRepository;
//
//    @Override
//    public User addUser(User user) {
//        return userRepository.save(user);
//    }
//
//    @Override
//    public User updateUser(User user) {
//        if (!userRepository.existsById(user.getId())) {
//            throw new NotFoundException("Пользователь не найден");
//        }
//        return userRepository.save(user);
//    }
//
//    @Override
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    @Override
//    public User getUserById(int id) {
//        return userRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
//    }
//}