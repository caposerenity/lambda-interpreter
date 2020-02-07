package cn.seecoder;

public class Application extends AST{
	
	
    public Application(AST left, AST right) {
    	super(left, right);
        this.left = left;
        this.right = right;
        this.type = AST.Node.Application;
    }
    
    public String ApptoString() {
        if(this.left.value==-1&&this.right.value==-1)//若左右均为自由变量这一特殊情况
        	{this.left=(Identifier)this.left;
    		this.left=(Identifier)this.left;
            return this.left.IdetoString()+" "+this.right.IdetoString();
        	}
        else {
        String leftstr = "";
        String rightstr = "";
        //仍然使用递归方法，左右两侧分别tostring
        switch (this.left.type) {
            case Application:
            	this.left=(Application)this.left;
                leftstr = this.left.ApptoString();
                break;
            case Identifier:
            	this.left=(Identifier)this.left;
                leftstr = this.left.IdetoString();
                break;
            case Abstraction:
            	this.left=(Abstraction)this.left;
                leftstr = this.left.AbstoString();
                break;
        }
        switch (this.right.type) {
            case Application:
            	this.right=(Application)this.right;
                rightstr = this.right.ApptoString();
                break;
            case Identifier:
            	this.right=(Identifier)this.right;
                rightstr = this.right.IdetoString();
                break;
            case Abstraction:
            	this.right=(Abstraction)this.right;
                rightstr = this.right.AbstoString();
                break;
        }
        return "("+leftstr + " " + rightstr+")";
    }
    }
}
