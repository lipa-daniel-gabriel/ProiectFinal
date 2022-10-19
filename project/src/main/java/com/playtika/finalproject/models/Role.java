package com.playtika.finalproject.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<Player> user;

    @Override
    public String getAuthority() {
        return name;
    }

    public Role(RoleType roleType){
        this.name = roleType.name();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Role) {
            Role roleObj = (Role) o;
            return this.name.equals(roleObj.name);
        } else return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}