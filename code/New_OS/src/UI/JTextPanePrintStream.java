package UI;

import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class JTextPanePrintStream extends PrintStream {
    private final JTextPane textPane;

    public JTextPanePrintStream(OutputStream out, JTextPane textPane) {
        super(out);
        this.textPane = textPane;
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        String message = new String(buf, off, len);

        // 在 JTextPane 文档末尾追加新内容
        Document doc = textPane.getDocument();
        try {
            doc.insertString(doc.getLength(), message, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
