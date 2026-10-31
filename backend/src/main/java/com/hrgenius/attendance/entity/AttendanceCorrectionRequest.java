package com.hrgenius.attendance.entity;

import com.hrgenius.employee.entity.Employee;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name="attendance_correction_requests",uniqueConstraints=@UniqueConstraint(name="uk_attendance_correction_employee_date",columnNames={"employee_id","work_date"}))
public class AttendanceCorrectionRequest {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="employee_id",nullable=false) private Employee employee;
    @Column(name="work_date",nullable=false) private LocalDate workDate;
    @Column(name="requested_check_in_at",nullable=false) private Instant requestedCheckInAt;
    @Column(name="requested_check_out_at",nullable=false) private Instant requestedCheckOutAt;
    @Column(nullable=false,length=1000) private String reason;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private CorrectionStatus status=CorrectionStatus.PENDING;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="reviewer_id") private Employee reviewer;
    @Column(name="reviewer_comment",length=1000) private String reviewerComment;
    @Column(name="reviewed_at") private Instant reviewedAt;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
    protected AttendanceCorrectionRequest() { }
    public AttendanceCorrectionRequest(Employee employee,LocalDate day,Instant in,Instant out,String reason){this.employee=employee;workDate=day;requestedCheckInAt=in;requestedCheckOutAt=out;this.reason=reason.trim();}
    public void decide(Employee reviewer,boolean approve,String comment){if(status!=CorrectionStatus.PENDING)throw new IllegalStateException("Only pending corrections can be reviewed");this.reviewer=reviewer;status=approve?CorrectionStatus.APPROVED:CorrectionStatus.REJECTED;reviewerComment=comment==null||comment.isBlank()?null:comment.trim();reviewedAt=Instant.now();}
    @PrePersist void onCreate(){createdAt=Instant.now();}
    public Long getId(){return id;} public Employee getEmployee(){return employee;} public LocalDate getWorkDate(){return workDate;} public Instant getRequestedCheckInAt(){return requestedCheckInAt;} public Instant getRequestedCheckOutAt(){return requestedCheckOutAt;} public String getReason(){return reason;} public CorrectionStatus getStatus(){return status;} public Employee getReviewer(){return reviewer;} public String getReviewerComment(){return reviewerComment;} public Instant getReviewedAt(){return reviewedAt;}
}
