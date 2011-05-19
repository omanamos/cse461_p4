public enum Packet {
    YEAH,
    SAYS,
    GDAY,
    GBYE;
    
    public static Packet parseFromBytes(byte[] payload) {
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