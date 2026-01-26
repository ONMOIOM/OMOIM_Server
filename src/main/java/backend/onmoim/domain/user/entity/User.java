package backend.onmoim.domain.user.entity;


import backend.onmoim.domain.user.enums.Status;
import backend.onmoim.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "introduction", length = 255)
    private String introduction;

    @Column(name = "email" , nullable = false, length = 255)
    private String email;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Column(name = "instagram_id", length = 255)
    private String instagramId;

    @Column(name = "twitter_id", length = 255)
    private String twitterId;

    @Column(name = "linkedin_id", length = 255)
    private String linkedinId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, optional = true)
    @JoinColumn(name = "profile_image_id")
    private ProfileImage profileImage;
}