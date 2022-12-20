package antifraud.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@ToString
@Entity
@Table(name = "transaction")
@JsonPropertyOrder({
        "amount",
        "ip",
        "number",
        "region",
        "date"
})
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    @Column(name = "transaction_id")
    private Long transactionId;
    @Min(value = 1)
    @NotNull
    private Long amount;
    @NotBlank
    private String ip;
    @NotBlank
    private String number;
    @NotBlank
    private String region;
    @DateTimeFormat
    private LocalDateTime date;
    @JsonIgnore
    private String result;
    @JsonIgnore
    private String feedback = "";

    public TransactionEntity(Long transactionId, Long amount, String ip, String number, String region, LocalDateTime date, String result, String feedback) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.ip = ip;
        this.number = number;
        this.region = region;
        this.date = date;
        this.result = result;
        this.feedback = feedback;
    }

    public TransactionEntity() {}

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDate() {
        return date.toString().replace(' ', 'T');
    }
    @JsonIgnore
    public LocalDateTime getDateLikeLocalDateTime() {
        return date;
    }

    public void setDate(String date) {
        this.date = LocalDateTime.parse(date);
    }
}
