package com.example.restapi.accounts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Test
    void findByUsername(){
        // given
        String pass = "pass";
        String username = "daeho@email.com";
        Account account = Account.builder()
                .email(username)
                .password(pass)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountRepository.save(account);

        // when
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // then
        assertThat(userDetails.getPassword()).isEqualTo(pass);
    }

    @Test
    void findByUsernameFail() {
        // given
        String randomUsername = "randomUsername";

        // when
        UsernameNotFoundException expectedException = assertThrows(UsernameNotFoundException.class, () -> {
            accountService.loadUserByUsername(randomUsername);
        });

        // then
        assertEquals(expectedException.getMessage(), randomUsername);
    }


}