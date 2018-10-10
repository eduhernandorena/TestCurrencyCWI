package br.com.cwi.eduardo.bean;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Currency {
    private Long cod;
    private String name;
    private String type;
    private BigDecimal parity;
    private LocalDate date;
    
    public Currency() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Long getCod() {
        return cod;
    }
    
    public void setCod(Long cod) {
        this.cod = cod;
    }
    
    public BigDecimal getParity() {
        return parity;
    }
    
    public void setParity(BigDecimal parity) {
        this.parity = parity;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
}
