package com.hrgenius.performance.service;

import com.hrgenius.common.ConflictException;
import com.hrgenius.common.PageRequests;
import com.hrgenius.common.PageResponse;
import com.hrgenius.common.ResourceNotFoundException;
import com.hrgenius.employee.entity.Employee;
import com.hrgenius.employee.entity.EmployeeStatus;
import com.hrgenius.employee.repository.EmployeeRepository;
import com.hrgenius.performance.dto.*;
import com.hrgenius.performance.entity.PerformanceReview;
import com.hrgenius.performance.repository.PerformanceReviewRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerformanceService {
    private final PerformanceReviewRepository reviews;
    private final EmployeeRepository employees;
    public PerformanceService(PerformanceReviewRepository reviews, EmployeeRepository employees){this.reviews=reviews;this.employees=employees;}
    @Transactional
    public PerformanceReviewResponse create(PerformanceReviewCreateRequest body){
        Employee employee=employees.findById(body.employeeId()).orElseThrow(()->new ResourceNotFoundException("Employee",body.employeeId()));
        if(employee.getStatus()!=EmployeeStatus.ACTIVE)throw new ConflictException("Reviews can only be created for active employees");
        if(employee.getManager()==null)throw new ConflictException("Assign a manager to the employee before creating a review");
        if(reviews.existsByCycleNameIgnoreCaseAndEmployee_Id(body.cycleName().trim(),employee.getId()))throw new ConflictException("A review already exists for this employee and cycle");
        return PerformanceReviewResponse.from(reviews.save(new PerformanceReview(body.cycleName(),employee,employee.getManager())));
    }
    @Transactional(readOnly=true) public PageResponse<PerformanceReviewResponse> mine(String email,Pageable pageable){return PageResponse.from(reviews.findAllByEmployee_WorkEmailIgnoreCase(email,PageRequests.capped(pageable)),PerformanceReviewResponse::from);}
    @Transactional(readOnly=true) public PageResponse<PerformanceReviewResponse> team(String email,Pageable pageable){return PageResponse.from(reviews.findAllByReviewer_WorkEmailIgnoreCase(email,PageRequests.capped(pageable)),PerformanceReviewResponse::from);}
    @Transactional(readOnly=true) public PageResponse<PerformanceReviewResponse> all(Pageable pageable){return PageResponse.from(reviews.findAll(PageRequests.capped(pageable)),PerformanceReviewResponse::from);}
    @Transactional public PerformanceReviewResponse self(String email,long id,PerformanceSelfReviewRequest body){
        PerformanceReview review=reviews.findByIdAndEmployee_WorkEmailIgnoreCase(id,email).orElseThrow(()->new ResourceNotFoundException("Performance review",id));
        try{review.selfReview(body.summary(),body.rating());}catch(IllegalStateException ex){throw new ConflictException(ex.getMessage());}
        return PerformanceReviewResponse.from(review);
    }
    @Transactional public PerformanceReviewResponse manager(String email,long id,PerformanceManagerReviewRequest body){
        PerformanceReview review=reviews.findByIdAndReviewer_WorkEmailIgnoreCase(id,email).orElseThrow(()->new ResourceNotFoundException("Performance review",id));
        try{review.managerReview(body.feedback(),body.rating());}catch(IllegalStateException ex){throw new ConflictException(ex.getMessage());}
        return PerformanceReviewResponse.from(review);
    }
}
