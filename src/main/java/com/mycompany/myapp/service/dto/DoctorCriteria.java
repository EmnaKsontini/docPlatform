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
import io.github.jhipster.service.filter.BigDecimalFilter;

/**
 * Criteria class for the Doctor entity. This class is used in DoctorResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /doctors?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class DoctorCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private BigDecimalFilter cin;

    private StringFilter address;

    private StringFilter speciality;

    private StringFilter email;

    private BigDecimalFilter phoneNumber;

    private LongFilter requestsId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public BigDecimalFilter getCin() {
        return cin;
    }

    public void setCin(BigDecimalFilter cin) {
        this.cin = cin;
    }

    public StringFilter getAddress() {
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public StringFilter getSpeciality() {
        return speciality;
    }

    public void setSpeciality(StringFilter speciality) {
        this.speciality = speciality;
    }

    public StringFilter getEmail() {
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public BigDecimalFilter getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(BigDecimalFilter phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LongFilter getRequestsId() {
        return requestsId;
    }

    public void setRequestsId(LongFilter requestsId) {
        this.requestsId = requestsId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DoctorCriteria that = (DoctorCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(cin, that.cin) &&
            Objects.equals(address, that.address) &&
            Objects.equals(speciality, that.speciality) &&
            Objects.equals(email, that.email) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(requestsId, that.requestsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        cin,
        address,
        speciality,
        email,
        phoneNumber,
        requestsId
        );
    }

    @Override
    public String toString() {
        return "DoctorCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (cin != null ? "cin=" + cin + ", " : "") +
                (address != null ? "address=" + address + ", " : "") +
                (speciality != null ? "speciality=" + speciality + ", " : "") +
                (email != null ? "email=" + email + ", " : "") +
                (phoneNumber != null ? "phoneNumber=" + phoneNumber + ", " : "") +
                (requestsId != null ? "requestsId=" + requestsId + ", " : "") +
            "}";
    }

}
