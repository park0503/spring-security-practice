package com.prgrms.devcourse.user;

import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 20, unique = true, nullable = false, name = "login_id")
    private String loginId;

    @Column(length = 80, nullable = false)
    private String passwd;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

    public User() {
    }

    public void checkPassword(PasswordEncoder passwordEncoder, String credentials) {
        if (!passwordEncoder.matches(credentials, this.passwd)) {
            throw new IllegalArgumentException("Bad credentials");
        }
    }

    public Long getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPasswd() {
        return passwd;
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", loginId='" + loginId + '\'' +
                ", passwd='" + passwd + '\'' +
                ", group=" + group +
                '}';
    }
}
