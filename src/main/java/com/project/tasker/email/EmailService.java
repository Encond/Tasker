package com.project.tasker.email;

import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class EmailService {

	private final JavaMailSender javaMailSender;
	private final TemplateEngine templateEngine;

	/**
	 * Sends a password reset token to the given email address.
	 *
	 * @param email the recipient's email address
	 * @param token the password reset token
	 */
	public void sendPasswordResetToken(final String email, final String token) {
		final Context context = new Context();
		context.setVariable("token", token);
		context.setVariable("email", email);
		context.setVariable("url", ""); // TODO: Add URL

		final String html = this.templateEngine.process("password-reset-token", context);

		this.sendHtml(email, "Password reset request", html);
	}

	/**
	 * Sends an authentication code to the given email address.
	 *
	 * @param email the recipient's email address
	 * @param code  the authentication code
	 */
	public void sendAuthenticationCode(final String email, final String code) {
		final Context context = new Context();
		context.setVariable("code", code);

		final String html = this.templateEngine.process("authentication-code", context);

		this.sendHtml(email, "Your authentication code", html);
	}

	private void sendHtml(final String email, final String subject, final String html) {
		final MimeMessage message = this.javaMailSender.createMimeMessage();

		try {
			final MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
			helper.setTo(email);
			helper.setSubject(subject);
			helper.setText(html, true);

			this.javaMailSender.send(message);
		} catch (final MessagingException exception) {
			throw new MailPreparationException("Failed to prepare email", exception);
		}
	}

}
