
public class PendingYeah {
    public Peer from;
    public Packet.Yeah yeah;
    
    public PendingYeah(Peer from, Packet.Yeah yeah) {
        this.from = from;
        this.yeah = yeah;
    }
    
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof PendingYeah))
            return false;
        
        PendingYeah o = (PendingYeah) other;
        
        return o.from == this.from && o.yeah == this.yeah;
    }
    
    @Override
    public int hashCode() {
        return (from.hashCode() << 3) + yeah.hashCode();
    }
}
