package edu.udacity.java.nano.chat;

import edu.udacity.java.nano.WebSocketChatApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(SpringJUnit4ClassRunner.class) // @RunWith: integrate spring with junit
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatServiceTest {

    @Resource
    private ChatService chatService;

    @Test
    public void testEnterRequest() throws IOException {

        final AtomicBoolean fired = new AtomicBoolean(false);
        ChatEndpoint fakeChatEndpoint = new ChatEndpoint() {

            @Override
            public void send(String username, ChatResponse chatResponse) {
                if (!fired.get()) {
                    fired.set(true);
                    Assert.assertEquals("WELCOME", chatResponse.getType());
                    Assert.assertEquals("foo", chatResponse.getArg1());
                }
            }

            @Override
            public void sendToAll(ChatResponse chatResponse) throws IOException {
            }

            @Override
            public void registerUsername(String username, Session session) {
            }

            @Override
            public void unregisterUsername(String username) {
            }

            @Override
            public List<String> getAllUsernames() {
                return null;
            }
        };

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setType(ChatRequest.ENTER);
        chatRequest.setArg1("foo");
        chatRequest.setArg2("");
        chatService.handleEnterReq(chatRequest, fakeChatEndpoint,null);
        Assert.assertTrue(fired.get());
    }
}
