package com.example.hydrant

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Properties
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class EmailSender {
    companion object {
        // Email Configuration
        private const val EMAIL = "project.fhydrant@gmail.com"
        private const val APP_PASSWORD = "lcln hmre sfux sozl"

        suspend fun sendEmailWithAttachment(
            subject: String,
            body: String,
            attachmentFile: File
        ): Result<String> = withContext(Dispatchers.IO) {
            try {
                val props = Properties().apply {
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.ssl.protocols", "TLSv1.2")
                    put("mail.smtp.ssl.trust", "smtp.gmail.com")
                    put("mail.smtp.connectiontimeout", "15000")
                    put("mail.smtp.timeout", "15000")
                }

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(EMAIL, APP_PASSWORD)
                    }
                })

                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(EMAIL, "FireGrid App"))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL))
                    setSubject(subject)

                    val multipart = MimeMultipart()

                    // Email body
                    val textPart = MimeBodyPart().apply {
                        setText(body, "UTF-8")
                    }
                    multipart.addBodyPart(textPart)

                    // PDF attachment
                    val attachmentPart = MimeBodyPart().apply {
                        dataHandler = DataHandler(FileDataSource(attachmentFile))
                        fileName = attachmentFile.name
                    }
                    multipart.addBodyPart(attachmentPart)

                    setContent(multipart)
                }

                Transport.send(message)
                Result.success("Report sent successfully to $EMAIL")

            } catch (e: javax.mail.AuthenticationFailedException) {
                e.printStackTrace()
                Result.failure(Exception("Authentication failed. Check App Password."))
            } catch (e: javax.mail.MessagingException) {
                e.printStackTrace()
                Result.failure(Exception("Failed to send: ${e.message}"))
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }

        // NEW FUNCTION: Send Problem Report with User Info
        suspend fun sendProblemReport(
            problemType: String,
            description: String,
            userName: String,
            userEmail: String
        ): Result<Unit> = withContext(Dispatchers.IO) {
            try {
                val props = Properties().apply {
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                }

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(EMAIL, APP_PASSWORD)
                    }
                })

                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(EMAIL))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL))
                    subject = "Problem Report: $problemType"
                    setText("""
                        Problem Type: $problemType
                        
                        Description:
                        $description
                        
                        Submitted by:
                        Name: $userName
                        $userEmail
                        
                        ---
                        Sent from Fire Hydrant App
                    """.trimIndent())
                }

                Transport.send(message)
                Result.success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}