package com.hrgenius.recruitment.repository;
import com.hrgenius.recruitment.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface OfferRepository extends JpaRepository<Offer, Long> {
    Optional<Offer> findByApplication_Id(Long applicationId);
    java.util.List<Offer> findAllByApplication_IdOrderByCreatedAtDesc(Long applicationId);
}
