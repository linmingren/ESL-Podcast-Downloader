package me.linmingren;

import java.awt.Dimension;

import java.io.OutputStream;

import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 *
 */
public class ConsolePane extends JScrollPane {
	private static final long serialVersionUID = -8702709566122285119L;

	private JTextPane textPane = new JTextPane();

	PrintStream mySystemOut;

	public PrintStream getPrintStream() {
		return mySystemOut;
	}

	public ConsolePane() {
		setViewportView(textPane);

		mySystemOut = new MyPrintStream(System.out);

		textPane.setEditable(true);
		setPreferredSize(new Dimension(640, 120));
	}

	class MyPrintStream extends PrintStream {

		MyPrintStream(OutputStream out) {
			super(out, true);
		}

		@Override
		public void write(byte[] buf, int off, int len) {
			final String message = new String(buf, off, len);

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						StyledDocument doc = (StyledDocument) textPane
								.getDocument();

						doc.insertString(doc.getLength(), message,
								doc.getStyle("StyleName"));

					} catch (BadLocationException e) {
						e.printStackTrace();
					}

					// Make sure the last line is always visible
					textPane.setCaretPosition(textPane.getDocument()
							.getLength());

				}
			});
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame("test");
		f.setPreferredSize(new Dimension(600, 600));
		ConsolePane p = new ConsolePane();

		f.setContentPane(p);
		f.pack();
		f.setVisible(true);

		p.getPrintStream().println("ffdffdsfdsaf");
	}

}
