package com.cos.security1.config.auth;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

// 시큐리티 설정에서 loginProcessingUrl("/login")
// /login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC되어 있는 loadUserByUsername 함수가 실행
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService{
	
	private final UserRepository userRepository;

	// 시큐리티 session = Authentication = UserDetails타입이들어감
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { 
		User userEntity = userRepository.findByUsername(username);
		if (userEntity != null) {
			return new PrincipalDetails(userEntity);
		}
		return null;
	}

}