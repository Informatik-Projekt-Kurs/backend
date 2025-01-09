package com.MeetMate.appointment;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, Long> {

    Optional<Appointment> findAppointmentById(long id);

    ArrayList<Appointment> findAppointmentsByCompanyId(long clientId);

    ArrayList<Appointment> findAppointmentsByClientId(long clientId);
}
