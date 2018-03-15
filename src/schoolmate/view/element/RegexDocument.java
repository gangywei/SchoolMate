package schoolmate.view.element;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class RegexDocument extends PlainDocument
{
   private static final long serialVersionUID = 1L;
   private String regex = null;

   public RegexDocument(){
      super();
   }

   public RegexDocument(String regex){
      this();
      this.regex = regex;
   }

   //限制文本框的输入类型
   public void insertString(int offs, String str, AttributeSet a)
         throws BadLocationException{
      if (str == null){
         return;
      }
      if (regex != null){
         if (!new StringBuilder(str).toString().matches(regex)){
            return;
         }
      }
      super.insertString(offs, str, a);
   }
   
   
   
}
