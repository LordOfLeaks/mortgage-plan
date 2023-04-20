package com.olafparfienczyk.mortgageplan.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "total_loan_cents", nullable = false)
    private long totalLoanCents;

    @Column(name = "interest_bp", nullable = false)
    private int interestBasePoints;

    @Column(name = "loan_duration_years", nullable = false)
    private int years;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTotalLoanCents() {
        return totalLoanCents;
    }

    public void setTotalLoanCents(long totalLoanCents) {
        this.totalLoanCents = totalLoanCents;
    }

    public int getInterestBasePoints() {
        return interestBasePoints;
    }

    public void setInterestBasePoints(int interestBasePoints) {
        this.interestBasePoints = interestBasePoints;
    }

    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }
}