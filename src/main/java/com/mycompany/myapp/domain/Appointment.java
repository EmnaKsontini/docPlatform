package com.mycompany.myapp.domain;



import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Appointment.
 */
@Entity
@Table(name = "appointment")
@Document(indexName = "appointment")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    private Long id;

    @NotNull
    @Column(name = "date_and_hour", nullable = false, unique = true)
    private ZonedDateTime dateAndHour;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Request request;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDateAndHour() {
        return dateAndHour;
    }

    public Appointment dateAndHour(ZonedDateTime dateAndHour) {
        this.dateAndHour = dateAndHour;
        return this;
    }

    public void setDateAndHour(ZonedDateTime dateAndHour) {
        this.dateAndHour = dateAndHour;
    }

    public Request getRequest() {
        return request;
    }

    public Appointment request(Request request) {
        this.request = request;
        return this;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Appointment appointment = (Appointment) o;
        if (appointment.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), appointment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Appointment{" +
            "id=" + getId() +
            ", dateAndHour='" + getDateAndHour() + "'" +
            "}";
    }
}
