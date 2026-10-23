package com.bank.atm_monolithic.atm;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "atm_terminal")
public class AtmTerminal {

    /* atm terminal status */
    public enum AtmTerminalStatus {
        ACTIVE,
        MAINTENANCE,
        OFFLINE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String terminalCode;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private AtmTerminalStatus status;

    private String ipAddress;

    private String softwareVersion;

    @Column(name = "total_money", nullable = false)
    private Integer totalMoney;

    @Column(name = "cassette_1_count", nullable = false)
    private Integer cassette1Count;

    @Column(name = "cassette_5_count", nullable = false)
    private Integer cassette5Count;

    @Column(name = "cassette_10_count", nullable = false)
    private Integer cassette10Count;

    @Column(name = "cassette_20_count", nullable = false)
    private Integer cassette20Count;

    @Column(name = "cassette_50_count", nullable = false)
    private Integer cassette50Count;

    @Column(name = "cassette_100_count", nullable = false)
    private Integer cassette100Count;

    @Column(name = "cassette_200_count", nullable = false)
    private Integer cassette200Count;

    @Column(name = "cassette_500_count", nullable = false)
    private Integer cassette500Count;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public AtmTerminalStatus getStatus() {
        return status;
    }

    public void setStatus(AtmTerminalStatus status) {
        this.status = status;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    @PreUpdate
    public void recalculateTotal() {
        int c1 = cassette1Count != null ? cassette1Count : 0;
        int c5 = cassette5Count != null ? cassette5Count : 0;
        int c10 = cassette10Count != null ? cassette10Count : 0;
        int c20 = cassette20Count != null ? cassette20Count : 0;
        int c50 = cassette50Count != null ? cassette50Count : 0;
        int c100 = cassette100Count != null ? cassette100Count : 0;
        int c200 = cassette200Count != null ? cassette200Count : 0;
        int c500 = cassette500Count != null ? cassette500Count : 0;

        this.totalMoney = c1 +
                (c5 * 5) +
                (c10 * 10) +
                (c20 * 20) +
                (c50 * 50) +
                (c100 * 100) +
                (c200 * 200) +
                (c500 * 500);

        this.updatedAt = LocalDateTime.now();
    }

    public Integer getTotalMoney() {
        return totalMoney;
    }

    public Integer getCassette1Count() {
        return cassette1Count;
    }

    public void setCassette1Count(Integer cassette1Count) {
        this.cassette1Count = cassette1Count;
    }

    public Integer getCassette5Count() {
        return cassette5Count;
    }

    public void setCassette5Count(Integer cassette5Count) {
        this.cassette5Count = cassette5Count;
    }

    public Integer getCassette10Count() {
        return cassette10Count;
    }

    public void setCassette10Count(Integer cassette10Count) {
        this.cassette10Count = cassette10Count;
    }

    public Integer getCassette20Count() {
        return cassette20Count;
    }

    public void setCassette20Count(Integer cassette20Count) {
        this.cassette20Count = cassette20Count;
    }

    public Integer getCassette50Count() {
        return cassette50Count;
    }

    public void setCassette50Count(Integer cassette50Count) {
        this.cassette50Count = cassette50Count;
    }

    public Integer getCassette100Count() {
        return cassette100Count;
    }

    public void setCassette100Count(Integer cassette100Count) {
        this.cassette100Count = cassette100Count;
    }

    public Integer getCassette200Count() {
        return cassette200Count;
    }

    public void setCassette200Count(Integer cassette200Count) {
        this.cassette200Count = cassette200Count;
    }

    public Integer getCassette500Count() {
        return cassette500Count;
    }

    public void setCassette500Count(Integer cassette500Count) {
        this.cassette500Count = cassette500Count;
    }
}