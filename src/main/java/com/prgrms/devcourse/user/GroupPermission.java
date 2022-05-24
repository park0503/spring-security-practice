package com.prgrms.devcourse.user;

import javax.persistence.*;

@Table(name = "group_permission")
@Entity
public class GroupPermission {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", referencedColumnName = "id")
    private Permission permission;

    public Long getId() {
        return id;
    }

    public Group getGroup() {
        return group;
    }

    public Permission getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return "GroupPermission{" +
                "id=" + id +
                ", group=" + group +
                ", permission=" + permission +
                '}';
    }
}
