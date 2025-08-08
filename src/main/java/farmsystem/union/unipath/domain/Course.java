package farmsystem.union.unipath.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "courses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {

    @Id
    @Column(name = "class_id", nullable = false)
    private String classId;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column
    private String credits;

    @Column
    private String theory;

    @Column
    private String practice;

    @Column(name = "required_division")
    private String requiredDivision;

    @Column(name = "target_credit")
    private String targetCredit;

    @Column(name = "recommended_semester")
    private String recommendedSemester;

    @Column
    private String notes;

    // Career Relevance Scores
    @Column(name = "score_web_dev")
    private Integer scoreWebDev;

    @Column(name = "score_app_dev")
    private Integer scoreAppDev;

    @Column(name = "score_game_dev")
    private Integer scoreGameDev;

    @Column(name = "score_data_ai_dev")
    private Integer scoreDataAiDev;

    @Column(name = "score_infosec")
    private Integer scoreInfosec;

    @Column(name = "score_cloud_devops")
    private Integer scoreCloudDevops;

    @Column(name = "score_tech_pm")
    private Integer scoreTechPm;

    @Column(name = "score_special_tech")
    private Integer scoreSpecialTech;

    @Builder
    public Course(String classId, String className, String credits, String theory, String practice,
                  String requiredDivision, String targetCredit, String recommendedSemester, String notes,
                  Integer scoreWebDev, Integer scoreAppDev, Integer scoreGameDev, Integer scoreDataAiDev,
                  Integer scoreInfosec, Integer scoreCloudDevops, Integer scoreTechPm, Integer scoreSpecialTech) {
        this.classId = classId;
        this.className = className;
        this.credits = credits;
        this.theory = theory;
        this.practice = practice;
        this.requiredDivision = requiredDivision;
        this.targetCredit = targetCredit;
        this.recommendedSemester = recommendedSemester;
        this.notes = notes;
        this.scoreWebDev = scoreWebDev;
        this.scoreAppDev = scoreAppDev;
        this.scoreGameDev = scoreGameDev;
        this.scoreDataAiDev = scoreDataAiDev;
        this.scoreInfosec = scoreInfosec;
        this.scoreCloudDevops = scoreCloudDevops;
        this.scoreTechPm = scoreTechPm;
        this.scoreSpecialTech = scoreSpecialTech;
    }
}