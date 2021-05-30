package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.EntityDTO;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    private final Map<Long, SseEmitter> unactivatedEmitters = new HashMap<>();
    private final Map<Long, String> emitterTokens = new HashMap<>();

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
                scheduledExecutorService.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.warn("Interrupted!", e);
                executorService.shutdownNow();
                scheduledExecutorService.shutdownNow();
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

//        String emitterToken = UUID.randomUUID().toString();
//        unactivatedEmitters.put(userId, sseEmitter);
//        emitterTokens.put(userId, emitterToken);

//        executorService.execute(() -> {
//            try {
////                sseEmitter.send(SseEmitter.event().name("ActivationRequest").data(emitterToken));
//
//                LOGGER.info(String.format("SseEmitter #%d ready", userId));
//
////                scheduledExecutorService.schedule(() -> {
////                    if (unclaimedEmitters.remove(emitterKey) != null)
////                        sseEmitter.completeWithError(new ConnectException("activation timeout"));
////                }, 10, TimeUnit.SECONDS);
//
//            } catch (IOException e) {
//                sseEmitter.completeWithError(e);
//            }
//        });

        userService.putSubscriber(userId, sseEmitter);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                sseEmitter.send(SseEmitter.event().name("ConnectionTest").data(null));
            } catch (IOException e) {
                sseEmitter.completeWithError(new ConnectException());
            }
        }, 10, 5, TimeUnit.SECONDS);

        LOGGER.info(String.format("SseEmitter #%d initializing...", userId));
        return sseEmitter;
    }


//    @PutMapping("/activateEmitter")
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public void activateEmitter(
//            @RequestHeader("userId") Long userId,
//            @RequestHeader("token") String token,
//            @RequestBody String emitterToken
//
//    ) {
//        userService.verifyUser(userId, token);
//        SseEmitter sseEmitter = unactivatedEmitters.get(userId);
//        String expectedToken = emitterTokens.get(userId);
//        if (sseEmitter == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no emitter to activate");
//        }
//        if (!expectedToken.equals(emitterToken)) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid emitter token!");
//        }
//        unactivatedEmitters.remove(userId);
//        emitterTokens.remove(userId);
//        userService.putSubscriber(userId, sseEmitter);
//    }

    @PutMapping(value = "/observeEntity")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void observeEntity(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestBody Long entityId
    ) {
        User user = userService.verifyUser(userId, token);
        userService.observeEntity(user, entityId);
    }

    @PutMapping(value = "/disregardEntity")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void disregardEntity(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestHeader("entityId") Long entityId
    ) {
        User user = userService.verifyUser(userId, token);
        userService.disregardEntity(user, entityId);
    }

    @GetMapping(value = "/entity/{entityId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public EntityDTO getEntity(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @PathVariable("entityId") Long entityId
    ) {
        userService.verifyUser(userId, token);
        EntityDTO dto = EntityDTO.find(entityId);
        if (dto == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        dto.crop(userId, null);
        return dto;
    }

}
