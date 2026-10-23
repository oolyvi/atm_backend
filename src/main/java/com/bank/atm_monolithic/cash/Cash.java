package com.bank.atm_monolithic.cash;

import com.bank.atm_monolithic.atm.AtmTerminal;
import jakarta.persistence.*;

@Entity
@Table(name = "cash")
public class Cash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atm_id", nullable = false)
    private AtmTerminal atmTerminal;

    @Column(nullable = false)
    private Integer denomination; // e.g., 1, 5, 10, 20, 50, 100, 200, 500

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "low_threshold", nullable = false)
    private Integer lowThreshold = 10;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtmTerminal getAtmTerminal() {
        return atmTerminal;
    }

    public void setAtmTerminal(AtmTerminal atmTerminal) {
        this.atmTerminal = atmTerminal;
    }

    public Integer getDenomination() {
        return denomination;
    }

    public void setDenomination(Integer denomination) {
        this.denomination = denomination;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getLowThreshold() {
        return lowThreshold;
    }

    public void setLowThreshold(Integer lowThreshold) {
        this.lowThreshold = lowThreshold;
    }
}