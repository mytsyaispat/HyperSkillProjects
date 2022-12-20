package antifraud.entity.response;

import antifraud.entity.TransactionEntity;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder({
        "transactionId",
        "amount",
        "ip",
        "number",
        "region",
        "date",
        "result",
        "feedback"
})
public class TransactionResponse {

    private long transactionId;
    private long amount;
    private String ip;
    private String number;
    private String region;
    private LocalDateTime date;
    private String result;
    private String feedback;

    public TransactionResponse(TransactionEntity transaction) {
        this.transactionId = transaction.getTransactionId();
        this.amount = transaction.getAmount();
        this.ip = transaction.getIp();
        this.number = transaction.getNumber();
        this.region = transaction.getRegion();
        this.date = transaction.getDateLikeLocalDateTime();
        this.result = transaction.getResult();
        this.feedback = transaction.getFeedback();
    }

    public TransactionResponse(long transactionId, long amount, String ip, String number, String region, LocalDateTime date, String result, String feedback) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.ip = ip;
        this.number = number;
        this.region = region;
        this.date = date;
        this.result = result;
        this.feedback = feedback;
    }

    public TransactionResponse() {}

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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
}
