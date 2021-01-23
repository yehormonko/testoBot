package yehor.monko.model;

public class EditPojo {
    private String chatId;
    private int messageId;

    public EditPojo(String chatId, int messageId) {
        this.chatId = chatId;
        this.messageId = messageId;
    }

    public EditPojo() {
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
}
