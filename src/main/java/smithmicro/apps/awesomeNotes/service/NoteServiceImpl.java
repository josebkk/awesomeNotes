package smithmicro.apps.awesomeNotes.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smithmicro.apps.awesomeNotes.model.Note;
import smithmicro.apps.awesomeNotes.model.User;
import smithmicro.apps.awesomeNotes.repository.NoteRepository;
import smithmicro.apps.awesomeNotes.repository.UserRepository;


@Transactional
@Service("noteService")
public class NoteServiceImpl implements NoteService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private EmailService emailService;


	@Override
	public Note findNoteById(int noteId) {
		return noteRepository.getOne(noteId);
	}

	@Override
	public void saveNote(Note note) {
		noteRepository.save(note);
	}


	@Override
	public void deleteNoteById(Note note) {
		noteRepository.delete(note);
	}

	@Override
	public List<Note> getAllByUserId(Long userId) {
		User user = userRepository.findOne(userId);
		return user.getNotes();
	}


	@Override
	public void shareNoteWithUsers(ArrayList<Long> userIds, Note sharedNote) {
		for(Long sharingUserId: userIds){
			User sharingUser = userRepository.getOne(sharingUserId);
			List<Note> notes = sharingUser.getNotes();
			notes.add(sharedNote);
			sharingUser.setNotes(notes);
		}
	}


	@Override
	public void unfollowNote(User user, Note sharedNote) {
		List<Note> notes = user.getNotes();
		notes.remove(sharedNote);
		user.setNotes(notes);
	}





	@Override
	public void notifySharedNote(ArrayList<Long> userIds, User user, Note noteToShare, String noteUrl) {
		for(Long notifyUserId: userIds){
			User notifyUser = userRepository.getOne(notifyUserId);
			emailService.sendSimpleMessage(notifyUser.getEmail(), "AwesomeNotes App - " + user.getName() + " " + user.getLastName() + " shared a note with you", "<b>Title</b>: "+ noteToShare.getTitle() +"<br/> <b>Note</b>:" + noteToShare.getContent()+"<br/><br/> Click <a href="+ noteUrl +">here</a> to access the note details");
		}

	}

	@Override
	public void deleteNoteFromUsers(List<User> noteUsers, Note noteToDelete) {
		for(User noteUser: noteUsers){
			List<Note> notes = noteUser.getNotes();
			notes.remove(noteToDelete);
			noteUser.setNotes(notes);
		}
	}

	@Override
	public void notifyDeletedNote(List<User> usersToNotify, User user, Note deletedNote) {
		for(User notifyUser: usersToNotify){
			emailService.sendSimpleMessage(notifyUser.getEmail(), "AwesomeNotes App - " + user.getName() + " " + user.getLastName() + " deleted a note which was shared with you", "<b>Title</b>: "+ deletedNote.getTitle() +"<br/> <b>Note</b>:" + deletedNote.getContent());
		}

	}

	@Override
	public String getNoteBase64Image(Note note) {
		return Base64.encodeBase64String(note.getImage());
	}


}
