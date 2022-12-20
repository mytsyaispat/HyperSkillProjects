package antifraud.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.*;

@Entity
@Table(name = "suspicion_ip")
@JsonPropertyOrder({
        "id",
        "ip"
})
public class SuspicionIp {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String ip;

    public SuspicionIp(String ip) {
        this.ip = ip;
    }

    public SuspicionIp() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
