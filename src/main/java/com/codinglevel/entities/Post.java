package com.codinglevel.entities;

import com.codinglevel.enumerations.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post_table")
public class Post {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long postId;
    private String subject;
    private String description;
    private String userName;

    @Enumerated(EnumType.STRING)
    private PostStatus status;
}
