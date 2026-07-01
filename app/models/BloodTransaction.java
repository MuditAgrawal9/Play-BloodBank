package models;

import io.ebean.Model;
import io.ebean.Finder;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "blood_transactions")
public class BloodTransaction extends Model {

  @Id private Long id;

  @ManyToOne private User user;

  private String bloodGroup;

  private Integer units;

  // INCOMING / OUTGOING
  private String transactionType;

  // PENDING / APPROVED / REJECTED / COMPLETED
  private String status;

  private LocalDateTime transactionDate;

  public static final Finder<Long, BloodTransaction> find = new Finder<>(BloodTransaction.class);

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public String getBloodGroup() {
    return bloodGroup;
  }

  public Integer getUnits() {
    return units;
  }

  public String getTransactionType() {
    return transactionType;
  }

  public String getStatus() {
    return status;
  }

  public LocalDateTime getTransactionDate() {
    return transactionDate;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void setBloodGroup(String bloodGroup) {
    this.bloodGroup = bloodGroup;
  }

  public void setUnits(Integer units) {
    this.units = units;
  }

  public void setTransactionType(String transactionType) {
    this.transactionType = transactionType;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setTransactionDate(LocalDateTime transactionDate) {
    this.transactionDate = transactionDate;
  }
}
