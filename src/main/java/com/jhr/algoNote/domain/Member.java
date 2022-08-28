package com.jhr.algoNote.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id" , updatable = false )
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Column(unique = true)
    private String email;

    private String picture;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private final List<Problem> problems = new ArrayList<>();

    @NotNull
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private final List<Review> reviews = new ArrayList<>();

    @Builder
    public Member(@NonNull String name, @NonNull String email, String picture, Role role) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
    }

    // == 비스니스 로직 ==//

    public Member updateName(String name) {
        this.name = name;
        return this;
    }

    public Member updatePicture(String picture) {
        this.picture = picture;
        return this;
    }

    @Generated
    public String getRoleKey() {
        return this.role.getKey();
    }
}
