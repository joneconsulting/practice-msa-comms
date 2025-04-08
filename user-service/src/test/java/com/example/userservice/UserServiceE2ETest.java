package com.example.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.jayway.jsonpath.JsonPath;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ✅ WireMock용 stub methods
import static com.github.tomakehurst.wiremock.client.WireMock.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8082) // ✅ 실제 RestTemplate가 사용하는 포트에 맞춰야 함!
@TestPropertySource(properties = {
        "spring.mvc.servlet.path=/",           // 명시적 servlet path
        "spring.mvc.servlet.name=dispatcherServlet"  // ⭐ 서블릿 이름 설정
})
public class UserServiceE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testE2E_userCreationAndFetchWithOrderStub() throws Exception {
        // 1. 사용자 생성
        String userJson = """
        {
            "email": "e2e@mock.com",
            "name": "E2E",
            "pwd": "1234"
        }
        """;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String userId = JsonPath.read(responseBody, "$.userId");

        // 2. 주문 서비스 Stub 등록 (실제 URL과 일치하게 설정!)
        stubFor(get(urlEqualTo("/" + userId + "/orders"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        // 3. 사용자 조회
        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orders").isArray())
                .andExpect(jsonPath("$.orders.length()").value(0));
    }
}