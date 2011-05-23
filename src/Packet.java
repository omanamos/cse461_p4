public enum Packet {
    YEAH,   // XXX confusing that Packet.YEAH and Packet.Yeah look so similar...
    SAYS,   // maybe just give in and separate them out into their own files..
    GDAY,
    GBYE;
    
    public static Packet getType(byte[] payload) {
        String[] delimitedContents = payload.toString().split(" ");
        if(delimitedContents.length == 0)
            return null;
            
        
        String type = delimitedContents[0];
        if(type.equals("YEAH")) {
            return YEAH;
        } else if(type.equals("SAYS")) {
            return SAYS;
        } else if(type.equals("GDAY")) {
            return GDAY;
        } else if(type.equals("GBYE")) {
            return GBYE;
        } else {
            return null;
        }
    }
    
    public static class Yeah {
        public int sequenceNumber;
        
        public Yeah(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }
        
        public Yeah(byte[] payload) {
            String[] delimitedContents = payload.toString().split(" ");
            if(delimitedContents.length != 2 || !delimitedContents[0].equals("YEAH"))
                throw new IllegalArgumentException("Could not parse payload as YEAH packet");
            
            this.sequenceNumber = Integer.parseInt(delimitedContents[1]);
        }
        
        public byte[] toBytes() {
            String contents = "YEAH " + sequenceNumber;
            return contents.getBytes();
        }
        
        @Override
        public boolean equals(Object other) {
            if(!(other instanceof Packet.Yeah))
                return false;
            
            Packet.Yeah o = (Packet.Yeah) other;
            
            return o.sequenceNumber == this.sequenceNumber;
        }
        
        @Override
        public int hashCode() {
            return sequenceNumber;
        }
    }
    
    public static class Says {
        public String nickname;
        public int sequenceNumber;
        public String message;
        
        public Says(String nickname, int sequenceNumber, String message) {
            this.nickname = nickname;
            this.sequenceNumber = sequenceNumber;
            this.message = message;
        }
        
        public Says(byte[] payload) {
            // XXX what if the message includes spaces?
            // We should only split the first two spaces
            String[] delimitedContents = payload.toString().split(" "); 
            if(delimitedContents.length != 4 || !delimitedContents[0].equals("SAYS"))
                throw new IllegalArgumentException("Could not parse payload as Says packet");
            
            this.nickname = delimitedContents[1];
            this.sequenceNumber = Integer.parseInt(delimitedContents[2]);
            this.message = delimitedContents[3];
        }
        
        public byte[] toBytes() {
            // XXX what if nickname has a space?
            String contents = "SAYS " + nickname + " " + sequenceNumber + " " + message;
            return contents.getBytes();
        }
    }
    
    public static class GDay {
        public String nickname;
        
        public GDay(String nickname) {
            this.nickname = nickname;
        }
        
        public byte[] toBytes() {
            String contents = "GDAY " + nickname;
            return contents.getBytes();
        }
    }
    
    public static class GBye {
        public String nickname;
        
        public GBye(String nickname) {
            this.nickname = nickname;
        }
        
        public byte[] toBytes() {
            String contents = "GBYE " + nickname;
            return contents.getBytes();
        }
    }
}