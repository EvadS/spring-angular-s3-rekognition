package com.se.sample.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(indexes = @Index(columnList = "name"))
public class ImageLabel {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "confidence")
    private Float confidence;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image image;
}
