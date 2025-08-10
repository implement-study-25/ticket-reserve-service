package com.study.ticketservice.test.infrastructure;

import com.study.ticketservice.test.domain.Test;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long testId;
//    ...

    public Test toDomain() {
        return new Test();
    }

    public static TestEntity from(Test domain) {
        TestEntity entity = new TestEntity();
        return entity;
    }
}
