package antifraud.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.*;


@JsonPropertyOrder({
        "id",
        "number"
})
@Entity
@Table(name = "stolen_card")
public class StolenCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String number;

    public StolenCard(String number) {
        this.number = number;
    }

    public StolenCard() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
