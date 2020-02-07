package cn.seecoder;

public class Abstraction extends AST{
	
	public Abstraction(String id, AST body) {
		super(id, body);
        this.param = id;
        this.body = body;
        this.type = AST.Node.Abstraction;
    }
    public String AbstoString() {
    	if(this.type==AST.Node.Abstraction&&this.param==null) {
        	return "";
        }//参数为空的特殊情况
        String temp = "\\" + ".";
        if (this.body.type==AST.Node.Application) {
    		this.body=(Application)this.body;
            temp+=(this.body.ApptoString());
    }
    else if(this.body.type==AST.Node.Identifier) {
			this.body=(Identifier)this.body;
            temp+=(this.body.IdetoString());
    }
    else if(this.body.type==AST.Node.Abstraction) {
			this.body=(Abstraction)this.body;
            temp+=(this.body.AbstoString());
    }
        return temp+=("").toString();
    }
}
