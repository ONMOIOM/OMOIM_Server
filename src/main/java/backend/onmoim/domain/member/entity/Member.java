package backend.onmoim.domain.member.entity;


import backend.onmoim.domain.member.enums.Status;
import backend.onmoim.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "nickname" , nullable = false, length = 50)
    private String nickname;

    @Column(name = "introduction", length = 255)
    private String introduction;

    @Column(name = "email" , nullable = false, length = 255)
    private String email;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "instagram_id", length = 255)
    private String instagramId;

    @Column(name = "twitter_id", length = 255)
    private String twitterId;

    @Column(name = "linkedin_id", length = 255)
    private String linkedinId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "member_id")
    private ProfileImage profileImage;
}