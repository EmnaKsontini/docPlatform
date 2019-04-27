package com.mycompany.myapp.service.dto;
import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Appointment entity.
 */
public class AppointmentDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime dateAndHour;


    private Long requestId;

    private String requestAppointement;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDateAndHour() {
        return dateAndHour;
    }

    public void setDateAndHour(ZonedDateTime dateAndHour) {
        this.dateAndHour = dateAndHour;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getRequestAppointement() {
        return requestAppointement;
    }

    public void setRequestAppointement(String requestAppointement) {
        this.requestAppointement = requestAppointement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AppointmentDTO appointmentDTO = (AppointmentDTO) o;
        if (appointmentDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), appointmentDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AppointmentDTO{" +
            "id=" + getId() +
            ", dateAndHour='" + getDateAndHour() + "'" +
            ", request=" + getRequestId() +
            ", request='" + getRequestAppointement() + "'" +
            "}";
    }
}
