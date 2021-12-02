import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class MaxLengthTextDocument extends PlainDocument { // file importato

	private static final long serialVersionUID = 1L;
	
	private int maxChars;

    public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {
        if(str != null && (getLength() + str.length() < maxChars)){
        super.insertString(offs, str, a);
        }
    }
    
    public void setMaxChars(int m){
    	maxChars = m;
    }
}
