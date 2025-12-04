package com.palja.order_service.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Recipient {

    // 이메일 형식
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Column(name = "recipient_name", nullable = false, length = 50)
    private String name;

    @Column(name = "recipient_email", length = 255)
    private String email;

    @Column(name = "recipient_address", nullable = false, length = 200)
    private String address;

    @Column(name = "delivery_message", length = 255)
    private String deliveryMessage;

    public static Recipient create(
            String name,
            String email,
            String address,
            String deliveryMessage
    ) {
        String validatedName = validateName(name);
        String validatedEmail = validateEmail(email);
        String validatedAddress = validateAddress(address);
        String validatedMessage = validateDeliveryMessage(deliveryMessage);

        return Recipient.builder()
                .name(validatedName)
                .email(validatedEmail)
                .address(validatedAddress)
                .deliveryMessage(validatedMessage)
                .build();
    }

    public Recipient change(
            String name,
            String email,
            String address,
            String deliveryMessage
    ) {
        return create(name, email, address, deliveryMessage);
    }

    // ====== Validation + Normalization ======
    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("수령인 이름은 필수입니다.");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("수령인 이름은 50자를 초과할 수 없습니다.");
        }
        return name.trim();
    }

    private static String validateAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("수령인 주소는 필수입니다.");
        }
        if (address.length() > 200) {
            throw new IllegalArgumentException("수령인 주소는 200자를 초과할 수 없습니다.");
        }
        return address.trim();
    }

    private static String validateEmail(String email) {
        // optional
        if (email == null || email.isBlank()) {
            return null;
        }

        String trimmed = email.trim();

        if (trimmed.length() > 255) {
            throw new IllegalArgumentException("이메일은 255자를 초과할 수 없습니다.");
        }

        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("유효한 이메일 형식이 아닙니다.");
        }

        return trimmed.toLowerCase();
    }

    private static String validateDeliveryMessage(String message) {
        // optional
        if (message == null || message.isBlank()) {
            return null;
        }

        if (message.length() > 255) {
            throw new IllegalArgumentException("배송 메시지는 255자를 초과할 수 없습니다.");
        }

        return message.trim();
    }
}