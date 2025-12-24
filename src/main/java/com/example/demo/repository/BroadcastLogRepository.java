package com.example.demo.repository;

import com.example.demo.entity.*;
import java.util.*;

public interface BroadcastLogRepository {
    BroadcastLog save(BroadcastLog log);
    List<BroadcastLog> findByEventUpdateId(Long updateId);
}
