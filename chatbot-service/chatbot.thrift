namespace java chatbot


/**
 * Invalid message error returned to client.
 */
exception InvalidMessageException {
    1: string whatMsg,
    2: string why
}

/**
 * Chatbot service.
 */
service Chatbot {
    /**
     *  send message to chatbot service
     */
    bool sendMessage(1:string msg) throws (1:InvalidMessageException ex)
}