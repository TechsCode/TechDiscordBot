package me.techscode.techdiscordbot.transcripts;

public class TicketTranscriptOptions {

    private final int messages;
    private final TicketTranscriptMessageType[] types;

    public static TicketTranscriptOptions DEFAULT = TicketTranscriptOptions.builder().build();

    public TicketTranscriptOptions(int messages, TicketTranscriptMessageType[] types) {
        this.messages = messages;
        this.types = types;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getMessagesAmount() {
        return messages;
    }

    public TicketTranscriptMessageType[] getTypes() {
        return types;
    }

    public static class Builder {

        /**
         *The amount of messages to include in the transcript.
         *<br>-1 = all
        **/
        private int messages = -1;

        /**
         *The tyeps of messages include in the transcript.
         *<br>empty = all
         **/
        private TicketTranscriptMessageType[] types = new TicketTranscriptMessageType[0];

        public Builder messages(int amount) {
            this.messages = amount;
            return this;
        }

        public Builder messages() {
            this.messages = -1;
            return this;
        }

        public Builder types(TicketTranscriptMessageType... types) {
            this.types = types;
            return this;
        }

        public TicketTranscriptOptions build() {
            return new TicketTranscriptOptions(this.messages, this.types);
        }
    }
}
