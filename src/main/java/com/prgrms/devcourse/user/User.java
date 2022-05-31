package com.prgrms.devcourse.user;

import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "profile_image")
    private String profileImage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

    protected User() {
    }

    public User(String username, String provider, String providerId, String profileImage, Group group) {
        checkArgument(isNotEmpty(username), "username must me provided");
        checkArgument(isNotEmpty(provider), "provider must me provided");
        checkArgument(isNotEmpty(providerId), "providerId must me provided");
        checkArgument(group != null, "group must me provided");
        this.username = username;
        this.provider = provider;
        this.providerId = providerId;
        this.profileImage = profileImage;
        this.group = group;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public Optional<String> getProfileImage() {
        return Optional.ofNullable(profileImage);
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", provider='" + provider + '\'' +
                ", providerId='" + providerId + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", group=" + group +
                '}';
    }
}
