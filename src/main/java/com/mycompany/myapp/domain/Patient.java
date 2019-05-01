package com.mycompany.myapp.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Patient.
 */
@Entity
@Table(name = "patient")
@Document(indexName = "patient")
public class Patient implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "phone_number", nullable = false)
    private Long phoneNumber;

    @NotNull
    @Column(name = "cin", nullable = false, unique = true)
    private Long cin;

    @Pattern(regexp = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")
    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "patient")
    private Set<Request> requests = new HashSet<>();
    @ManyToMany
    @JoinTable(name = "patient_doctor",
               joinColumns = @JoinColumn(name = "patient_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "doctor_id", referencedColumnName = "id"))
    private Set<Doctor> doctors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Patient name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public Patient phoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getCin() {
        return cin;
    }

    public Patient cin(Long cin) {
        this.cin = cin;
        return this;
    }

    public void setCin(Long cin) {
        this.cin = cin;
    }

    public String getEmail() {
        return email;
    }

    public Patient email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Request> getRequests() {
        return requests;
    }

    public Patient requests(Set<Request> requests) {
        this.requests = requests;
        return this;
    }

    public Patient addRequests(Request request) {
        this.requests.add(request);
        request.setPatient(this);
        return this;
    }

    public Patient removeRequests(Request request) {
        this.requests.remove(request);
        request.setPatient(null);
        return this;
    }

    public void setRequests(Set<Request> requests) {
        this.requests = requests;
    }

    public Set<Doctor> getDoctors() {
        return doctors;
    }

    public Patient doctors(Set<Doctor> doctors) {
        this.doctors = doctors;
        return this;
    }

    public Patient addDoctor(Doctor doctor) {
        this.doctors.add(doctor);
        doctor.getPatients().add(this);
        return this;
    }

    public Patient removeDoctor(Doctor doctor) {
        this.doctors.remove(doctor);
        doctor.getPatients().remove(this);
        return this;
    }

    public void setDoctors(Set<Doctor> doctors) {
        this.doctors = doctors;
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
        Patient patient = (Patient) o;
        if (patient.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), patient.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Patient{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", phoneNumber=" + getPhoneNumber() +
            ", cin=" + getCin() +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
