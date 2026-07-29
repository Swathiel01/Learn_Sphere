import com.sap.gateway.ip.core.customdev.util.Message
 
def Message processData(Message message) {
 
    def ex = message.getProperty("CamelExceptionCaught")
 
    String errorMessage = "Unknown Error"
    String errorLocation = "Unknown"
 
    if (ex != null) {
 
        Throwable rootCause = ex
 
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause()
        }
 
        errorMessage = rootCause.getMessage()
 
        for (StackTraceElement ste : rootCause.getStackTrace()) {
 
            if (ste.getFileName() != null &&
                ste.getFileName().endsWith(".groovy")) {
 
                errorLocation =
                    ste.getFileName() +
                    " : Line " +
                    ste.getLineNumber()
 
                break
            }
        }
    }
 
    message.setProperty("ErrorMessage", errorMessage)
    message.setProperty("ErrorLocation", errorLocation)
 
    message.setHeader("CamelHttpResponseCode", 500)
 
    return message
}
 