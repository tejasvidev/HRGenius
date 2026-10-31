package com.hrgenius.announcement.service;
import com.hrgenius.announcement.dto.*;
import com.hrgenius.announcement.entity.*;
import com.hrgenius.announcement.repository.AnnouncementRepository;
import com.hrgenius.common.ConflictException;
import com.hrgenius.common.PageRequests;
import com.hrgenius.common.PageResponse;
import com.hrgenius.common.ResourceNotFoundException;
import com.hrgenius.employee.repository.EmployeeRepository;
import com.hrgenius.organization.entity.Department;
import com.hrgenius.organization.repository.DepartmentRepository;
import com.hrgenius.notification.service.InAppNotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
@Service public class AnnouncementService {
    private final AnnouncementRepository announcements; private final DepartmentRepository departments; private final EmployeeRepository employees; private final InAppNotificationService notifications;
    public AnnouncementService(AnnouncementRepository a,DepartmentRepository d,EmployeeRepository e,InAppNotificationService n){announcements=a;departments=d;employees=e;notifications=n;}
    @Transactional(readOnly=true) public PageResponse<AnnouncementResponse> visible(String email,Pageable pageable){
        Long departmentId=employees.findByWorkEmailIgnoreCase(email).map(e->e.getDepartment()==null?null:e.getDepartment().getId()).orElse(null);
        return PageResponse.from(announcements.findVisible(AnnouncementStatus.PUBLISHED,departmentId,Instant.now(),PageRequests.capped(pageable)),AnnouncementResponse::from);
    }
    @Transactional(readOnly=true) public PageResponse<AnnouncementResponse> all(Pageable pageable){return PageResponse.from(announcements.findAll(PageRequests.capped(pageable)),AnnouncementResponse::from);}
    @Transactional public AnnouncementResponse create(AnnouncementRequest request,String email){Announcement a=new Announcement(request.title(),request.body(),department(request.departmentId()),request.priority(),request.expiresAt(),email);return AnnouncementResponse.from(announcements.save(a));}
    @Transactional public AnnouncementResponse update(long id,AnnouncementRequest request){Announcement a=get(id);try{a.update(request.title(),request.body(),department(request.departmentId()),request.priority(),request.expiresAt());}catch(IllegalStateException|IllegalArgumentException ex){throw new ConflictException(ex.getMessage());}return AnnouncementResponse.from(a);}
    @Transactional public AnnouncementResponse publish(long id){Announcement a=get(id);try{a.publish();}catch(IllegalStateException ex){throw new ConflictException(ex.getMessage());}notifications.announce(a);return AnnouncementResponse.from(a);}
    private Announcement get(long id){return announcements.findById(id).orElseThrow(()->new ResourceNotFoundException("Announcement",id));}
    private Department department(Long id){if(id==null)return null;Department d=departments.findById(id).orElseThrow(()->new ResourceNotFoundException("Department",id));if(!d.isActive())throw new ConflictException("Announcement department must be active");return d;}
}
