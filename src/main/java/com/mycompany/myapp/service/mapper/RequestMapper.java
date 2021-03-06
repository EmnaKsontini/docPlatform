package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.service.dto.RequestDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Request and its DTO RequestDTO.
 */
@Mapper(componentModel = "spring", uses = {PatientMapper.class, DoctorMapper.class})
public interface RequestMapper extends EntityMapper<RequestDTO, Request> {

    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "doctor.id", target = "doctorId")
    RequestDTO toDto(Request request);

    @Mapping(source = "patientId", target = "patient")
    @Mapping(source = "doctorId", target = "doctor")
    @Mapping(target = "appointment", ignore = true)
    Request toEntity(RequestDTO requestDTO);

    default Request fromId(Long id) {
        if (id == null) {
            return null;
        }
        Request request = new Request();
        request.setId(id);
        return request;
    }
}
