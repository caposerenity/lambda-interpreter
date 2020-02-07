package cn.seecoder;

//AST类，是三种具体节点的父类

public abstract class AST {
	public String id;
	public String param;
	public AST body;
	public AST left;
	public AST right;
	public Node type;
	public int value;

    //枚举出三种类型
    public enum Node {
        Application, Identifier, Abstraction
    }    
    //AST构造方法，不同类型节点选取不同构造方法，其实例变量也不同
    public AST(AST left, AST right) {
        this.left = left;
        this.right = right;
        this.type = AST.Node.Application;
    }
    
    public AST(String id, AST body) {
        this.param = id;
        this.body = body;
        this.type = AST.Node.Abstraction;
    }
    public AST(int value) {
        this.value = value;
        this.type = AST.Node.Identifier;
    }
    public AST(int value, String id) {
        this.value = value;
        this.id = id;
        this.type = AST.Node.Identifier;
    }
    
    //AST的tostring方法
    public String toString() {
    	if(this.type==Node.Application) {
        	return this.ApptoString();
        }
        else if(this.type==Node.Abstraction) {
        	return this.AbstoString();
        }
        else {
        	return this.IdetoString();
        }
    }
    
    
    //以下三个方法由具体的子类进行实现
	public String IdetoString() {
		return null;
	}
	public String ApptoString() {
		return null;
	}
	public String AbstoString() {
		return null;
	}
    
}
