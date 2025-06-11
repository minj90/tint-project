package com.example.tint.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PinkProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="process_id")
    private Long id;

    @Setter
    private String lotNumber;

    @Setter
    private int step;

    @Setter
    @Enumerated(EnumType.STRING)
    private Status status;

    @Setter
    private LocalDateTime processDate;

    @Setter
    private String errorCode;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="jobOrder_id")
    private JobOrder jobOrder;

}
