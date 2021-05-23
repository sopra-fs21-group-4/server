package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.helpers.SpringContext;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
public class SSEController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SSEController.class);

    private static final long SUBSCRIBER_LIFETIME = Long.MAX_VALUE;
    private static final long CONNECTION_TEST_INTERVAL = 3000;

    private final UserService userService;
    private final Map<String, SseEmitter> unclaimedEmitters = new HashMap<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    SSEController(UserService userService) {
        this.userService = userService;
    }

    //Termination of executor service
    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdown();
            try {
                executorService.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.error(e.toString());
            }
        }));
    }

    @GetMapping("/createEmitter/{userId}")
    @CrossOrigin
    public SseEmitter subscribeUpdate(
            @PathVariable("userId") Long userId
    ) {

        SseEmitter sseEmitter = new SseEmitter(SUBSCRIBER_LIFETIME);

        sseEmitter.onCompletion(() -> {
            LOGGER.info(String.format("SseEmitter #%d completed", userId));
            userService.removeSubscriber(userId);
        });

        sseEmitter.onTimeout(() -> {
            LOGGER.info(String.format("SseEmitter #%d timed out", userId));
            userService.removeSubscriber(userId);
        });

        sseEmitter.onError((ex) -> {
            LOGGER.info(String.format("ERROR at SseEmitter #%d: %s", userId, ex.toString()));
            userService.removeSubscriber(userId);
        });

        String emitterToken = UUID.randomUUID().toString();
        String emitterKey = String.format("%d | %s", userId, emitterToken);

        unclaimedEmitters.put(emitterKey, sseEmitter);

        executorService.execute(() -> {
            try {
                sseEmitter.send(SseEmitter.event().name("ActivationRequest").data(emitterToken));

                LOGGER.info(String.format("SseEmitter #%d ready", userId));

                scheduledExecutorService.schedule(() -> {
                    unclaimedEmitters.remove(emitterKey, MediaType.APPLICATION_JSON);
                    sseEmitter.completeWithError(new ConnectException("activation timeout"));
                }, 10, TimeUnit.SECONDS);

                scheduledExecutorService.scheduleAtFixedRate(() -> {
                    try {
                        sseEmitter.send("connection test", MediaType.ALL);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(new ConnectException());
                    }
                }, 10, 5, TimeUnit.SECONDS);

            } catch (IOException e) {
                sseEmitter.completeWithError(e);
            }
        });

        LOGGER.info(String.format("SseEmitter #%d initializing...", userId));
        return sseEmitter;
    }


    @PutMapping("/activateEmitter")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void activateEmitter(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestBody String emitterToken

    ) {
        userService.verifyUser(userId, token);
        SseEmitter sseEmitter = unclaimedEmitters.get(String.format("%d | %s", userId, emitterToken));
        if (sseEmitter == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "emitterToken invalid or timed out");
        userService.putSubscriber(userId, sseEmitter);
    }

}
