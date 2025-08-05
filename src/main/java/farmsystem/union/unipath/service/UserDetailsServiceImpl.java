package farmsystem.union.unipath.service;

import farmsystem.union.unipath.domain.CustomUserDetails;
import farmsystem.union.unipath.domain.User;
import farmsystem.union.unipath.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 사용자를 찾을 수 없습니다. : " + userId));

        return new CustomUserDetails(user);
    }
}