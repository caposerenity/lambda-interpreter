package cn.seecoder;

public class Interpreter {
    Parser parser;
    AST astAfterParser;

    public Interpreter(Parser p){
        this.parser = p;
        this.astAfterParser = p.parse();
    }	//构造方法，这之后用于计算的ast建立完成
    
   
    public AST eval() {
    	return evalAST(this.astAfterParser);
    }
    //递归计算最终结果，从左向右运算
    private AST evalAST(AST ast) {
        while(true){
            if(ast.type==AST.Node.Application){//app的情况，需要就左右树分类讨论
                if(ast.left.type==AST.Node.Abstraction){//左树是abs，用右侧替换
                    ast = substitute(ast.left.body,ast.right);
                }
                else if(ast.left.type==AST.Node.Application&&(ast.right.type!=AST.Node.Identifier)){
                    ast.left=evalAST(ast.left);					//左树为app，右侧非ide，则左右分别运算
                    ast.right=evalAST(ast.right);
                    if(ast.left.type==AST.Node.Abstraction) {//若运算结束后左树成为abs，则再对ast进行运算
                    	ast = evalAST(ast);
                    }
                    return ast;
                }
                else if(ast.left.type==AST.Node.Application&&ast.right.type==AST.Node.Identifier){
                    ast.left=evalAST(ast.left);					//与上述类似，但无需计算右树
                    if(ast.left.type==AST.Node.Abstraction) {
                    	ast = evalAST(ast);
                    }
                    return ast;
                }
                else{
                    ast.right=evalAST(ast.right);				//左树为ide，则只需计算右树
                    return ast;
                }
            }
            else if(ast.type==AST.Node.Abstraction){//abs，计算其body后返回
               ast.body = evalAST(ast.body);
                return ast;
            }
            else{//ide的情况，直接返回
                return ast;
            }
        }
    }

   
    private AST substitute( AST node,AST value) {		//替换方法
        return shift(-1, subst(node,shift(1, value, 0), 0), 0);
        //整体替换前需要先+1，替换完成后由于消去最前面的λ，故需要-1
    }

    /**
     *  value替换node节点中的变量：
     *  如果节点是Application，分别对左右树替换；
     *  如果node节点是abstraction，替入node.body时深度得+1；
     *  如果node是identifier，则替换De Bruijn index值等于depth的identifier（替换之后value的值加深depth）

     *@param value 替换成为的value
     *@param node 被替换的整个节点
     *@param depth 外围的深度

             
     *@return AST
     *@exception  (方法有异常的话加)


     */
    private AST subst(AST node, AST value, int depth) {
        if (node.type == AST.Node.Identifier) {
            if (depth == node.value) {
                return shift(depth, value, 0);//替换
            }
            else { 
            	return node;//不替换
            }
        } 
        else if (node.type == AST.Node.Application) {
            return new Application(subst(node.left,value , depth),subst(node.right,value , depth));
            //返回一个新app,其左右两部分根据depth分别被判定是否需要替换
        }
        else if (node.type==AST.Node.Abstraction) {
            return new Abstraction(node.param,subst(node.body, value,depth + 1));
            //返回一个新abs,使得其body部分被替换，注意，此时depth需要+1（因为被替换部分是abs时，替换深度+1）
        }
        else {
        return null;
        }
    }

    /**

     *  De Bruijn index值位移
     *  如果节点是Application，分别对左右树位移；
     *  如果node节点是abstraction，新的body等于旧node.body位移by（from得+1）；
     *  如果node是identifier，则新的identifier的De Bruijn index值如果大于等于from则加by，否则加0（超出内层的范围的外层变量才要shift by位）.

        *@param by 位移的距离
     *@param node 位移的节点
     *@param from 内层的深度

             
     *@return AST
     *@exception  (方法有异常的话加)


     */

    private AST shift(int by, AST node, int from) {
        if (node.type == AST.Node.Identifier) {
            return new Identifier(node.value +  (node.value >= from ? by : 0),node.id);
            //若value<from，即没有超过内层范围，替换后不会引起误解，则+0；否则加上位移by
            }
        else if (node.type == AST.Node.Application) {
        	//如果节点是Application，分别对左右树位移
             return new Application(shift(by, node.left, from),shift(by, node.right, from));
        }
        else if (node.type==AST.Node.Abstraction) {
        	//如果node节点是abstraction，新的body等于旧node.body位移by（from得+1，因为此时更深入了一层）
            return new Abstraction(node.param,shift(by, node.body, from + 1));
        }
        else {
        return null;
        }
    }
    static String ZERO = "(\\f.\\x.x)";
    static String SUCC = "(\\n.\\f.\\x.f (n f x))";
    static String ONE = app(SUCC, ZERO);
    static String TWO = app(SUCC, ONE);
    static String THREE = app(SUCC, TWO);
    static String FOUR = app(SUCC, THREE);
    static String FIVE = app(SUCC, FOUR);
    static String PLUS = "(\\m.\\n.((m "+SUCC+") n))";
    static String POW = "(\\b.\\e.e b)";       // POW not ready
    static String PRED = "(\\n.\\f.\\x.n(\\g.\\h.h(g f))(\\u.x)(\\u.u))";
    static String SUB = "(\\m.\\n.n"+PRED+"m)";
    static String TRUE = "(\\x.\\y.x)";
    static String FALSE = "(\\x.\\y.y)";
    static String AND = "(\\p.\\q.p q p)";
    static String OR = "(\\p.\\q.p p q)";
    static String NOT = "(\\p.\\a.\\b.p b a)";
    static String IF = "(\\p.\\a.\\b.p a b)";
    static String ISZERO = "(\\n.n(\\x."+FALSE+")"+TRUE+")";
    static String LEQ = "(\\m.\\n."+ISZERO+"("+SUB+"m n))";
    static String EQ = "(\\m.\\n."+AND+"("+LEQ+"m n)("+LEQ+"n m))";
    static String MAX = "(\\m.\\n."+IF+"("+LEQ+" m n)n m)";
    static String MIN = "(\\m.\\n."+IF+"("+LEQ+" m n)m n)";

    private static String app(String func, String x){
        return "(" + func + x + ")";
    }
    private static String app(String func, String x, String y){
        return "(" +  "(" + func + x +")"+ y + ")";
    }
    private static String app(String func, String cond, String x, String y){
        return "(" + func + cond + x + y + ")";
    }

    public static void main(String[] args) {
        // write your code here


        String[] sources = {
                ZERO,//0
                ONE,//1
                TWO,//2
                THREE,//3
                app(PLUS, ZERO, ONE),//4
                app(PLUS, TWO, THREE),//5
                app(POW, TWO, TWO),//6
                app(PRED, ONE),//7
                app(PRED, TWO),//8
                app(SUB, FOUR, TWO),//9
                app(AND, TRUE, TRUE),//10
                app(AND, TRUE, FALSE),//11
                app(AND, FALSE, FALSE),//12
                app(OR, TRUE, TRUE),//13
                app(OR, TRUE, FALSE),//14
                app(OR, FALSE, FALSE),//15
                app(NOT, TRUE),//16
                app(NOT, FALSE),//17
                app(IF, TRUE, TRUE, FALSE),//18
                app(IF, FALSE, TRUE, FALSE),//19
                app(IF, app(OR, TRUE, FALSE), ONE, ZERO),//20
                app(IF, app(AND, TRUE, FALSE), FOUR, THREE),//21
                app(ISZERO, ZERO),//22
                app(ISZERO, ONE),//23
                app(LEQ, THREE, TWO),//24
                app(LEQ, TWO, THREE),//25
                app(EQ, TWO, FOUR),//26
                app(EQ, FIVE, FIVE),//27
                app(MAX, ONE, TWO),//28
                app(MAX, FOUR, TWO),//29
                app(MIN, ONE, TWO),//30
                app(MIN, FOUR, TWO),//31
        };

      for(int i=0 ; i<sources.length; i++) {
            String source = sources[i];
            System.out.println(i+":"+source);
            Lexer lexer = new Lexer(source);
            Parser parser = new Parser(lexer);
            AST result = new Interpreter(parser).eval();
            System.out.println(i+":"+result.toString());
       }

    }
}
