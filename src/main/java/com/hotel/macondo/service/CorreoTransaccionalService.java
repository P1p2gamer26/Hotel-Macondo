package com.hotel.macondo.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class CorreoTransaccionalService {
    private final ObjectProvider<JavaMailSender> mailSender;

    @Value("${app.email.enabled:false}")
    private boolean enabled;

    @Value("${app.email.from:${spring.mail.username:}}")
    private String from;

    public CorreoTransaccionalService(ObjectProvider<JavaMailSender> mailSender) {
        this.mailSender = mailSender;
    }

    public boolean estaHabilitado() {
        return enabled && mailSender.getIfAvailable() != null && from != null && !from.isBlank();
    }

    public void enviarEnlace(String destinatario, String asunto, String enlace, String instruccion) {
        if (!enabled) {
            throw new IllegalStateException("El envío de correo no está configurado.");
        }
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || from == null || from.isBlank()) {
            throw new IllegalStateException("Falta configurar el remitente SMTP.");
        }
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(from);
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(instruccion + "\n\n" + enlace + "\n\nSi no solicitaste este mensaje, puedes ignorarlo.");
        sender.send(mensaje);
    }
}
