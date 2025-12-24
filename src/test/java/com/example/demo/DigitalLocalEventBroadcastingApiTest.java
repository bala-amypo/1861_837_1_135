package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.*;
import com.example.demo.service.impl.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.*;
@Listeners(TestResultListener.class)
public class DigitalLocalEventBroadcastingApiTest {

    private UserRepository userRepository;
    private EventRepository eventRepository;
    private EventUpdateRepository eventUpdateRepository;
    private SubscriptionRepository subscriptionRepository;
    private BroadcastLogRepository broadcastLogRepository;

    private UserService userService;
    private EventService eventService;
    private EventUpdateService eventUpdateService;
    private SubscriptionService subscriptionService;
    private BroadcastService broadcastService;

    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

    @BeforeClass
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        eventRepository = Mockito.mock(EventRepository.class);
        eventUpdateRepository = Mockito.mock(EventUpdateRepository.class);
        subscriptionRepository = Mockito.mock(SubscriptionRepository.class);
        broadcastLogRepository = Mockito.mock(BroadcastLogRepository.class);

        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserServiceImpl(userRepository, passwordEncoder);
        eventService = new EventServiceImpl(eventRepository, userRepository);
        eventUpdateService = new EventUpdateServiceImpl(eventUpdateRepository, eventRepository);
        subscriptionService = new SubscriptionServiceImpl(subscriptionRepository, userRepository, eventRepository);
        broadcastService = new BroadcastServiceImpl(eventUpdateRepository, subscriptionRepository, broadcastLogRepository);

        jwtUtil = new JwtUtil("ThisIsAVerySecureSecretKeyForJwtDemo123456789", 3600000);
    }

    // 1. Simple servlet

    public static class HelloServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setStatus(200);
            resp.setContentType("text/plain");
            PrintWriter writer = resp.getWriter();
            writer.write("Hello, Local Events");
            writer.flush();
        }
    }

    @Test(priority = 1, groups = "servlet")
    public void testServletBasicResponse() throws Exception {
        HelloServlet servlet = new HelloServlet();
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/hello");
        MockHttpServletResponse res = new MockHttpServletResponse();
        servlet.doGet(req, res);
        Assert.assertEquals(res.getStatus(), 200);
        Assert.assertEquals(res.getContentAsString(), "Hello, Local Events");
    }

    @Test(priority = 2, groups = "servlet")
    public void testServletContentType() throws Exception {
        HelloServlet servlet = new HelloServlet();
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/hello");
        MockHttpServletResponse res = new MockHttpServletResponse();
        servlet.doGet(req, res);
        Assert.assertEquals(res.getContentType(), "text/plain");
    }

    @Test(priority = 3, groups = "servlet")
    public void testServletNotPost() throws Exception {
        HelloServlet servlet = new HelloServlet();
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/hello");
        MockHttpServletResponse res = new MockHttpServletResponse();
        servlet.service(req, res);
        Assert.assertNotEquals(res.getStatus(), 200);
    }

    @Test(priority = 4, groups = "servlet")
    public void testServletEmptyPath() throws Exception {
        HelloServlet servlet = new HelloServlet();
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "");
        MockHttpServletResponse res = new MockHttpServletResponse();
        servlet.doGet(req, res);
        Assert.assertEquals(res.getStatus(), 200);
    }

    @Test(priority = 5, groups = "servlet")
    public void testServletMultipleCalls() throws Exception {
        HelloServlet servlet = new HelloServlet();
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/hello");
        MockHttpServletResponse res1 = new MockHttpServletResponse();
        MockHttpServletResponse res2 = new MockHttpServletResponse();
        servlet.doGet(req, res1);
        servlet.doGet(req, res2);
        Assert.assertEquals(res1.getContentAsString(), "Hello, Local Events");
        Assert.assertEquals(res2.getContentAsString(), "Hello, Local Events");
    }

    @Test(priority = 6, groups = "servlet")
    public void testServletWriterNotNull() throws Exception {
        HelloServlet servlet = new HelloServlet();
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/hello");
        MockHttpServletResponse res = new MockHttpServletResponse();
        servlet.doGet(req, res);
        Assert.assertNotNull(res.getWriter());
    }

    @Test(priority = 7, groups = "servlet")
    public void testServletStatusCodeRange() throws Exception {
        HelloServlet servlet = new HelloServlet();
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/hello");
        MockHttpServletResponse res = new MockHttpServletResponse();
        servlet.doGet(req, res);
        Assert.assertTrue(res.getStatus() >= 200 && res.getStatus() < 300);
    }

    @Test(priority = 8, groups = "servlet")
    public void testServletOutputLength() throws Exception {
        HelloServlet servlet = new HelloServlet();
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/hello");
        MockHttpServletResponse res = new MockHttpServletResponse();
        servlet.doGet(req, res);
        Assert.assertTrue(res.getContentAsString().length() > 0);
    }

    // 2. CRUD operations with services

    @Test(priority = 9, groups = "crud")
    public void testCreateEventSuccess() {
        User publisher = new User();
        publisher.setId(1L);
        publisher.setRole(Role.PUBLISHER);

        Event event = new Event();
        event.setPublisher(publisher);
        event.setTitle("Title");
        event.setDescription("Desc");
        event.setLocation("Loc");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(publisher));
        Mockito.when(eventRepository.save(Mockito.any(Event.class))).thenAnswer(i -> i.getArgument(0));

        Event saved = eventService.createEvent(event);
        Assert.assertEquals(saved.getTitle(), "Title");
    }

    @Test(priority = 10, groups = "crud")
    public void testCreateEventInvalidRole() {
        User publisher = new User();
        publisher.setId(2L);
        publisher.setRole(Role.SUBSCRIBER);

        Event event = new Event();
        event.setPublisher(publisher);
        event.setTitle("T");
        event.setDescription("D");
        event.setLocation("L");

        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(publisher));

        try {
            eventService.createEvent(event);
            Assert.fail("Expected BadRequestException");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Only PUBLISHER or ADMIN"));
        }
    }

    @Test(priority = 11, groups = "crud")
    public void testUpdateEventSuccess() {
        Event existing = new Event();
        existing.setId(5L);
        existing.setTitle("Old");

        Mockito.when(eventRepository.findById(5L)).thenReturn(Optional.of(existing));
        Mockito.when(eventRepository.save(Mockito.any(Event.class))).thenAnswer(i -> i.getArgument(0));

        Event updated = new Event();
        updated.setTitle("NewTitle");
        updated.setDescription("NewDesc");
        updated.setLocation("NewLoc");
        updated.setCategory("Traffic");

        Event result = eventService.updateEvent(5L, updated);
        Assert.assertEquals(result.getTitle(), "NewTitle");
    }

    @Test(priority = 12, groups = "crud")
    public void testUpdateEventNotFound() {
        Mockito.when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        Event updated = new Event();
        updated.setTitle("X");

        try {
            eventService.updateEvent(999L, updated);
            Assert.fail("Expected ResourceNotFoundException");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Event not found"));
        }
    }

    @Test(priority = 13, groups = "crud")
    public void testGetActiveEvents() {
        Event e1 = new Event();
        e1.setActive(true);
        Event e2 = new Event();
        e2.setActive(true);

        Mockito.when(eventRepository.findByIsActiveTrue()).thenReturn(List.of(e1, e2));

        List<Event> events = eventService.getActiveEvents();
        Assert.assertEquals(events.size(), 2);
    }

    @Test(priority = 14, groups = "crud")
    public void testDeactivateEvent() {
        Event e1 = new Event();
        e1.setId(10L);
        e1.setActive(true);

        Mockito.when(eventRepository.findById(10L)).thenReturn(Optional.of(e1));
        Mockito.when(eventRepository.save(Mockito.any(Event.class))).thenAnswer(i -> i.getArgument(0));

        eventService.deactivateEvent(10L);
        Assert.assertFalse(e1.isActive());
    }

    @Test(priority = 15, groups = "crud")
    public void testDeactivateEventNotFound() {
        Mockito.when(eventRepository.findById(11L)).thenReturn(Optional.empty());
        try {
            eventService.deactivateEvent(11L);
            Assert.fail("Expected ResourceNotFoundException");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Event not found"));
        }
    }

    @Test(priority = 16, groups = "crud")
    public void testGetEventByIdSuccess() {
        Event e = new Event();
        e.setId(20L);
        Mockito.when(eventRepository.findById(20L)).thenReturn(Optional.of(e));
        Event result = eventService.getById(20L);
        Assert.assertEquals(result.getId(), Long.valueOf(20L));
    }

    // 3. DI and IoC

    @Test(priority = 17, groups = "ioc")
    public void testUserServiceRegisterEncryptsPassword() {
        User u = new User();
        u.setEmail("x@y.com");
        u.setPassword("plain");
        Mockito.when(userRepository.existsByEmail("x@y.com")).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> i.getArgument(0));

        User saved = userService.register(u);
        Assert.assertNotEquals(saved.getPassword(), "plain");
        Assert.assertTrue(saved.getPassword().startsWith("$2"));
    }

    @Test(priority = 18, groups = "ioc")
    public void testUserServiceDuplicateEmail() {
        User u = new User();
        u.setEmail("dup@y.com");
        Mockito.when(userRepository.existsByEmail("dup@y.com")).thenReturn(true);
        try {
            userService.register(u);
            Assert.fail("Expected BadRequestException");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Email already registered"));
        }
    }

    @Test(priority = 19, groups = "ioc")
    public void testUserServiceFindByEmailUsesRepo() {
        User u = new User();
        u.setEmail("a@b.com");
        Mockito.when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(u));
        User found = userService.findByEmail("a@b.com");
        Assert.assertEquals(found.getEmail(), "a@b.com");
    }

    @Test(priority = 20, groups = "ioc")
    public void testUserServiceFindByEmailNotFound() {
        Mockito.when(userRepository.findByEmail("zzz@x.com")).thenReturn(Optional.empty());
        try {
            userService.findByEmail("zzz@x.com");
            Assert.fail("Expected ResourceNotFoundException");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("User not found"));
        }
    }

    @Test(priority = 21, groups = "ioc")
    public void testEventServiceUsesUserRepo() {
        User publisher = new User();
        publisher.setId(7L);
        publisher.setRole(Role.PUBLISHER);
        Mockito.when(userRepository.findById(7L)).thenReturn(Optional.of(publisher));
        Mockito.when(eventRepository.save(Mockito.any(Event.class))).thenAnswer(i -> i.getArgument(0));

        Event e = new Event();
        e.setPublisher(publisher);
        e.setTitle("T");
        e.setDescription("D");
        e.setLocation("L");

        Event saved = eventService.createEvent(e);
        Assert.assertEquals(saved.getPublisher().getId(), Long.valueOf(7L));
    }

    @Test(priority = 22, groups = "ioc")
    public void testSubscriptionServiceUsesEventRepo() {
        User u = new User();
        u.setId(1L);
        Event e = new Event();
        e.setId(2L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        Mockito.when(eventRepository.findById(2L)).thenReturn(Optional.of(e));
        Mockito.when(subscriptionRepository.existsByUserIdAndEventId(1L, 2L)).thenReturn(false);
        Mockito.when(subscriptionRepository.save(Mockito.any(Subscription.class))).thenAnswer(i -> i.getArgument(0));

        Subscription s = subscriptionService.subscribe(1L, 2L);
        Assert.assertEquals(s.getUser().getId(), Long.valueOf(1L));
        Assert.assertEquals(s.getEvent().getId(), Long.valueOf(2L));
    }

    @Test(priority = 23, groups = "ioc")
    public void testSubscriptionDuplicatePrevented() {
        Mockito.when(subscriptionRepository.existsByUserIdAndEventId(1L, 3L)).thenReturn(true);
        try {
            subscriptionService.subscribe(1L, 3L);
            Assert.fail("Expected BadRequestException");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Already subscribed"));
        }
    }

    @Test(priority = 24, groups = "ioc")
    public void testBroadcastServiceUsesSubsAndLogs() {
        Event event = new Event();
        event.setId(50L);
        EventUpdate update = new EventUpdate();
        update.setId(9L);
        update.setEvent(event);

        User u1 = new User();
        u1.setId(1L);
        User u2 = new User();
        u2.setId(2L);

        Subscription s1 = new Subscription();
        s1.setUser(u1);
        s1.setEvent(event);
        Subscription s2 = new Subscription();
        s2.setUser(u2);
        s2.setEvent(event);

        Mockito.when(eventUpdateRepository.findById(9L)).thenReturn(Optional.of(update));
        Mockito.when(subscriptionRepository.findByEventId(50L)).thenReturn(List.of(s1, s2));
        Mockito.when(broadcastLogRepository.save(Mockito.any(BroadcastLog.class))).thenAnswer(i -> i.getArgument(0));

        broadcastService.broadcastUpdate(9L);
        Mockito.verify(broadcastLogRepository, Mockito.times(2)).save(Mockito.any(BroadcastLog.class));
    }

    // 4. Hibernate config and lifecycle

    @Test(priority = 25, groups = "hibernate")
    public void testEventPrePersistSetsTimestamps() {
        Event e = new Event();
        e.setTitle("T");
        e.setDescription("D");
        e.setLocation("Loc");
        e.onCreate();
        Assert.assertNotNull(e.getCreatedAt());
        Assert.assertNotNull(e.getLastUpdatedAt());
        Assert.assertTrue(e.isActive());
    }

    @Test(priority = 26, groups = "hibernate")
    public void testEventPreUpdateUpdatesLastUpdatedAt() {
        Event e = new Event();
        e.onCreate();
        Instant first = e.getLastUpdatedAt();
        e.onUpdate();
        Assert.assertNotNull(e.getLastUpdatedAt());
    }

    @Test(priority = 27, groups = "hibernate")
    public void testUserPrePersistDefaultRole() {
        User u = new User();
        u.setEmail("u@test.com");
        u.setPassword("x");
        u.onCreate();
        Assert.assertEquals(u.getRole(), Role.SUBSCRIBER);
        Assert.assertNotNull(u.getCreatedAt());
    }

    @Test(priority = 28, groups = "hibernate")
    public void testSubscriptionPrePersistSetSubscribedAt() {
        Subscription s = new Subscription();
        s.onCreate();
        Assert.assertNotNull(s.getSubscribedAt());
    }

    @Test(priority = 29, groups = "hibernate")
    public void testBroadcastLogDefaultStatus() {
        BroadcastLog log = new BroadcastLog();
        Assert.assertEquals(log.getDeliveryStatus(), DeliveryStatus.SENT);
    }

    @Test(priority = 30, groups = "hibernate")
    public void testEventUpdatePrePersistDefaultSeverity() {
        EventUpdate update = new EventUpdate();
        update.onCreate();
        Assert.assertNotNull(update.getTimestamp());
        Assert.assertEquals(update.getSeverityLevel(), SeverityLevel.LOW);
    }

    @Test(priority = 31, groups = "hibernate")
    public void testCreateEventWithRepositoryMock() {
        Event e = new Event();
        e.setTitle("Hibernate");
        Mockito.when(eventRepository.save(e)).thenReturn(e);
        Event saved = eventRepository.save(e);
        Assert.assertEquals(saved.getTitle(), "Hibernate");
    }

    @Test(priority = 32, groups = "hibernate")
    public void testDeleteEventWithRepositoryMock() {
        Event e = new Event();
        e.setId(100L);
        eventRepository.delete(e);
        Mockito.verify(eventRepository, Mockito.times(1)).delete(e);
    }

    // 5. JPA mapping / normalization

    @Test(priority = 33, groups = "mapping")
    public void testUserEventRelationshipManyToOne() {
        User publisher = new User();
        publisher.setId(5L);
        Event e = new Event();
        e.setPublisher(publisher);
        Assert.assertEquals(e.getPublisher().getId(), Long.valueOf(5L));
    }

    @Test(priority = 34, groups = "mapping")
    public void testEventUpdateHasEventReference() {
        Event event = new Event();
        event.setId(4L);
        EventUpdate update = new EventUpdate();
        update.setEvent(event);
        Assert.assertEquals(update.getEvent().getId(), Long.valueOf(4L));
    }

    @Test(priority = 35, groups = "mapping")
    public void testSubscriptionHasUserAndEvent() {
        User u = new User();
        u.setId(1L);
        Event e = new Event();
        e.setId(2L);
        Subscription s = new Subscription();
        s.setUser(u);
        s.setEvent(e);
        Assert.assertEquals(s.getUser().getId(), Long.valueOf(1L));
        Assert.assertEquals(s.getEvent().getId(), Long.valueOf(2L));
    }

    @Test(priority = 36, groups = "mapping")
    public void testBroadcastLogHasUpdateAndSubscriber() {
        EventUpdate update = new EventUpdate();
        update.setId(3L);
        User u = new User();
        u.setId(4L);
        BroadcastLog log = new BroadcastLog();
        log.setEventUpdate(update);
        log.setSubscriber(u);
        Assert.assertEquals(log.getEventUpdate().getId(), Long.valueOf(3L));
        Assert.assertEquals(log.getSubscriber().getId(), Long.valueOf(4L));
    }

    @Test(priority = 37, groups = "mapping")
    public void testNormalizedNoRedundantFieldsInSubscription() {
        Subscription s = new Subscription();
        Assert.assertNull(s.getId());
    }

    @Test(priority = 38, groups = "mapping")
    public void testSubscriberEntityDependsOnUserTable() {
        User u = new User();
        u.setId(99L);
        Subscription s = new Subscription();
        s.setUser(u);
        Assert.assertEquals(s.getUser().getId(), Long.valueOf(99L));
    }

    @Test(priority = 39, groups = "mapping")
    public void testEventCategoryCanBeSeparatedDimension() {
        Event e = new Event();
        e.setCategory("Weather");
        Assert.assertEquals(e.getCategory(), "Weather");
    }

    @Test(priority = 40, groups = "mapping")
    public void testEventLocationNormalizedAsStringField() {
        Event e = new Event();
        e.setLocation("Coimbatore");
        Assert.assertTrue(e.getLocation().contains("Coim"));
    }

    // 6. Many-to-many via Subscription

    @Test(priority = 41, groups = "manyToMany")
    public void testUserEventManyToManyThroughSubscription() {
        User u = new User();
        u.setId(1L);
        Event e = new Event();
        e.setId(2L);

        Subscription s = new Subscription();
        s.setUser(u);
        s.setEvent(e);
        Assert.assertEquals(s.getUser().getId(), Long.valueOf(1L));
        Assert.assertEquals(s.getEvent().getId(), Long.valueOf(2L));
    }

    @Test(priority = 42, groups = "manyToMany")
    public void testUserSubscribedToMultipleEvents() {
        User u = new User();
        u.setId(1L);
        Event e1 = new Event();
        e1.setId(10L);
        Event e2 = new Event();
        e2.setId(11L);

        Subscription s1 = new Subscription();
        s1.setUser(u);
        s1.setEvent(e1);
        Subscription s2 = new Subscription();
        s2.setUser(u);
        s2.setEvent(e2);

        List<Subscription> subs = List.of(s1, s2);
        Assert.assertEquals(subs.size(), 2);
    }

    @Test(priority = 43, groups = "manyToMany")
    public void testEventHasMultipleSubscribers() {
        Event e = new Event();
        e.setId(20L);

        User u1 = new User();
        u1.setId(1L);
        User u2 = new User();
        u2.setId(2L);

        Subscription s1 = new Subscription();
        s1.setEvent(e);
        s1.setUser(u1);
        Subscription s2 = new Subscription();
        s2.setEvent(e);
        s2.setUser(u2);

        List<Subscription> subs = List.of(s1, s2);
        long distinctUsers = subs.stream().map(x -> x.getUser().getId()).distinct().count();
        Assert.assertEquals(distinctUsers, 2L);
    }

    @Test(priority = 44, groups = "manyToMany")
    public void testSubscriptionServiceIsSubscribedTrue() {
        Mockito.when(subscriptionRepository.existsByUserIdAndEventId(1L, 2L)).thenReturn(true);
        Assert.assertTrue(subscriptionService.isSubscribed(1L, 2L));
    }

    @Test(priority = 45, groups = "manyToMany")
    public void testSubscriptionServiceIsSubscribedFalse() {
        Mockito.when(subscriptionRepository.existsByUserIdAndEventId(1L, 3L)).thenReturn(false);
        Assert.assertFalse(subscriptionService.isSubscribed(1L, 3L));
    }

    @Test(priority = 46, groups = "manyToMany")
    public void testUnsubscribeSuccess() {
        User u = new User();
        u.setId(1L);
        Event e = new Event();
        e.setId(2L);

        Subscription s = new Subscription();
        s.setUser(u);
        s.setEvent(e);

        Mockito.when(subscriptionRepository.findByUserIdAndEventId(1L, 2L)).thenReturn(Optional.of(s));
        subscriptionService.unsubscribe(1L, 2L);
        Mockito.verify(subscriptionRepository, Mockito.times(1)).delete(s);
    }

    @Test(priority = 47, groups = "manyToMany")
    public void testUnsubscribeMissing() {
        Mockito.when(subscriptionRepository.findByUserIdAndEventId(1L, 99L)).thenReturn(Optional.empty());
        try {
            subscriptionService.unsubscribe(1L, 99L);
            Assert.fail("Expected BadRequestException");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Subscription not found"));
        }
    }

    @Test(priority = 48, groups = "manyToMany")
    public void testGetUserSubscriptionsListSize() {
        User u = new User();
        u.setId(1L);
        Event e = new Event();
        e.setId(2L);
        Subscription s = new Subscription();
        s.setUser(u);
        s.setEvent(e);
        Mockito.when(subscriptionRepository.findByUserId(1L)).thenReturn(List.of(s));
        List<Subscription> list = subscriptionService.getUserSubscriptions(1L);
        Assert.assertEquals(list.size(), 1);
    }

    // 7. Security and JWT

    @Test(priority = 49, groups = "security")
    public void testJwtTokenContainsUserIdAndRole() {
        String token = jwtUtil.generateToken(1L, "user@test.com", "SUBSCRIBER");
        Assert.assertTrue(jwtUtil.validateToken(token));
        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        Assert.assertEquals(userId, Long.valueOf(1L));
        Assert.assertEquals(role, "SUBSCRIBER");
    }

    @Test(priority = 50, groups = "security")
    public void testJwtInvalidTokenFailsValidation() {
        Assert.assertFalse(jwtUtil.validateToken("invalid.token.here"));
    }

    @Test(priority = 51, groups = "security")
    public void testGenerateTokenDifferentUsers() {
        String token1 = jwtUtil.generateToken(1L, "a@test.com", "ADMIN");
        String token2 = jwtUtil.generateToken(2L, "b@test.com", "PUBLISHER");
        Assert.assertNotEquals(token1, token2);
    }

    @Test(priority = 52, groups = "security")
    public void testJwtUsernameExtraction() {
        String token = jwtUtil.generateToken(3L, "x@test.com", "SUBSCRIBER");
        String username = jwtUtil.getUsernameFromToken(token);
        Assert.assertEquals(username, "x@test.com");
    }

    @Test(priority = 53, groups = "security")
    public void testJwtExpirationFuture() {
        String token = jwtUtil.generateToken(5L, "y@test.com", "SUBSCRIBER");
        Assert.assertTrue(jwtUtil.validateToken(token));
    }

    @Test(priority = 54, groups = "security")
    public void testJwtRoleClaimPresent() {
        String token = jwtUtil.generateToken(10L, "role@test.com", "ADMIN");
        String role = jwtUtil.getRoleFromToken(token);
        Assert.assertEquals(role, "ADMIN");
    }

    @Test(priority = 55, groups = "security")
    public void testJwtTamperedTokenFails() {
        String token = jwtUtil.generateToken(1L, "x@test.com", "SUBSCRIBER");
        String tampered = token + "abc";
        Assert.assertFalse(jwtUtil.validateToken(tampered));
    }

    @Test(priority = 56, groups = "security")
    public void testPasswordEncoderMatches() {
        String raw = "password";
        String encoded = passwordEncoder.encode(raw);
        Assert.assertTrue(passwordEncoder.matches(raw, encoded));
    }

    // 8. HQL/HCQL-style advanced querying via repositories

    @Test(priority = 57, groups = "query")
    public void testFindActiveEventsByCategory() {
        Event e1 = new Event();
        e1.setCategory("Weather");
        e1.setActive(true);

        Mockito.when(eventRepository.findByIsActiveTrueAndCategory("Weather")).thenReturn(List.of(e1));

        List<Event> list = eventRepository.findByIsActiveTrueAndCategory("Weather");
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0).getCategory(), "Weather");
    }

    @Test(priority = 58, groups = "query")
    public void testFindActiveEventsByLocation() {
        Event e1 = new Event();
        e1.setLocation("Coimbatore North");
        e1.setActive(true);

        Mockito.when(eventRepository.findByIsActiveTrueAndLocationContainingIgnoreCase("coimbatore"))
                .thenReturn(List.of(e1));

        List<Event> list = eventRepository.findByIsActiveTrueAndLocationContainingIgnoreCase("coimbatore");
        Assert.assertEquals(list.size(), 1);
    }

    @Test(priority = 59, groups = "query")
    public void testGetUpdatesForEventOrdered() {
        EventUpdate u1 = new EventUpdate();
        u1.setId(1L);
        EventUpdate u2 = new EventUpdate();
        u2.setId(2L);
        Mockito.when(eventUpdateRepository.findByEventIdOrderByTimestampAsc(5L)).thenReturn(List.of(u1, u2));
        List<EventUpdate> updates = eventUpdateService.getUpdatesForEvent(5L);
        Assert.assertEquals(updates.size(), 2);
        Assert.assertEquals(updates.get(0).getId(), Long.valueOf(1L));
    }

    @Test(priority = 60, groups = "query")
    public void testFindSubscribersForEvent() {
        Subscription s1 = new Subscription();
        Subscription s2 = new Subscription();
        Mockito.when(subscriptionRepository.findByEventId(5L)).thenReturn(List.of(s1, s2));
        List<Subscription> list = subscriptionRepository.findByEventId(5L);
        Assert.assertEquals(list.size(), 2);
    }

    @Test(priority = 61, groups = "query")
    public void testFetchBroadcastLogsByUpdate() {
        BroadcastLog log1 = new BroadcastLog();
        log1.setId(1L);
        Mockito.when(broadcastLogRepository.findByEventUpdateId(7L)).thenReturn(List.of(log1));
        List<BroadcastLog> logs = broadcastService.getLogsForUpdate(7L);
        Assert.assertEquals(logs.size(), 1);
    }

    @Test(priority = 62, groups = "query")
    public void testCheckSubscriptionExistsTrue() {
        Mockito.when(subscriptionRepository.existsByUserIdAndEventId(1L, 8L)).thenReturn(true);
        Assert.assertTrue(subscriptionRepository.existsByUserIdAndEventId(1L, 8L));
    }

    @Test(priority = 63, groups = "query")
    public void testCheckSubscriptionExistsFalse() {
        Mockito.when(subscriptionRepository.existsByUserIdAndEventId(2L, 9L)).thenReturn(false);
        Assert.assertFalse(subscriptionRepository.existsByUserIdAndEventId(2L, 9L));
    }

    @Test(priority = 64, groups = "query")
    public void testRecordDeliveryUpdatesLogStatus() {
        EventUpdate update = new EventUpdate();
        update.setId(10L);
        User u = new User();
        u.setId(20L);
        BroadcastLog log = new BroadcastLog();
        log.setEventUpdate(update);
        log.setSubscriber(u);
        log.setDeliveryStatus(DeliveryStatus.SENT);

        Mockito.when(broadcastLogRepository.findByEventUpdateId(10L)).thenReturn(List.of(log));
        Mockito.when(broadcastLogRepository.save(Mockito.any(BroadcastLog.class))).thenAnswer(i -> i.getArgument(0));

        broadcastService.recordDelivery(10L, 20L, false);
        Assert.assertEquals(log.getDeliveryStatus(), DeliveryStatus.FAILED);
    }
}
