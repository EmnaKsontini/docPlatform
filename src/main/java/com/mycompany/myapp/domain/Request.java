package com.mycompany.myapp.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Request.
 */
@Entity
@Table(name = "request")
@Document(indexName = "request")
public class Request implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    private Long id;

    @NotNull
    @Column(name = "date_1", nullable = false)
    private LocalDate date1;

    @Column(name = "date_2")
    private LocalDate date2;

    @Column(name = "date_3")
    private LocalDate date3;

    @Column(name = "confirmation")
    private Boolean confirmation;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("requests")
    private Patient patient;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("requests")
    private Doctor doctor;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Appointment appointment;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate1() {
        return date1;
    }

    public Request date1(LocalDate date1) {
        this.date1 = date1;
        return this;
    }

    public void setDate1(LocalDate date1) {
        this.date1 = date1;
    }

    public LocalDate getDate2() {
        return date2;
    }

    public Request date2(LocalDate date2) {
        this.date2 = date2;
        return this;
    }

    public void setDate2(LocalDate date2) {
        this.date2 = date2;
    }

    public LocalDate getDate3() {
        return date3;
    }

    public Request date3(LocalDate date3) {
        this.date3 = date3;
        return this;
    }

    public void setDate3(LocalDate date3) {
        this.date3 = date3;
    }

    public Boolean isConfirmation() {
        return confirmation;
    }

    public Request confirmation(Boolean confirmation) {
        this.confirmation = confirmation;
        return this;
    }

    public void setConfirmation(Boolean confirmation) {
        this.confirmation = confirmation;
    }

    public Patient getPatient() {
        return patient;
    }

    public Request patient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Request doctor(Doctor doctor) {
        this.doctor = doctor;
        return this;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public Request appointment(Appointment appointment) {
        this.appointment = appointment;
        return this;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
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
        Request request = (Request) o;
        if (request.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), request.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Request{" +
            "id=" + getId() +
            ", date1='" + getDate1() + "'" +
            ", date2='" + getDate2() + "'" +
            ", date3='" + getDate3() + "'" +
            ", confirmation='" + isConfirmation() + "'" +
            "}";
    }
}
