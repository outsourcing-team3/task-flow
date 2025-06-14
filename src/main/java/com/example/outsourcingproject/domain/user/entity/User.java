package com.example.outsourcingproject.domain.user.entity;

import com.example.outsourcingproject.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String username;  // 로그인 아이디

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;      // 실명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole userRole; // USER, ADMIN


    public static User createUser(String username, String email, String password, String name, UserRole userRole) {
        User user = new User();
        user.username = username;
        user.email = email;
        user.password = password;
        user.name = name;
        user.userRole = userRole;
        return user;
    }

    public void updateRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public User(String email, String password, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }
}