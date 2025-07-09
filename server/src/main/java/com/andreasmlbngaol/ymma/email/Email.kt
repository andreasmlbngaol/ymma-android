package com.andreasmlbngaol.ymma.email

import jakarta.mail.Authenticator
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import java.util.Properties

object EmailService {
    private val email = System.getProperty("SMTP_USER")
    private val password = System.getProperty("SMTP_PASS")

    private val props = Properties().apply {
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.host", "smtp.gmail.com")
        put("mail.smtp.port", "587")
    }

    private val session: Session = Session.getInstance(props, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(email, password)
        }
    })

    fun sendEmail(to: String, subject: String, content: String) {
        println("📧 Sending email to $to with subject: $subject")
        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(email, "YMMA"))
                setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(to))
                setSubject(subject)
                setText(content, "utf-8", "html")
            }

            Transport.send(message) // still blocking
            println("✅ Email sent to $to")
        } catch (e: Exception) {
            println("❌ Error sending email: ${e.message}")
        }
    }

}