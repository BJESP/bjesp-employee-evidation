package com.example.demo.service;

import com.example.demo.model.Privilege;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.UserDetailsImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.repo.UserRepo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


@Service
public class CustomUserDetailsService implements UserDetailsService {


	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	public CustomUserDetailsService() {
	}

	// Funkcija koja na osnovu username-a iz baze vraca objekat User-a
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userService.findByEmail(email);

		if (user == null) {
			throw new UsernameNotFoundException(String.format("No user found with username '%s'.", email));
		} else {
			//return (UserDetails) user;

			UserDetailsImpl userDetails=new UserDetailsImpl();;
			userDetails.setUser(user);
			System.out.println("ovo je dobijeno"+getAuthorities(user.getRoles()));
			userDetails.setAuthorities((Collection<GrantedAuthority>) getAuthorities(user.getRoles()));
			System.out.println(userDetails.getAuthorities());

			return userDetails;
			//return new org.springframework.security.core.userdetails.User(
					//user.getEmail(), user.getPassword(), user.isEnabled(), true, true,
					//true, getAuthorities(user.getRoles()));
		}
	}

	private Collection<? extends GrantedAuthority> getAuthorities(
			Collection<Role> roles) {

		return getGrantedAuthorities(getPrivileges(roles));
	}

	private List<String> getPrivileges(Collection<Role> roles) {

		List<String> privileges = new ArrayList<>();
		List<Privilege> collection = new ArrayList<>();
		for (Role role : roles) {
			privileges.add(role.getName());
			//collection.addAll(role.getPrivileges());
		}
		for (Privilege item : collection) {
			privileges.add(item.getName());
		}
		return privileges;
	}

	private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (String privilege : privileges) {
			authorities.add(new SimpleGrantedAuthority(privilege));
		}
		return authorities;
	}

}
