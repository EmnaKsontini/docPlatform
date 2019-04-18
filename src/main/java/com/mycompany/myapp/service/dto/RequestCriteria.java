package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the Request entity. This class is used in RequestResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /requests?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class RequestCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter date1;

    private LocalDateFilter date2;

    private LocalDateFilter date3;

    private BooleanFilter confirmation;

    private LongFilter patientId;

    private LongFilter doctorId;

    private LongFilter appointmentId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalDateFilter getDate1() {
        return date1;
    }

    public void setDate1(LocalDateFilter date1) {
        this.date1 = date1;
    }

    public LocalDateFilter getDate2() {
        return date2;
    }

    public void setDate2(LocalDateFilter date2) {
        this.date2 = date2;
    }

    public LocalDateFilter getDate3() {
        return date3;
    }

    public void setDate3(LocalDateFilter date3) {
        this.date3 = date3;
    }

    public BooleanFilter getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(BooleanFilter confirmation) {
        this.confirmation = confirmation;
    }

    public LongFilter getPatientId() {
        return patientId;
    }

    public void setPatientId(LongFilter patientId) {
        this.patientId = patientId;
    }

    public LongFilter getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(LongFilter doctorId) {
        this.doctorId = doctorId;
    }

    public LongFilter getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(LongFilter appointmentId) {
        this.appointmentId = appointmentId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RequestCriteria that = (RequestCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(date1, that.date1) &&
            Objects.equals(date2, that.date2) &&
            Objects.equals(date3, that.date3) &&
            Objects.equals(confirmation, that.confirmation) &&
            Objects.equals(patientId, that.patientId) &&
            Objects.equals(doctorId, that.doctorId) &&
            Objects.equals(appointmentId, that.appointmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        date1,
        date2,
        date3,
        confirmation,
        patientId,
        doctorId,
        appointmentId
        );
    }

    @Override
    public String toString() {
        return "RequestCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (date1 != null ? "date1=" + date1 + ", " : "") +
                (date2 != null ? "date2=" + date2 + ", " : "") +
                (date3 != null ? "date3=" + date3 + ", " : "") +
                (confirmation != null ? "confirmation=" + confirmation + ", " : "") +
                (patientId != null ? "patientId=" + patientId + ", " : "") +
                (doctorId != null ? "doctorId=" + doctorId + ", " : "") +
                (appointmentId != null ? "appointmentId=" + appointmentId + ", " : "") +
            "}";
    }

}
