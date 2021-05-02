package ch.uzh.ifi.hase.soprafs21.controller;

public class ChatControllerTest {
    
}

//@PostMapping("/chat/create")
    public MessageChannelGetDTO createMessageChannel() 
    {
        
    }

    /**
     * get messages from a chat, can use queries
     * TODO add more queries?
     * TODO make chats exclusive to enrolled participants
     */
    //@GetMapping("/chat/{chatId}")
    
    public List<MessageGetDTO> getMessages() {

    }

    private List<MessageGetDTO> convertMessageListToDTOList(List<Message> messages) {
        List<MessageGetDTO> messageGetDTOs = new ArrayList<>();

        // convert each message to the API representation
        for (Message m : messages) {
            messageGetDTOs.add(DTOMapper.INSTANCE.convertEntityToMessageGetDTO(m));
        }
        return messageGetDTOs;
    }

    /**
     * post a message to a chat
     */
    //@PostMapping("/chat/{chatId}")
    
    public MessageGetDTO postMessage()
    {
        
    }

}