package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

@RestController
public class SSEController {

    private final UserService userService;

    private static final long SUBSCRIBER_LIFETIME = 3000L;

    SSEController(UserService userService) {
        this.userService = userService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SSEController.class);
    //only one single thread per client
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    //Termination of thread
    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.error(e.toString());
            }
        }));
    }

    @GetMapping("/update")
    @CrossOrigin
    public SseEmitter subscribeUpdate(@RequestHeader("userId") Long userId,
                                      @RequestHeader("token") String token) {

        userService.verifyUser(userId, token);

        SseEmitter sseEmitter = new SseEmitter(SUBSCRIBER_LIFETIME);

        sseEmitter.onCompletion(() -> LOGGER.info("SseEmitter is completed"));

        sseEmitter.onTimeout(() -> userService.remove_Subscriber(userId));

        sseEmitter.onError((ex) -> LOGGER.info("SseEmitter got error"));



        executor.execute(() -> {
             //Pass the Emitter to the userService
            userService.put_Subscriber(userId, sseEmitter);
        });

        LOGGER.info("Controller exits");
        return sseEmitter;
    }
}
