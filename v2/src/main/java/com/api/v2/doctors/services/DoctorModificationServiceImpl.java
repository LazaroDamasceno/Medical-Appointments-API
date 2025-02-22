package com.api.v2.doctors.services;

import com.api.v2.common.MLN;
import com.api.v2.doctors.controller.DoctorController;
import com.api.v2.doctors.domain.exposed.Doctor;
import com.api.v2.doctors.domain.DoctorRepository;
import com.api.v2.doctors.exceptions.ImmutableDoctorStatusException;
import com.api.v2.doctors.resources.DoctorResponseResource;
import com.api.v2.doctors.utils.DoctorFinderUtil;
import com.api.v2.doctors.utils.DoctorResponseMapper;
import com.api.v2.people.domain.exposed.Person;
import com.api.v2.people.dtos.PersonModificationDto;
import com.api.v2.people.services.interfaces.PersonModificationService;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class DoctorModificationServiceImpl implements DoctorModificationService {

    private final DoctorFinderUtil doctorFinderUtil;
    private final DoctorRepository doctorRepository;
    private final PersonModificationService personModificationService;

    public DoctorModificationServiceImpl(DoctorFinderUtil doctorFinderUtil,
                                         DoctorRepository doctorRepository,
                                         PersonModificationService personModificationService
    ) {
        this.doctorFinderUtil = doctorFinderUtil;
        this.doctorRepository = doctorRepository;
        this.personModificationService = personModificationService;
    }

    @Override
    public DoctorResponseResource modify(@MLN String medicalLicenseNumber, PersonModificationDto modificationDto) {
        Doctor doctor = doctorFinderUtil.findByMedicalLicenseNumber(medicalLicenseNumber);
        onTerminatedDoctor(doctor);
        Person modifiedPerson = personModificationService.modify(doctor.getPerson(), modificationDto);
        doctor.setPerson(modifiedPerson);
        Doctor modifiedDoctor = doctorRepository.save(doctor);
        return DoctorResponseMapper
                .mapToResource(modifiedDoctor)
                .add(
                    linkTo(
                            methodOn(DoctorController.class).findByMedicalLicenseNumber(medicalLicenseNumber)
                    ).withRel("find_doctor_by_medical_license_number"),
                    linkTo(
                            methodOn(DoctorController.class).terminate(medicalLicenseNumber)
                    ).withRel("terminate_doctor_by_medical_license_number")
                );
    }

    private void onTerminatedDoctor(Doctor doctor) {
        if (doctor.getTerminatedAt() != null) {
            String message = """
                    Doctor whose medical license number is %s cannot be modified, because they're terminated.
            """.formatted(doctor.getMedicalLicenseNumber());
            throw new ImmutableDoctorStatusException(message);
        }
    }
}
