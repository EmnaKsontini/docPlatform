package com.mycompany.myapp.service.dto;
import java.time.LocalDate;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Request entity.
 */
public class RequestDTO implements Serializable {

    private Long id;

    @NotNull
    private LocalDate date1;

    private LocalDate date2;

    private LocalDate date3;

    private Boolean confirmation;


    private Long patientId;

    private String patientRequests;

    private Long doctorId;

    private String doctorRequests;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate1() {
        return date1;
    }

    public void setDate1(LocalDate date1) {
        this.date1 = date1;
    }

    public LocalDate getDate2() {
        return date2;
    }

    public void setDate2(LocalDate date2) {
        this.date2 = date2;
    }

    public LocalDate getDate3() {
        return date3;
    }

    public void setDate3(LocalDate date3) {
        this.date3 = date3;
    }

    public Boolean isConfirmation() {
        return confirmation;
    }

    public void setConfirmation(Boolean confirmation) {
        this.confirmation = confirmation;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientRequests() {
        return patientRequests;
    }

    public void setPatientRequests(String patientRequests) {
        this.patientRequests = patientRequests;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorRequests() {
        return doctorRequests;
    }

    public void setDoctorRequests(String doctorRequests) {
        this.doctorRequests = doctorRequests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RequestDTO requestDTO = (RequestDTO) o;
        if (requestDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), requestDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RequestDTO{" +
            "id=" + getId() +
            ", date1='" + getDate1() + "'" +
            ", date2='" + getDate2() + "'" +
            ", date3='" + getDate3() + "'" +
            ", confirmation='" + isConfirmation() + "'" +
            ", patient=" + getPatientId() +
            ", patient='" + getPatientRequests() + "'" +
            ", doctor=" + getDoctorId() +
            ", doctor='" + getDoctorRequests() + "'" +
            "}";
    }
}
