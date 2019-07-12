package com.syntax.webapp.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.syntax.webapp.model.User;
import com.syntax.webapp.model.UserProfile;
import com.syntax.webapp.model.Vendor;
import com.syntax.webapp.service.UserProfileService;
import com.syntax.webapp.service.UserService;
import com.syntax.webapp.service.VendorService;



@Controller
@RequestMapping("/")
@SessionAttributes("roles")
public class AppController {

	@Autowired
	UserService userService;
	
	@Autowired
	VendorService vendorService;
	
	@Autowired
	UserProfileService userProfileService;
	
	@Autowired
	MessageSource messageSource;

	@Autowired
	PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;
	
	@Autowired
	AuthenticationTrustResolver authenticationTrustResolver;
	
	
	/**
	 * This method will list all existing users.
	 */
	@RequestMapping(value = { "/", "/list" }, method = RequestMethod.GET)
	public String listUsers(ModelMap model) {

		List<User> users = userService.findAllUsers();
		model.addAttribute("users", users);
		model.addAttribute("loggedinuser", getPrincipal());
		return "userslist";
	}

	/**
	 * This method will provide the medium to add a new user.
	 */
	@RequestMapping(value = { "/newuser" }, method = RequestMethod.GET)
	public String newUser(ModelMap model) {
		User user = new User();
		List<UserProfile> roles = userProfileService.findAll();
		model.addAttribute("user", user);
		model.addAttribute("edit", false);
		model.addAttribute("loggedinuser", getPrincipal());
		model.addAttribute("roles", roles);
		return "registration";
	}

	/**
	 * This method will be called on form submission, handling POST request for
	 * saving user in database. It also validates the user input
	 */
	@RequestMapping(value = { "/newuser" }, method = RequestMethod.POST)
	public String saveUser(@Valid User user, BindingResult result,
			ModelMap model) {

		if (result.hasErrors()) {
			return "registration";
		}

		/*
		 * Preferred way to achieve uniqueness of field [sso] should be implementing custom @Unique annotation 
		 * and applying it on field [sso] of Model class [User].
		 * 
		 * Below mentioned peace of code [if block] is to demonstrate that you can fill custom errors outside the validation
		 * framework as well while still using internationalized messages.
		 * 
		 */
		if(!userService.isUserSSOUnique(user.getId(), user.getSsoId())){
			FieldError ssoError =new FieldError("user","ssoId",messageSource.getMessage("non.unique.ssoId", new String[]{user.getSsoId()}, Locale.getDefault()));
		    result.addError(ssoError);
			return "registration";
		}
		
		userService.saveUser(user);

		model.addAttribute("success", "User " + user.getFirstName() + " "+ user.getLastName() + " registered successfully");
		model.addAttribute("loggedinuser", getPrincipal());
		//return "success";
		return "registrationsuccess";
	}


	/**
	 * This method will provide the medium to update an existing user.
	 */
	@RequestMapping(value = { "/edit-user-{ssoId}" }, method = RequestMethod.GET)
	public String editUser(@PathVariable String ssoId, ModelMap model) {
		User user = userService.findBySSO(ssoId);
		model.addAttribute("user", user);
		model.addAttribute("edit", true);
		model.addAttribute("loggedinuser", getPrincipal());
		return "registration";
	}
	
	/**
	 * This method will be called on form submission, handling POST request for
	 * updating user in database. It also validates the user input
	 */
	@RequestMapping(value = { "/edit-user-{ssoId}" }, method = RequestMethod.POST)
	public String updateUser(@Valid User user, BindingResult result,
			ModelMap model, @PathVariable String ssoId) {

		if (result.hasErrors()) {
			return "registration";
		}

		/*//Uncomment below 'if block' if you WANT TO ALLOW UPDATING SSO_ID in UI which is a unique key to a User.
		if(!userService.isUserSSOUnique(user.getId(), user.getSsoId())){
			FieldError ssoError =new FieldError("user","ssoId",messageSource.getMessage("non.unique.ssoId", new String[]{user.getSsoId()}, Locale.getDefault()));
		    result.addError(ssoError);
			return "registration";
		}*/


		userService.updateUser(user);

		model.addAttribute("success", "User " + user.getFirstName() + " "+ user.getLastName() + " updated successfully");
		model.addAttribute("loggedinuser", getPrincipal());
		return "registrationsuccess";
	}

	
	/**
	 * This method will delete an user by it's SSOID value.
	 */
	@RequestMapping(value = { "/delete-user-{ssoId}" }, method = RequestMethod.GET)
	public String deleteUser(@PathVariable String ssoId) {
		userService.deleteUserBySSO(ssoId);
		return "redirect:/list";
	}
	

	/**
	 * This method will provide UserProfile list to views
	 */
	@ModelAttribute("roles")
	public List<UserProfile> initializeProfiles() {
		return userProfileService.findAll();
	}
	
	/**
	 * This method handles Access-Denied redirect.
	 */
	@RequestMapping(value = "/Access_Denied", method = RequestMethod.GET)
	public String accessDeniedPage(ModelMap model) {
		model.addAttribute("loggedinuser", getPrincipal());
		return "accessDenied";
	}

	/**
	 * This method handles login GET requests.
	 * If users is already logged-in and tries to goto login page again, will be redirected to list page.
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginPage() {
		if (isCurrentAuthenticationAnonymous()) {
			return "login";
	    } else {
	    	return "redirect:/list";  
	    }
	}

	/**
	 * This method handles logout requests.
	 * Toggle the handlers if you are RememberMe functionality is useless in your app.
	 */
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logoutPage (HttpServletRequest request, HttpServletResponse response){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null){    
			//new SecurityContextLogoutHandler().logout(request, response, auth);
			persistentTokenBasedRememberMeServices.logout(request, response, auth);
			SecurityContextHolder.getContext().setAuthentication(null);
		}
		return "redirect:/login?logout";
	}

	/**
	 * This method returns the principal[user-name] of logged-in user.
	 */
	private String getPrincipal(){
		String userName = null;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails) {
			userName = ((UserDetails)principal).getUsername();
		} else {
			userName = principal.toString();
		}
		return userName;
	}
	
	/**
	 * This method returns true if users is already authenticated [logged-in], else false.
	 */
	private boolean isCurrentAuthenticationAnonymous() {
	    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    return authenticationTrustResolver.isAnonymous(authentication);
	}


//-------------------------------------------------------------------------------------------

	@RequestMapping(value = { "/newvendor" }, method = RequestMethod.GET)
	public String newVendor(ModelMap model) {
		User user = new User();
		Vendor vendor = new Vendor();
		
		List<UserProfile> roles = userProfileService.findAll();
		
		model.addAttribute("loggedinuser", getPrincipal());		
		model.addAttribute("user", user);
		model.addAttribute("vendor", vendor);
		model.addAttribute("edit", false);		
		model.addAttribute("roles", roles);
		
		return "create_vendor";
	}
	

	@RequestMapping(value = { "/newvendor" }, method = RequestMethod.POST)
	public String saveVendor(@Valid Vendor vendor, BindingResult result,ModelMap model) {
		if (result.hasErrors()) {
			return "create_vendor";
		}		
		vendorService.saveVendor(vendor);
		model.addAttribute("success", "Vendor " + vendor.getVendorName() + " "+ vendor.getVendorCode() + " registered successfully");
		model.addAttribute("loggedinuser", getPrincipal());
		return "vendorsuccess";
	}	
	
	@RequestMapping(value = { "/vendors" }, method = RequestMethod.GET)
	public String listVendor(ModelMap model) {

		List<Vendor> vendors = vendorService.findAllVendors();
		model.addAttribute("vendors", vendors);
		model.addAttribute("loggedinuser", getPrincipal());
		return "vendorslist";
	}	
	
	
	@RequestMapping(value = { "/edit-vendor-{code}" }, method = RequestMethod.GET)
	public String editVendor(@PathVariable String code, ModelMap model) {
		Vendor vendor = vendorService.findByCode(code);
		model.addAttribute("vendor", vendor);
		model.addAttribute("edit", true);
		model.addAttribute("loggedinuser", getPrincipal());
		return "create_vendor";
	}	
	
	@RequestMapping(value = { "/edit-vendor-{code}" }, method = RequestMethod.POST)
	public String updateVendor(@Valid Vendor vendor, BindingResult result,ModelMap model, @PathVariable String code) {

		if (result.hasErrors()) {
			return "create_vendor";
		}
		vendorService.updateVendor(vendor);

		model.addAttribute("success", "Vendor " + vendor.getVendorName() + " "+ vendor.getVendorCode() + " updated successfully");
		model.addAttribute("loggedinuser", getPrincipal());
		return "vendorsuccess";
	}

	@RequestMapping(value = { "/delete-vendor-{code}" }, method = RequestMethod.GET)
	public String deleteVendor(@PathVariable String code) {
		vendorService.deleteVendorByCode(code);
		return "redirect:/vendors";
	}
	
	
	
}