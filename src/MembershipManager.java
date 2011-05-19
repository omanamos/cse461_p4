
public class MembershipManager{

	private final String mulitcastAddr;
	public MembershipManager(String multicastAddr, String nickname){
		this.mulitcastAddr = multicastAddr;
	}
	
	public String getMulitcastAddr() {
		return mulitcastAddr;
	}
}
