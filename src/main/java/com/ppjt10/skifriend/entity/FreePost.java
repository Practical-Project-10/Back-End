package com.ppjt10.skifriend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ppjt10.skifriend.time.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class FreePost extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private String skiResort;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String Image;

    @OneToMany(mappedBy = "freePost", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"freePost"})
    List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "freePost", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"freePost"})
    List<Likes> likeList = new ArrayList<>();
}
