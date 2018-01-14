package smithmicro.apps.awesomeNotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import smithmicro.apps.awesomeNotes.model.Location;

@Repository("locationRepository")
public interface LocationRepository extends JpaRepository<Location, Integer>{

}

