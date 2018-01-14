package smithmicro.apps.awesomeNotes.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import smithmicro.apps.awesomeNotes.model.Location;
import smithmicro.apps.awesomeNotes.model.Note;
import smithmicro.apps.awesomeNotes.model.User;
import smithmicro.apps.awesomeNotes.service.LocationService;
import smithmicro.apps.awesomeNotes.service.NoteService;
import smithmicro.apps.awesomeNotes.service.UserService;

@RestController
public class NoteController {

	private static final String NOTES_VIEW = "home";
	private static final String NEW_NOTE_VIEW = "notes/new";
	private static final String SHARE_NOTE_VIEW = "notes/share";
	private static final String NOTE_DETAIL_VIEW = "notes/noteDetails";

	@Autowired
	private NoteService noteService;

	@Autowired
	private UserService userService;

	@Autowired
	private LocationService locationService;


	// Bind byteArray class with ByteArrayMultiPartFileEditor class for images
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor()); 

	}

	// Render my notes view
	@GetMapping("/")
	public ModelAndView home(Principal principal) throws UnsupportedEncodingException{
		// Get logged user
		User loggedUser = getLoggedUser(principal);
		
		return new ModelAndView(NOTES_VIEW, populateHomeView(loggedUser, null, null));
	}

	// Render new note view
	@GetMapping("/notes/new")
	public ModelAndView newNote(ModelAndView modelAndView, Principal principal) throws UnsupportedEncodingException {
		Note note = new Note();
		modelAndView.setViewName(NEW_NOTE_VIEW);
		// Push note object to UI model
		modelAndView.addObject("note", note);

		// Get logged user
		User loggedUser = getLoggedUser(principal);

		//Add avatar image to UI model
		modelAndView.addObject("avatarImage", userService.getAvatarBase64Image(loggedUser));
		return modelAndView;
	}

	@PostMapping("/notes/new")
	public ModelAndView createNote(ModelMap model, @Valid Note note, BindingResult result, Principal principal, @RequestParam("image") MultipartFile image, @RequestParam("jsonStringLocation") String jsonStringLocation, RedirectAttributes redir) throws IOException {
		if (result.hasErrors()) {
			return new ModelAndView(NEW_NOTE_VIEW, model);
		} else {
			// get location if was choosed
			if(!jsonStringLocation.isEmpty()){
				Location noteLocation = locationService.jsonStringToLocation(jsonStringLocation);
				note.setLocation(noteLocation);
			}

			//get logged user
			User createdByUser = getLoggedUser(principal);

			// set note created by logged user
			note.setCreatedBy(createdByUser);

			// get image if was choosed
			if (!image.isEmpty()){
				note.setImage(image.getBytes());
			}else{
				//force note image to be null
				note.setImage(null);
			}

			// get all user notes
			List<Note> notes = createdByUser.getNotes();
			// add this note to all user notes
			notes.add(note);

			// set/update user notes
			createdByUser.setNotes(notes);

			// create note.
			noteService.saveNote(note);
			return new ModelAndView("redirect:/", populateHomeView(createdByUser, "Note " +note.getTitle()+ " was created successfully", redir));
		}
	}


	@GetMapping("/notes/{noteId}/edit")
	public ModelAndView editNote(@PathVariable("noteId") int noteId, Principal principal) throws UnsupportedEncodingException {

		ModelAndView modelAndView = new ModelAndView();
		Note note = noteService.findNoteById(noteId);

		// add note list to the UI model
		modelAndView.addObject("note", note);

		// get logged user
		User loggedUser = getLoggedUser(principal);

		// add loggedUserId to UI
		modelAndView.addObject("loggedUserId", loggedUser.getId());

		//get avatar image and push to UI model
		modelAndView.addObject("avatarImage", userService.getAvatarBase64Image(loggedUser));

		//get note image and push to UI model
		modelAndView.addObject("noteImage", noteService.getNoteBase64Image(note));

		modelAndView.setViewName(NOTE_DETAIL_VIEW);
		return modelAndView;
	}


	@PostMapping("/notes/{noteId}/edit")
	public ModelAndView saveNote(ModelMap model, @ModelAttribute("note") @Valid Note note, BindingResult bindingResult, Principal principal, @PathVariable("noteId") int noteId, RedirectAttributes redir) throws UnsupportedEncodingException{
		if (bindingResult.hasErrors()) {
			return new ModelAndView(NOTE_DETAIL_VIEW, model);
		} else {
			// Get note by id
			Note editedNote = noteService.findNoteById(noteId);
			
			// Get logged user
			User loggedUser = getLoggedUser(principal);
			
			//Set note updatedBy, note title and note content
			editedNote.setUpdatedBy(loggedUser);
			editedNote.setTitle(note.getTitle());
			editedNote.setContent(note.getContent());
			
			//update note.
			noteService.saveNote(editedNote);

			return new ModelAndView("redirect:/", populateHomeView(loggedUser, "Note " +note.getTitle()+ " was saved successfully", redir));
		}

	}



	@GetMapping("/notes/{noteId}/share")
	public ModelAndView initShareNote(ModelMap model, @PathVariable("noteId") int noteId, Principal principal) throws UnsupportedEncodingException {

		// get logged user
		User loggedUser = getLoggedUser(principal);

		// push noteId attribute to UI model
		model.addAttribute("noteId", noteId);

		// get all users
		List<User>	allUsers = userService.findAllUsers();

		//get note to share
		Note note = noteService.findNoteById(noteId);

		List<User> filteredUsers = allUsers
				.stream()
				//exclude users that already have this note.
				.filter(p -> p.getNotes().contains(note) == false)
				// exclude logged user - logged user should not share note to himself
				.filter(p -> p.getId() != loggedUser.getId())
				.collect(Collectors.toCollection(ArrayList::new));

		// push filtered users to UI model
		model.addAttribute("users", filteredUsers);
		// push note to share to UI model
		model.addAttribute("note", note);
		//get avatar image
		model.addAttribute("avatarImage", userService.getAvatarBase64Image(loggedUser));

		return new ModelAndView(SHARE_NOTE_VIEW, model);
	}


	@PostMapping("/notes/{noteId}/share")
	public ModelAndView shareNote(@RequestParam(value = "userIds") ArrayList<Long> users, @PathVariable("noteId") int noteId, Principal principal, HttpServletRequest request, RedirectAttributes redir) throws UnsupportedEncodingException {

		//get logged user
		User loggedUser = getLoggedUser(principal);

		//get this note
		Note sharedNote = noteService.findNoteById(noteId);

		//associate this note with 'share with' selected users
		noteService.shareNoteWithUsers(users, sharedNote);

		//add user-note relationship
		noteService.saveNote(sharedNote);

		String noteUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort() + "/notes/"+ sharedNote.getNoteId() +"/edit";

		//send email notification to 'share with' selected users
		noteService.notifySharedNote(users, loggedUser, sharedNote, noteUrl);

		return new ModelAndView(NOTES_VIEW, populateHomeView(loggedUser, "Note " +sharedNote.getTitle()+ " was shared with selected users", redir));
	}



	@PostMapping("/notes/{noteId}/unfollow")
	public ModelAndView unfollowNote(@PathVariable("noteId") String noteId, Principal principal, RedirectAttributes redir) throws UnsupportedEncodingException {
		
		//get logged user
		User loggedUser = getLoggedUser(principal);

		//get this note
		Note unfollowedNote = noteService.findNoteById(Integer.parseInt(noteId));

		//remove this note from logged user notes
		noteService.unfollowNote(loggedUser, unfollowedNote);

		//save user-note relationship
		noteService.saveNote(unfollowedNote);

		return new ModelAndView("redirect:/", populateHomeView(loggedUser, "You unfollowed "+ unfollowedNote.getCreatedBy().getName() + " " + unfollowedNote.getCreatedBy().getLastName() + "'s note " +unfollowedNote.getTitle(), redir));
	}


	@PostMapping("/notes/{noteId}/delete")
	public ModelAndView deleteNote(Principal principal, @PathVariable("noteId") int noteId, RedirectAttributes redir) throws UnsupportedEncodingException {

		User loggedUser = getLoggedUser(principal);

		Note noteToDelete = noteService.findNoteById(noteId);

		//UI is validating this and not showing the delete button, but just double checking.
		if(!noteToDelete.getCreatedBy().equals(loggedUser)){
			return new ModelAndView(NOTES_VIEW, populateHomeView(loggedUser, "You can't delete a note which wasn't created by you", redir));
		}

		//get all users who share this note
		List<User> noteUsers = noteToDelete.getUsers();

		//remove this note association with all users who share this note
		noteService.deleteNoteFromUsers(noteUsers, noteToDelete);

		//delete note from DB
		noteService.deleteNoteById(noteToDelete);

		//collect all users the logged user shared this note (all except logged user)
		noteUsers.remove(loggedUser);

		//notify note deletion to users that you shared this note.
		noteService.notifyDeletedNote(noteUsers, loggedUser, noteToDelete);

		return new ModelAndView("redirect:/", populateHomeView(loggedUser, "Note " +noteToDelete.getTitle()+ " was deleted successfully", redir));
	}



	private ModelMap populateHomeView(User loggedUser, String message, RedirectAttributes redir) throws UnsupportedEncodingException{
		ModelMap model = new ModelMap();
		model.addAttribute("userId", loggedUser.getId());
		model.addAttribute("userName", loggedUser.getName() + " " + loggedUser.getLastName());
		// get notes from repository
		
		List<Note> notes = noteService.getAllByUserId(loggedUser.getId());
		
		// add note list to the UI model
		model.addAttribute("notes", notes);

		//encode avatar image to base64
		String avatarBase64 = Base64.encodeBase64String(loggedUser.getAvatar());

		//add encoded avatar image to model
		model.addAttribute("avatarImage", avatarBase64);

		// In order to set a model attribute on a redirect, we must use
		// RedirectAttributes
		if(null != message){
			redir.addFlashAttribute("successMessage", message);
		}

		return model;
	}

	private User getLoggedUser(Principal principal){
		//Initialize user
		User loggedUser = null;
		//Get logged user
		loggedUser = userService.findUserByEmail(principal.getName());

		if(null == loggedUser){
			//if getUserByEmail returns null, get by userId (facebook signed in users)
			loggedUser = userService.findById(Long.parseLong(principal.getName()));
		}

		return loggedUser;
	}

}
