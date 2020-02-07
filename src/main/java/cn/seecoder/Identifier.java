package cn.seecoder;
public class Identifier extends AST{
	
	public Identifier(int value) {
		super(value);
        this.value = value;
        this.type = AST.Node.Identifier;
    }

	public Identifier(int value, String id) {
    	super(value, id);
        this.value = value;
        this.id = id;
        this.type = AST.Node.Identifier;
    }
	
   public String IdetoString() {
            if (this.value == -1) {
            	return Integer.toString(value);	//自由变量
            }
            else {
            return Integer.toString(this.value);
            }
        }
    
}
