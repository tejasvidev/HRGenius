package com.hrgenius.notification.service;
import com.hrgenius.announcement.entity.Announcement;
import com.hrgenius.common.PageRequests;
import com.hrgenius.common.PageResponse;
import com.hrgenius.common.ResourceNotFoundException;
import com.hrgenius.employee.entity.Employee;
import com.hrgenius.employee.entity.EmployeeStatus;
import com.hrgenius.employee.repository.EmployeeRepository;
import com.hrgenius.notification.dto.InAppNotificationResponse;
import com.hrgenius.notification.entity.InAppNotification;
import com.hrgenius.notification.repository.InAppNotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service public class InAppNotificationService {
    private final InAppNotificationRepository notifications; private final EmployeeRepository employees;
    public InAppNotificationService(InAppNotificationRepository n,EmployeeRepository e){notifications=n;employees=e;}
    @Transactional(readOnly=true) public PageResponse<InAppNotificationResponse> mine(String email,Pageable p){return PageResponse.from(notifications.findAllByEmployee_WorkEmailIgnoreCaseOrderByCreatedAtDesc(email,PageRequests.capped(p)),InAppNotificationResponse::from);}
    @Transactional(readOnly=true) public long unreadCount(String email){return notifications.countByEmployee_WorkEmailIgnoreCaseAndReadAtIsNull(email);}
    @Transactional public void markRead(String email,long id){InAppNotification n=notifications.findByIdAndEmployee_WorkEmailIgnoreCase(id,email).orElseThrow(()->new ResourceNotFoundException("Notification",id));n.markRead();}
    @Transactional public int markAllRead(String email){return notifications.markAllRead(email,java.time.Instant.now());}
    @Transactional public int announce(Announcement announcement){
        int created=0; int page=0; Page<Employee> batch;
        do { Pageable pageable=PageRequest.of(page++,500,Sort.by("id"));
            batch=announcement.getDepartment()==null?employees.findAllByStatus(EmployeeStatus.ACTIVE,pageable):employees.findAllByStatusAndDepartment_Id(EmployeeStatus.ACTIVE,announcement.getDepartment().getId(),pageable);
            List<InAppNotification> items=batch.getContent().stream().map(e->new InAppNotification(e,announcement,announcement.getTitle(),announcement.getBody(),"ANNOUNCEMENT")).toList();
            notifications.saveAll(items); created+=items.size();
        } while(batch.hasNext());
        return created;
    }
}
