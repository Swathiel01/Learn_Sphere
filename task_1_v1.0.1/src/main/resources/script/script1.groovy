import com.sap.gateway.ip.core.customdev.util.Message
import groovy.util.XmlSlurper
 
def Message processData(Message message) {
 
    // Read XML Payload
    def reader = message.getBody(java.io.Reader)
    def xml = new XmlSlurper().parse(reader)
 
    // Extract Fields
    def poId = xml.Header.PO_ID.text()?.trim()
    def buyerId = xml.Header.BuyerID.text()?.trim()
    def totalAmountText = xml.Header.TotalAmount.text()?.trim()
 
    // Store values for Exception Subprocess
    message.setProperty("PO_ID", poId)
    message.setProperty("PO_Status", "VALIDATION_STARTED")
    message.setProperty("TotalAmount", totalAmountText)
 
    // Validate PO_ID
    if (!poId) {
 
        throw new RuntimeException(
            "VALIDATION_FAILED: PO_ID is missing"
        )
    }
 
    // Validate BuyerID
    if (!buyerId) {
 
        throw new RuntimeException(
            "VALIDATION_FAILED for PO [${poId}]: BuyerID is missing or empty"
        )
    }
 
    // Validate TotalAmount
    if (!totalAmountText) {
 
        throw new RuntimeException(
            "VALIDATION_FAILED for PO [${poId}]: TotalAmount is missing"
        )
    }
 
    // Validate Numeric Amount
    Double totalAmount
 
    try {
 
        totalAmount = totalAmountText.toDouble()
 
    } catch(Exception e) {
 
        throw new RuntimeException(
            "VALIDATION_FAILED for PO [${poId}]: TotalAmount must be numeric"
        )
    }
 
    // Validate Amount > 0
    if (totalAmount <= 0) {
 
        throw new RuntimeException(
            "VALIDATION_FAILED for PO [${poId}]: TotalAmount must be greater than zero"
        )
    }
 
    // Validate Item Count
    def itemCount = xml.Items.Item.size()
 
    if (itemCount < 1) {
 
        throw new RuntimeException(
            "VALIDATION_FAILED for PO [${poId}]: Must have at least one Item line"
        )
    }
 
    // Success Properties
    message.setProperty("PO_Status", "VALIDATION_PASSED")
    message.setProperty("PO_ItemCount", itemCount)
    message.setProperty("ValidatedAmount", totalAmount)
 
    return message
}
 