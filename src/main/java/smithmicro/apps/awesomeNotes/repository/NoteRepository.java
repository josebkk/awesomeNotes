package smithmicro.apps.awesomeNotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import smithmicro.apps.awesomeNotes.model.Note;

@Repository("noteRepository")
public interface NoteRepository extends JpaRepository<Note, Integer>{

}

