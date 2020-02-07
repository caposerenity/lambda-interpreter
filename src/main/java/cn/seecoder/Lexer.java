package cn.seecoder;

//lexer类用于得到下一个字符，并为parser类中处理字符提供几种方法
import java.util.regex.Pattern;

class Lexer {
  private String source;
  private int index;
  private Token token;

      Lexer(String source) {
      this.source = source;
      this.index = 0;
      this.getToken();
  }

  private char getChar() {
       if (this.index >= this.source.length()) {
      	 index++;
          return '\0';//source处理结束
      }
      return this.source.charAt(index++);
  }

  private void getToken() {
      char tempChar;
      do {
          tempChar = this.getChar();
      } while (Pattern.matches("\\s", String.valueOf(tempChar)));
      switch (tempChar) {
          case '\\':
              this.token = new Token(Token.Type.LAMBDA);
              System.out.println("LAMBDA");
              break;
          case '.':
              this.token = new Token(Token.Type.DOT);
              System.out.println("DOT");
              break;
          case '(':
              this.token = new Token(Token.Type.LPAREN);
              System.out.println("LPAREN");
              break;
          case ')':
              this.token = new Token(Token.Type.RPAREN);
              System.out.println("RPAREN");
              break;
          case '\0':
              this.token = new Token(Token.Type.EOF);
              System.out.println("EOF");
              break;
          default:
              if (Pattern.matches("[a-z]", String.valueOf(tempChar))) {
                  String tempStr = new String();
                  do {
                      tempStr+=tempChar;
                      tempChar = this.getChar();
                  } while (Pattern.matches("[a-zA-Z]", String.valueOf(tempChar)));
                  this.index--;
                  this.token = new Token(Token.Type.LCID, tempStr);
                  System.out.println("LCID");
              }
      }
  }

  
  //next方法用于检查type是否和给定的相同
  boolean next(Token.Type type) {
      return this.token.getType() == type;
  }

  
  //断言下一字符类型
  void match(Token.Type type) {
      if ((this.next(type))&&type!=Token.Type.EOF) {
      	this.getToken();
      }
  }
  
  //检查字符类型并跳过
  boolean skip(Token.Type type) {
      if (this.next(type)) {
          this.getToken();
          return true;
      }
      return false;
  }

  

  
  //断言并返回其值
  String token(Token.Type type) {
      Token tempToken = this.token;
      this.match(type);
      return tempToken.getValue();
  }
}

