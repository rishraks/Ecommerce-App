package com.ecommerce.application.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Data
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;


    @NotBlank
    @Size(min = 4, message = "Payment method must contain at least 4 characters")
    private String paymentMethod;

    private String pgName;
    private String pgPaymentId;
    private String pgPaymentStatus;
    private String pgResponseMessage;


    @OneToOne(mappedBy = "payment", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Order order;


    public Payment(String paymentMethod, String pgName, String pgPaymentId, String pgPaymentStatus, String pgResponseMessage) {
        this.paymentMethod = paymentMethod;
        this.pgName = pgName;
        this.pgPaymentId = pgPaymentId;
        this.pgPaymentStatus = pgPaymentStatus;
        this.pgResponseMessage = pgResponseMessage;
    }
}

