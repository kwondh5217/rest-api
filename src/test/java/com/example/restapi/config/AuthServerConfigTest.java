package com.example.restapi.config;

import com.example.restapi.accounts.Account;
import com.example.restapi.accounts.AccountRole;
import com.example.restapi.accounts.AccountService;
import com.example.restapi.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @DisplayName("인증 토큰을 발급 받는 테스트")
    @Test
    void getAuthJwtToken() throws Exception {
        String userEmail = "daeho@email.com";
        String userPassword = "pass";
        Account pass = Account.builder()
                .email(userEmail)
                .password(userPassword)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(pass);

        String clientId = "myApp";
        String clientSecret = "pass";

        this.mockMvc.perform(post("/oauth/token")
                        .with(httpBasic(clientId, userEmail))
                        .param("username", userEmail)
                        .param("password", userPassword)
                        .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("acess_token").exists());
    }


}