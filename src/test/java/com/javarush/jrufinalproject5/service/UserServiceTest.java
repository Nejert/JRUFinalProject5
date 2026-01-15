package com.javarush.jrufinalproject5.service;

import com.javarush.jrufinalproject5.dto.UserDto;
import com.javarush.jrufinalproject5.dto.user.UserIn;
import com.javarush.jrufinalproject5.dto.user.UserOut;
import com.javarush.jrufinalproject5.entity.Role;
import com.javarush.jrufinalproject5.entity.User;
import com.javarush.jrufinalproject5.repository.InitialDataBaseEntities;
import com.javarush.jrufinalproject5.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final UserDto mapper = Mappers.getMapper(UserDto.class);
    @Mock
    private UserRepository userRepository;
    private UserService userService;

    private User first;
    private User second;
    private User third;
    private UserOut firstOut;
    private UserOut secondOut;
    private UserOut thirdOut;
    private UserIn thirdIn;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, mapper);
        first = InitialDataBaseEntities.ADMIN;
        second = InitialDataBaseEntities.USER;
        firstOut = mapper.from(first);
        secondOut = mapper.from(second);
        thirdIn = new UserIn(null, "newUser", "newUser", "test@user.com", Role.USER);
        third = mapper.from(thirdIn);
        thirdOut = mapper.from(third);
    }

    @Test
    void getAllUsersTest() {
        // Given
        when(userRepository.findAll()).thenReturn(List.of(first, second));

        // When
        List<UserOut> result = userService.getAllUsers();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(firstOut);
        assertThat(result.get(1)).isEqualTo(secondOut);
        verify(userRepository).findAll();
    }

    @Test
    void findByIdTest() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(first));

        // When
        UserOut result = userService.findById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(firstOut);
    }

    @Test
    void findByIdExceptionTest() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> userService.findById(1L));
    }

    @Test
    void createUserTest() {
        // Given
        when(userRepository.save(third)).thenReturn(third);

        // When
        UserOut result = userService.createUser(thirdIn);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(thirdOut);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void patchUpdateUserTest() {
        // Given
        Long id = 1L;
        UserIn patch = new UserIn();
        patch.setLogin("newLogin");

        when(userRepository.findById(id)).thenReturn(Optional.of(first));
        when(userRepository.save(any(User.class))).thenReturn(first);

        // When
        UserOut result = userService.patchUpdateUser(id, patch);

        // Then
        assertThat(result.getLogin()).isEqualTo("newLogin");
        verify(userRepository).findById(id);
    }

    @Test
    void deleteUserTest() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(first));

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserExceptionTest() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.deleteUser(1L));
        assertThat(exception.getMessage()).isEqualTo("User not found!");
    }
}

