import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.w3c.dom.Document;

public class XfaExtractor {
	private static void SafeDocToXml(Document document, String filename) throws IOException, TransformerException {
		FileOutputStream outputStream = new FileOutputStream(filename);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(outputStream);
		transformer.transform(source, result);
		outputStream.close();
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Missing arguments. Syntax:");
			System.out.println("XfaExtractor <input> <output>");
			System.out.println("Exmaple of use:");
			System.out.println("java -jar XfaExtractor.jar input.pdf output.xml");
			return;
		}
		
		try {
			File file = new File(args[0]);
			PDDocument document = Loader.loadPDF(file);

			PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

			if (acroForm != null && acroForm.getXFA() != null) {
				Document xfaDocument = acroForm.getXFA().getDocument();
				SafeDocToXml(xfaDocument, args[1]);
				System.out.println("Done.");
			} else {
				System.out.println("XFA form not found.");
			}

			document.close();
		} catch (IOException | TransformerException e) {
			e.printStackTrace();
		}
	}

}
