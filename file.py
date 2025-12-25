import os

files = {
    "pom.xml": """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.4.1</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>demo</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>demo</name>
	<description>Demo project for Spring Boot</description>
	<url/>
	<licenses>
		<license/>
	</licenses>
	<developers>
		<developer/>
	</developers>
	<scm>
		<connection/>
		<developerConnection/>
		<tag/>
		<url/>
	</scm>
	<properties>
		<java.version>17</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>com.mysql</groupId>
			<artifactId>mysql-connector-j</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springdoc</groupId>
			<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
			<version>2.8.3</version>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-api</artifactId>
			<version>0.11.5</version>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-impl</artifactId>
			<version>0.11.5</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-jackson</artifactId>
			<version>0.11.5</version>
			<scope>runtime</scope>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
			<exclusions>
				<exclusion>
					<groupId>org.junit.jupiter</groupId>
					<artifactId>junit-jupiter</artifactId>
				</exclusion>
				<exclusion>
					<groupId>org.junit.vintage</groupId>
					<artifactId>junit-vintage-engine</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>org.testng</groupId>
			<artifactId>testng</artifactId>
			<version>7.9.0</version>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>""",
    "src/main/java/com/example/demo/DemoApplication.java": """package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}""",
    "src/main/java/com/example/demo/config/SecurityConfig.java": """package com.example.demo.config;

import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/simple-status").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}""",
    "src/main/java/com/example/demo/controller/AuthController.java": """package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        // Default role is SUBSCRIBER handled by @PrePersist if null
        User saved = userService.register(user);

        String token = jwtUtil.generateToken(saved.getId(), saved.getEmail(), saved.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, saved.getId(), saved.getEmail(), saved.getRole().name()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userService.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        
        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getEmail(), user.getRole().name()));
    }
}""",
    "src/main/java/com/example/demo/controller/BroadcastController.java": """package com.example.demo.controller;

import com.example.demo.entity.BroadcastLog;
import com.example.demo.service.BroadcastService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/broadcasts")
public class BroadcastController {

    private final BroadcastService broadcastService;

    public BroadcastController(BroadcastService broadcastService) {
        this.broadcastService = broadcastService;
    }

    @PostMapping("/trigger/{updateId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> triggerBroadcast(@PathVariable Long updateId) {
        broadcastService.broadcastUpdate(updateId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logs/{updateId}")
    public ResponseEntity<List<BroadcastLog>> getLogsForUpdate(@PathVariable Long updateId) {
        return ResponseEntity.ok(broadcastService.getLogsForUpdate(updateId));
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BroadcastLog>> getAllLogs() {
        return ResponseEntity.ok(broadcastService.getAllLogs());
    }
}""",
    "src/main/java/com/example/demo/controller/EventController.java": """package com.example.demo.controller;

import com.example.demo.dto.EventRequest;
import com.example.demo.entity.Event;
import com.example.demo.entity.User;
import com.example.demo.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/")
    @PreAuthorize("hasAnyRole('PUBLISHER', 'ADMIN')")
    public ResponseEntity<Event> createEvent(@RequestBody EventRequest request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setCategory(request.getCategory());
        
        User publisher = new User();
        publisher.setId(request.getPublisherId());
        event.setPublisher(publisher);

        return new ResponseEntity<>(eventService.createEvent(event), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PUBLISHER', 'ADMIN')")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody EventRequest request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setCategory(request.getCategory());
        // Publisher usually shouldn't change, or maybe it can? Requirements don't specify.
        // Service updateEvent only updates title, desc, loc, category.
        
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    @GetMapping("/")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Event>> getActiveEvents() {
        return ResponseEntity.ok(eventService.getActiveEvents());
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('PUBLISHER', 'ADMIN')")
    public ResponseEntity<Void> deactivateEvent(@PathVariable Long id) {
        eventService.deactivateEvent(id);
        return ResponseEntity.ok().build();
    }
}""",
    "src/main/java/com/example/demo/controller/EventUpdateController.java": """package com.example.demo.controller;

import com.example.demo.dto.EventUpdateRequest;
import com.example.demo.entity.Event;
import com.example.demo.entity.EventUpdate;
import com.example.demo.entity.UpdateType;
import com.example.demo.service.EventUpdateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/updates")
public class EventUpdateController {

    private final EventUpdateService eventUpdateService;

    public EventUpdateController(EventUpdateService eventUpdateService) {
        this.eventUpdateService = eventUpdateService;
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<EventUpdate> publishUpdate(@RequestBody EventUpdateRequest request) {
        EventUpdate update = new EventUpdate();
        Event event = new Event();
        event.setId(request.getEventId());
        update.setEvent(event);
        update.setUpdateContent(request.getUpdateContent());
        update.setUpdateType(UpdateType.valueOf(request.getUpdateType()));
        
        return new ResponseEntity<>(eventUpdateService.publishUpdate(update), HttpStatus.CREATED);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventUpdate>> getUpdatesForEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventUpdateService.getUpdatesForEvent(eventId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventUpdate> getUpdate(@PathVariable Long id) {
        return ResponseEntity.of(eventUpdateService.getUpdateById(id));
    }

    @GetMapping("/")
    public ResponseEntity<List<EventUpdate>> getAllUpdates() {
        return ResponseEntity.ok(eventUpdateService.getAllUpdates());
    }
}""",
    "src/main/java/com/example/demo/controller/SubscriptionController.java": """package com.example.demo.controller;

import com.example.demo.entity.Subscription;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.SubscriptionService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final JwtUtil jwtUtil;

    public SubscriptionController(SubscriptionService subscriptionService, JwtUtil jwtUtil) {
        this.subscriptionService = subscriptionService;
        this.jwtUtil = jwtUtil;
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return jwtUtil.getUserIdFromToken(token.substring(7));
        }
        throw new RuntimeException("Invalid Token");
    }

    @PostMapping("/{eventId}")
    @PreAuthorize("hasRole('SUBSCRIBER')")
    public ResponseEntity<Subscription> subscribe(@PathVariable Long eventId, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return new ResponseEntity<>(subscriptionService.subscribe(userId, eventId), HttpStatus.CREATED);
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('SUBSCRIBER')")
    public ResponseEntity<Void> unsubscribe(@PathVariable Long eventId, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        subscriptionService.unsubscribe(userId, eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Subscription>> getUserSubscriptions(@PathVariable Long userId) {
        // Validation: Admin or same user? Prompt doesn't specify, assumes protected.
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(userId));
    }

    @GetMapping("/check/{userId}/{eventId}")
    public ResponseEntity<Boolean> checkSubscription(@PathVariable Long userId, @PathVariable Long eventId) {
        return ResponseEntity.ok(subscriptionService.isSubscribed(userId, eventId));
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }
}""",
    "src/main/java/com/example/demo/controller/UserController.java": """package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil; // Maybe needed if we return token, but for admin create maybe just user info?
    // Requirement says returns AuthResponse for registration.

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        if (request.getRole() != null) {
            user.setRole(Role.valueOf(request.getRole()));
        }
        User saved = userService.register(user);

        String token = jwtUtil.generateToken(saved.getId(), saved.getEmail(), saved.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, saved.getId(), saved.getEmail(), saved.getRole().name()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.of(userService.findById(id));
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }
}""",
    "src/main/java/com/example/demo/dto/AuthRequest.java": """package com.example.demo.dto;

public class AuthRequest {
    private String email;
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}""",
    "src/main/java/com/example/demo/dto/AuthResponse.java": """package com.example.demo.dto;

public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String role;

    public AuthResponse(String token, Long userId, String email, String role) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}""",
    "src/main/java/com/example/demo/dto/EventRequest.java": """package com.example.demo.dto;

public class EventRequest {
    private String title;
    private String description;
    private String location;
    private String category;
    private Long publisherId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Long getPublisherId() { return publisherId; }
    public void setPublisherId(Long publisherId) { this.publisherId = publisherId; }
}""",
    "src/main/java/com/example/demo/dto/EventUpdateRequest.java": """package com.example.demo.dto;

public class EventUpdateRequest {
    private Long eventId;
    private String updateContent;
    private String updateType;

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getUpdateContent() { return updateContent; }
    public void setUpdateContent(String updateContent) { this.updateContent = updateContent; }
    public String getUpdateType() { return updateType; }
    public void setUpdateType(String updateType) { this.updateType = updateType; }
}""",
    "src/main/java/com/example/demo/dto/RegisterRequest.java": """package com.example.demo.dto;

public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String role; // Optional

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}""",
    "src/main/java/com/example/demo/entity/BroadcastLog.java": """package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "broadcast_logs")
public class BroadcastLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_update_id", nullable = false)
    private EventUpdate eventUpdate;

    @ManyToOne
    @JoinColumn(name = "subscriber_id", nullable = false)
    private User subscriber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus deliveryStatus = DeliveryStatus.SENT;

    @Column(updatable = false)
    private LocalDateTime sentAt;

    public BroadcastLog() {}

    public BroadcastLog(Long id, EventUpdate eventUpdate, User subscriber, DeliveryStatus deliveryStatus, LocalDateTime sentAt) {
        this.id = id;
        this.eventUpdate = eventUpdate;
        this.subscriber = subscriber;
        this.deliveryStatus = deliveryStatus;
        this.sentAt = sentAt;
    }

    @PrePersist
    public void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
        if (deliveryStatus == null) {
            deliveryStatus = DeliveryStatus.SENT;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventUpdate getEventUpdate() {
        return eventUpdate;
    }

    public void setEventUpdate(EventUpdate eventUpdate) {
        this.eventUpdate = eventUpdate;
    }

    public User getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(User subscriber) {
        this.subscriber = subscriber;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}""",
    "src/main/java/com/example/demo/entity/DeliveryStatus.java": """package com.example.demo.entity;

public enum DeliveryStatus {
    PENDING,
    SENT,
    FAILED
}""",
    "src/main/java/com/example/demo/entity/Event.java": """package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String location;

    private String category;

    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    private User publisher;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private Instant lastUpdatedAt; // Test expects Instant for lastUpdatedAt based on usage? 
    // Wait, requirement says "lastUpdatedAt (LocalDateTime, auto-updated)".
    // But test code: `Instant first = e.getLastUpdatedAt();` in `testEventPreUpdateUpdatesLastUpdatedAt`.
    // I should use Instant if the test demands it, or LocalDateTime if requirements demand it. 
    // Usually code follows requirements, but tests fail if types mismatch.
    // The requirements say LocalDateTime. The test says `Instant`. 
    // Let's check `testEventPreUpdateUpdatesLastUpdatedAt` again.
    // `Instant first = e.getLastUpdatedAt();`
    // I will use Instant for lastUpdatedAt to satisfy the test, but LocalDateTime for createdAt.
    // Actually, mixing them is weird. Let's look closer at the test code.
    // `Instant first = e.getLastUpdatedAt();`
    // `public void testEventPrePersistSetsTimestamps()` also asserts not null.
    // I'll stick to Instant for lastUpdatedAt to match the test variable type assignment.
    // Wait, if I change it to LocalDateTime, the test `Instant first = ...` will fail compilation.
    // So I MUST use Instant for lastUpdatedAt.
    
    // However, requirement says `createdAt (LocalDateTime)`.
    // Test `testEventPrePersistSetsTimestamps` doesn't assign createdAt to a variable, just asserts not null.
    // So LocalDateTime is fine for createdAt.

    public Event() {}

    public Event(Long id, String title, String description, String location, String category, User publisher, boolean isActive, LocalDateTime createdAt, Instant lastUpdatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.category = category;
        this.publisher = publisher;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (lastUpdatedAt == null) {
            lastUpdatedAt = Instant.now();
        }
    }

    @PreUpdate
    public void onUpdate() {
        lastUpdatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Instant lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}""",
    "src/main/java/com/example/demo/entity/EventUpdate.java": """package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_updates")
public class EventUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private String updateContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UpdateType updateType;

    @Column(updatable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private SeverityLevel severityLevel;

    public EventUpdate() {}

    public EventUpdate(Long id, Event event, String updateContent, UpdateType updateType, LocalDateTime timestamp, SeverityLevel severityLevel) {
        this.id = id;
        this.event = event;
        this.updateContent = updateContent;
        this.updateType = updateType;
        this.timestamp = timestamp;
        this.severityLevel = severityLevel;
    }

    @PrePersist
    public void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (severityLevel == null) {
            severityLevel = SeverityLevel.LOW;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public SeverityLevel getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(SeverityLevel severityLevel) {
        this.severityLevel = severityLevel;
    }
}""",
    "src/main/java/com/example/demo/entity/Role.java": """package com.example.demo.entity;

public enum Role {
    ADMIN,
    PUBLISHER,
    SUBSCRIBER
}""",
    "src/main/java/com/example/demo/entity/SeverityLevel.java": """package com.example.demo.entity;

public enum SeverityLevel {
    LOW,
    MEDIUM,
    HIGH
}""",
    "src/main/java/com/example/demo/entity/Subscription.java": """package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "event_id"})
})
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(updatable = false)
    private LocalDateTime subscribedAt;

    public Subscription() {}

    public Subscription(Long id, User user, Event event, LocalDateTime subscribedAt) {
        this.id = id;
        this.user = user;
        this.event = event;
        this.subscribedAt = subscribedAt;
    }

    @PrePersist
    public void onCreate() {
        if (subscribedAt == null) {
            subscribedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LocalDateTime getSubscribedAt() {
        return subscribedAt;
    }

    public void setSubscribedAt(LocalDateTime subscribedAt) {
        this.subscribedAt = subscribedAt;
    }
}""",
    "src/main/java/com/example/demo/entity/UpdateType.java": """package com.example.demo.entity;

public enum UpdateType {
    INFO,
    WARNING,
    CRITICAL
}""",
    "src/main/java/com/example/demo/entity/User.java": """package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public User() {}

    public User(Long id, String fullName, String email, String password, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (role == null) {
            role = Role.SUBSCRIBER;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}""",
    "src/main/java/com/example/demo/exception/BadRequestException.java": """package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}""",
    "src/main/java/com/example/demo/exception/ResourceNotFoundException.java": """package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}""",
    "src/main/java/com/example/demo/repository/BroadcastLogRepository.java": """package com.example.demo.repository;

import com.example.demo.entity.BroadcastLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BroadcastLogRepository extends JpaRepository<BroadcastLog, Long> {
    List<BroadcastLog> findByEventUpdateId(Long eventUpdateId);
    List<BroadcastLog> findBySubscriberId(Long subscriberId);
}""",
    "src/main/java/com/example/demo/repository/EventRepository.java": """package com.example.demo.repository;

import com.example.demo.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByIsActiveTrue();
    List<Event> findByIsActiveTrueAndCategory(String category);
    List<Event> findByIsActiveTrueAndLocationContainingIgnoreCase(String location);
}""",
    "src/main/java/com/example/demo/repository/EventUpdateRepository.java": """package com.example.demo.repository;

import com.example.demo.entity.EventUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventUpdateRepository extends JpaRepository<EventUpdate, Long> {
    List<EventUpdate> findByEventId(Long eventId);
    List<EventUpdate> findByEventIdOrderByTimestampAsc(Long eventId);
}""",
    "src/main/java/com/example/demo/repository/SubscriptionRepository.java": """package com.example.demo.repository;

import com.example.demo.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
    List<Subscription> findByUserId(Long userId);
    List<Subscription> findByEventId(Long eventId);
    Optional<Subscription> findByUserIdAndEventId(Long userId, Long eventId);
}""",
    "src/main/java/com/example/demo/repository/UserRepository.java": """package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}""",
    "src/main/java/com/example/demo/security/CustomUserDetailsService.java": """package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}""",
    "src/main/java/com/example/demo/security/JwtAuthenticationFilter.java": """package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getJwtFromRequest(request);

        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            String email = jwtUtil.getEmailFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            // userId is also available: Long userId = jwtUtil.getUserIdFromToken(token);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}""",
    "src/main/java/com/example/demo/security/JwtUtil.java": """package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String secret;
    private final long validityInMs;
    private final Key key;

    public JwtUtil(@Value("${jwt.secret:ThisIsAVerySecureSecretKeyForJwtDemo123456789}") String secret,
                   @Value("${jwt.expiration:3600000}") long validityInMs) {
        this.secret = secret;
        this.validityInMs = validityInMs;
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Long userId, String email, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getUsernameFromToken(String token) {
        return getEmailFromToken(token);
    }

    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    public Long getUserIdFromToken(String token) {
        return getClaims(token).get("userId", Long.class);
    }
}""",
    "src/main/java/com/example/demo/service/BroadcastService.java": """package com.example.demo.service;

import com.example.demo.entity.BroadcastLog;
import java.util.List;

public interface BroadcastService {
    void broadcastUpdate(Long updateId);
    List<BroadcastLog> getLogsForUpdate(Long updateId);
    void recordDelivery(Long updateId, Long subscriberId, boolean successful);
    List<BroadcastLog> getAllLogs();
}""",
    "src/main/java/com/example/demo/service/EventService.java": """package com.example.demo.service;

import com.example.demo.entity.Event;
import java.util.List;

public interface EventService {
    Event createEvent(Event event);
    Event updateEvent(Long id, Event updated);
    Event getById(Long id);
    List<Event> getActiveEvents();
    void deactivateEvent(Long id);
    Event getEventById(Long id);
    List<Event> getAllEvents();
}""",
    "src/main/java/com/example/demo/service/EventUpdateService.java": """package com.example.demo.service;

import com.example.demo.entity.EventUpdate;
import java.util.List;
import java.util.Optional;

public interface EventUpdateService {
    EventUpdate publishUpdate(EventUpdate update);
    List<EventUpdate> getUpdatesForEvent(Long eventId);
    Optional<EventUpdate> getUpdateById(Long id);
    List<EventUpdate> getAllUpdates();
}""",
    "src/main/java/com/example/demo/service/SubscriptionService.java": """package com.example.demo.service;

import com.example.demo.entity.Subscription;
import java.util.List;

public interface SubscriptionService {
    Subscription subscribe(Long userId, Long eventId);
    void unsubscribe(Long userId, Long eventId);
    List<Subscription> getUserSubscriptions(Long userId);
    boolean isSubscribed(Long userId, Long eventId);
    List<Subscription> getAllSubscriptions();
}""",
    "src/main/java/com/example/demo/service/UserService.java": """package com.example.demo.service;

import com.example.demo.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User updated);
}""",
    "src/main/java/com/example/demo/service/impl/BroadcastServiceImpl.java": """package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.repository.BroadcastLogRepository;
import com.example.demo.repository.EventUpdateRepository;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.service.BroadcastService;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BroadcastServiceImpl implements BroadcastService {

    // Test Constructor: BroadcastServiceImpl(EventUpdateRepository, SubscriptionRepository, BroadcastLogRepository)
    // Requirement Doc Constructor: BroadcastServiceImpl(BroadcastLogRepository, SubscriptionRepository, EventUpdateRepository)
    // I MUST follow the TEST code.
    
    private final EventUpdateRepository eventUpdateRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final BroadcastLogRepository broadcastLogRepository;

    public BroadcastServiceImpl(EventUpdateRepository eventUpdateRepository, 
                                SubscriptionRepository subscriptionRepository, 
                                BroadcastLogRepository broadcastLogRepository) {
        this.eventUpdateRepository = eventUpdateRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.broadcastLogRepository = broadcastLogRepository;
    }

    @Override
    public void broadcastUpdate(Long updateId) {
        EventUpdate update = eventUpdateRepository.findById(updateId)
                .orElseThrow(() -> new ResourceNotFoundException("EventUpdate not found"));
        
        List<Subscription> subscriptions = subscriptionRepository.findByEventId(update.getEvent().getId());
        
        for (Subscription sub : subscriptions) {
            BroadcastLog log = new BroadcastLog();
            log.setEventUpdate(update);
            log.setSubscriber(sub.getUser());
            log.setDeliveryStatus(DeliveryStatus.SENT);
            broadcastLogRepository.save(log);
        }
    }

    @Override
    public List<BroadcastLog> getLogsForUpdate(Long updateId) {
        return broadcastLogRepository.findByEventUpdateId(updateId);
    }

    @Override
    public void recordDelivery(Long updateId, Long subscriberId, boolean successful) {
        List<BroadcastLog> logs = broadcastLogRepository.findByEventUpdateId(updateId);
        // Find specific log for subscriber
        // This is inefficient but fits the repository methods available
        // Better would be findByEventUpdateIdAndSubscriberId
        // But let's filter in memory or find by subscriber
        
        for (BroadcastLog log : logs) {
            if (log.getSubscriber().getId().equals(subscriberId)) {
                log.setDeliveryStatus(successful ? DeliveryStatus.SENT : DeliveryStatus.FAILED);
                broadcastLogRepository.save(log);
                return; 
            }
        }
    }

    @Override
    public List<BroadcastLog> getAllLogs() {
        return broadcastLogRepository.findAll();
    }
}""",
    "src/main/java/com/example/demo/service/impl/EventServiceImpl.java": """package com.example.demo.service.impl;

import com.example.demo.entity.Event;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EventService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Event createEvent(Event event) {
        User publisher = userRepository.findById(event.getPublisher().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (publisher.getRole() != Role.ADMIN && publisher.getRole() != Role.PUBLISHER) {
            throw new BadRequestException("Only PUBLISHER or ADMIN can create events");
        }
        event.setPublisher(publisher); // Ensure managed entity
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Long id, Event updated) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setLocation(updated.getLocation());
        existing.setCategory(updated.getCategory());
        // Trigger update timestamp
        existing.onUpdate();
        
        return eventRepository.save(existing);
    }

    @Override
    public Event getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    @Override
    public List<Event> getActiveEvents() {
        return eventRepository.findByIsActiveTrue();
    }

    @Override
    public void deactivateEvent(Long id) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        existing.setActive(false);
        eventRepository.save(existing);
    }

    @Override
    public Event getEventById(Long id) {
        return getById(id);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}""",
    "src/main/java/com/example/demo/service/impl/EventUpdateServiceImpl.java": """package com.example.demo.service.impl;

import com.example.demo.entity.EventUpdate;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.EventUpdateRepository;
import com.example.demo.service.BroadcastService;
import com.example.demo.service.EventUpdateService;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventUpdateServiceImpl implements EventUpdateService {

    private final EventUpdateRepository eventUpdateRepository;
    private final EventRepository eventRepository;
    private final BroadcastService broadcastService;

    public EventUpdateServiceImpl(EventUpdateRepository eventUpdateRepository, EventRepository eventRepository, BroadcastService broadcastService) {
        this.eventUpdateRepository = eventUpdateRepository;
        this.eventRepository = eventRepository;
        this.broadcastService = broadcastService;
    }

    @Override
    public EventUpdate publishUpdate(EventUpdate update) {
        // Ensure event exists
        if (!eventRepository.existsById(update.getEvent().getId())) {
             throw new ResourceNotFoundException("Event not found");
        }
        
        EventUpdate saved = eventUpdateRepository.save(update);
        broadcastService.broadcastUpdate(saved.getId());
        return saved;
    }

    @Override
    public List<EventUpdate> getUpdatesForEvent(Long eventId) {
        return eventUpdateRepository.findByEventIdOrderByTimestampAsc(eventId);
    }

    @Override
    public Optional<EventUpdate> getUpdateById(Long id) {
        return eventUpdateRepository.findById(id);
    }

    @Override
    public List<EventUpdate> getAllUpdates() {
        return eventUpdateRepository.findAll();
    }
}""",
    "src/main/java/com/example/demo/service/impl/SubscriptionServiceImpl.java": """package com.example.demo.service.impl;

import com.example.demo.entity.Subscription;
import com.example.demo.entity.User;
import com.example.demo.entity.Event;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SubscriptionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public Subscription subscribe(Long userId, Long eventId) {
        if (subscriptionRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new BadRequestException("Already subscribed");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        Subscription sub = new Subscription();
        sub.setUser(user);
        sub.setEvent(event);
        
        return subscriptionRepository.save(sub);
    }

    @Override
    public void unsubscribe(Long userId, Long eventId) {
        Subscription sub = subscriptionRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new BadRequestException("Subscription not found")); // Test expects BadRequestException here
        subscriptionRepository.delete(sub);
    }

    @Override
    public List<Subscription> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    @Override
    public boolean isSubscribed(Long userId, Long eventId) {
        return subscriptionRepository.existsByUserIdAndEventId(userId, eventId);
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }
}""",
    "src/main/java/com/example/demo/service/impl/UserServiceImpl.java": """package com.example.demo.service.impl;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User updated) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        existing.setFullName(updated.getFullName());
        // Handle other fields updates if necessary, avoiding password/email overwrite if null
        return userRepository.save(existing);
    }
}""",
    "src/main/java/com/example/demo/servlet/SimpleStatusServlet.java": """package com.example.demo.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/simple-status")
public class SimpleStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(200);
        resp.setContentType("text/plain");
        resp.getWriter().write("OK");
    }
}""",
    "src/test/java/com/example/demo/DigitalLocalEventBroadcastingApiTest.java": """package com.example.demo;

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
        
        // Initialize services strictly
        userService = new UserServiceImpl(userRepository, passwordEncoder);
        eventService = new EventServiceImpl(eventRepository, userRepository);
        
        broadcastService = new BroadcastServiceImpl(eventUpdateRepository, subscriptionRepository, broadcastLogRepository);
        eventUpdateService = new EventUpdateServiceImpl(eventUpdateRepository, eventRepository, broadcastService);
        subscriptionService = new SubscriptionServiceImpl(subscriptionRepository, userRepository, eventRepository);

        jwtUtil = new JwtUtil("ThisIsAVerySecureSecretKeyForJwtDemo123456789", 3600000);
    }

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
        u.setEmail("x @y.com");
        u.setPassword("plain");
        Mockito.when(userRepository.existsByEmail("x @y.com")).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> i.getArgument(0));

        User saved = userService.register(u);
        Assert.assertNotEquals(saved.getPassword(), "plain");
        Assert.assertTrue(saved.getPassword().startsWith("$2"));
    }

    @Test(priority = 18, groups = "ioc")
    public void testUserServiceDuplicateEmail() {
        User u = new User();
        u.setEmail("dup @y.com");
        Mockito.when(userRepository.existsByEmail("dup @y.com")).thenReturn(true);
        try {
            userService.register(u);
            Assert.fail("Expected BadRequestException");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Email already registered") || ex.getMessage().contains("Email already exists"));
        }
    }

    @Test(priority = 19, groups = "ioc")
    public void testUserServiceFindByEmailUsesRepo() {
        User u = new User();
        u.setEmail("a @b.com");
        Mockito.when(userRepository.findByEmail("a @b.com")).thenReturn(Optional.of(u));
        Optional<User> found = userService.findByEmail("a @b.com");
        Assert.assertEquals(found.get().getEmail(), "a @b.com");
    }

    @Test(priority = 20, groups = "ioc")
    public void testUserServiceFindByEmailNotFound() {
        Mockito.when(userRepository.findByEmail("zzz @x.com")).thenReturn(Optional.empty());
        Optional<User> result = userService.findByEmail("zzz @x.com");
        Assert.assertTrue(result.isEmpty());
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
        u.setEmail("u @test.com");
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
        String token = jwtUtil.generateToken(1L, "user @test.com", "SUBSCRIBER");
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
        String token1 = jwtUtil.generateToken(1L, "a @test.com", "ADMIN");
        String token2 = jwtUtil.generateToken(2L, "b @test.com", "PUBLISHER");
        Assert.assertNotEquals(token1, token2);
    }

    @Test(priority = 52, groups = "security")
    public void testJwtUsernameExtraction() {
        String token = jwtUtil.generateToken(3L, "x @test.com", "SUBSCRIBER");
        String username = jwtUtil.getUsernameFromToken(token);
        Assert.assertEquals(username, "x @test.com");
    }

    @Test(priority = 53, groups = "security")
    public void testJwtExpirationFuture() {
        String token = jwtUtil.generateToken(5L, "y @test.com", "SUBSCRIBER");
        Assert.assertTrue(jwtUtil.validateToken(token));
    }

    @Test(priority = 54, groups = "security")
    public void testJwtRoleClaimPresent() {
        String token = jwtUtil.generateToken(10L, "role @test.com", "ADMIN");
        String role = jwtUtil.getRoleFromToken(token);
        Assert.assertEquals(role, "ADMIN");
    }

    @Test(priority = 55, groups = "security")
    public void testJwtTamperedTokenFails() {
        String token = jwtUtil.generateToken(1L, "x @test.com", "SUBSCRIBER");
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
}""",
    "src/test/java/com/example/demo/TestResultListener.java": """package com.example.demo;
 
import org.testng.ITestListener;
import org.testng.ITestResult;
 
public class TestResultListener implements ITestListener {
 
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println(result.getMethod().getMethodName() + " - PASS");
    }
 
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println(result.getMethod().getMethodName() + " - FAIL");
    }
 
    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println(result.getMethod().getMethodName() + " - SKIP");
    }
}"""
}

# Explicitly exclude certain files from being overwritten/written
excluded_files = [
    "src/main/resources/application.properties",
    "src/main/java/com/example/demo/config/SwaggerConfig.java"
]

for file_path, content in files.items():
    if file_path in excluded_files:
        print(f"Skipping {file_path} (Excluded)")
        continue
    
    # Create directory if it doesn't exist
    directory = os.path.dirname(file_path)
    if directory and not os.path.exists(directory):
        os.makedirs(directory)
        print(f"Created directory: {directory}")
    
    # Write content to file
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(content)
        print(f"Wrote to {file_path}")

print("Project recreation script completed.")
