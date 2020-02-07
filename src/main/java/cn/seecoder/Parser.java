package cn.seecoder;


import java.util.ArrayList;

class Parser {
    private Lexer lexer;

    Parser(Lexer l) {
        this.lexer = l;
    }

    //开始构建AST
    AST parse() {
        AST parse = this.term(new ArrayList<String>());
        this.lexer.match(Token.Type.EOF);
        return parse;
    }

   
    private AST term(ArrayList<String> ctx) {
    	//递归
    	//判断是否为左侧情况（即LAMBDA LCID DOT Term）
        if (this.lexer.skip(Token.Type.LAMBDA)) {
            String id = this.lexer.token(Token.Type.LCID);
            ArrayList<String> temp = new ArrayList<String>();
            this.lexer.match(Token.Type.DOT);
            temp.add(id);
            temp.addAll(ctx); //后加入ctx，便于下面使用indexof计算De Bruijn
            AST term = this.term(temp);//递归，直至abs的body部分是applicat情况
            return new Abstraction(id, term);//创立abs结点
        }
        //否则即作为application处理
        else {
            return this.application(ctx);
        }
    }

     //
     // remove left recursions
     //
     //    Application::=Application Atom | Atom
     // -> Application ::=Atom ... Atom
     // -> Application'::=Atom Application' | 'Empty
     //
     //按上述方式递归处理application
    private AST application(ArrayList<String> ctx) {
        AST left = this.atom(ctx);
        while (true) {
            AST right = this.atom(ctx);
            if (right == null) {
            	return left;       //右侧为空时，递归结束，返回左侧
            }
            else {
            	left = new Application(left, right);  //否则将当前部分作为子树（左侧）
            }
        }
    }

    
     //		Atom::=LPAREN Term RPAREN | LCID
    
    private AST atom(ArrayList<String> ctx) {
        //判断是否为lcid形式
        if (this.lexer.next(Token.Type.LCID)) {
            String id = this.lexer.token(Token.Type.LCID);
            if (ctx.indexOf(id) == -1) {
                return new Identifier(-1, id); //自由变量
            }
            else {
            	return new Identifier(ctx.indexOf(id));//indexof用于后面计算时De Bruijn
            }
        }
      //判断是否为（term）形式
        else if (this.lexer.skip(Token.Type.LPAREN)) {
            AST term = this.term(ctx);
            this.lexer.match(Token.Type.RPAREN);
            return term;
        }
        else {
        	return null;
        }
    }
}
