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
import io.github.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the Appointment entity. This class is used in AppointmentResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /appointments?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class AppointmentCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter dateAndHour;

    private LongFilter requestId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public ZonedDateTimeFilter getDateAndHour() {
        return dateAndHour;
    }

    public void setDateAndHour(ZonedDateTimeFilter dateAndHour) {
        this.dateAndHour = dateAndHour;
    }

    public LongFilter getRequestId() {
        return requestId;
    }

    public void setRequestId(LongFilter requestId) {
        this.requestId = requestId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AppointmentCriteria that = (AppointmentCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(dateAndHour, that.dateAndHour) &&
            Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        dateAndHour,
        requestId
        );
    }

    @Override
    public String toString() {
        return "AppointmentCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (dateAndHour != null ? "dateAndHour=" + dateAndHour + ", " : "") +
                (requestId != null ? "requestId=" + requestId + ", " : "") +
            "}";
    }

}
