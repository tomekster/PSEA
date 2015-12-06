package core;

public class Front extends Population{
	
	public Front(){
		super();
	}
	
	public Front(Front front){
		super((Population) front);
	}
	
	@Override
	public Front copy(){
		return new Front(this);
	}
}
