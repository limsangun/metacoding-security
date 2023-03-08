package com.cos.security1.config.oauth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{

	//private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	private final UserRepository userRepository;
	
	// 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {		
		System.out.println("getClientRegistration : "+userRequest.getClientRegistration());	//registrationId로 어떤 OAuth로 로그인 했는지 확인가능.
		System.out.println("getAccessToken : "+userRequest.getAccessToken());			
		
		OAuth2User oAuth2User = super.loadUser(userRequest);
		// 구글로그인 버튼 클릭 -> 구글로그인창 -> 로그인을 완료 -> code를 리턴(OAuth-Client라이브러리) -> AccessToken요청
		// userRequest 정보 -> loadUser함수 호출 -> 구글로부터 회원프로필을 받아준다.		
		System.out.println("getAttributes : "+super.loadUser(userRequest).getAttributes());
		
		String provider = userRequest.getClientRegistration().getClientId();
		String providerId = oAuth2User.getAttribute("sub");
		String username = provider+"_"+providerId;	//  ex)google_1089321893949484
		//String password = bCryptPasswordEncoder.encode("예시");
		String role = "ROLE_USER";
		String email = oAuth2User.getAttribute("email");
		
		User userEntity = userRepository.findByUsername(username);
		if(userEntity == null) {
			userEntity = User.builder()
					.username(username)
					//.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity);
		}
		
		return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
	}
}
