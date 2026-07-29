import com.sap.gateway.ip.core.customdev.util.Message
 
Message processData(Message message) {
 
    def savedBody = message.getProperty("SavedPOBody")
 
    message.setProperty("CreditStatus", "APPROVED")
    message.setProperty("CreditScore", "780")
 
    message.setBody(savedBody)
 
    return message
}
 