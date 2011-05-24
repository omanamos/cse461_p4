
public class User {

	public final String nickname;
	public final String address;
	
	public User(String nickname, String address){
		this.nickname = nickname;
		this.address = address;
	}
	
	public int hashCode(){
		return this.nickname.hashCode();
	}
	
	public boolean equals(Object obj){
		if(obj instanceof User){
			User other = (User)obj;
			return this.nickname.equals(other.nickname) && this.address.equals(other.address);
		}else
			return false;
	}
	
	public String toString(){
		return this.nickname + "@" + this.address;
	}
}
