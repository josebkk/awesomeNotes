package smithmicro.apps.awesomeNotes.service;


import java.util.ArrayList;
import java.util.List;

import smithmicro.apps.awesomeNotes.model.Note;
import smithmicro.apps.awesomeNotes.model.User;

public interface NoteService {

	public Note findNoteById(int noteId);
	public void saveNote(Note note);
	public void deleteNoteById(Note note);
	public List<Note> getAllByUserId(Long userId);
	public void shareNoteWithUsers(ArrayList<Long> userIds, Note noteToShare);
	public void unfollowNote(User user, Note sharedNote);
	public void deleteNoteFromUsers(List<User> noteUsers, Note noteToDelete);
	public void notifySharedNote(ArrayList<Long> userIds, User user, Note sharedNote, String noteUrl);
	public void notifyDeletedNote(List<User> usersToNotify, User user, Note deletedNote);
	public String getNoteBase64Image(Note note);

}
