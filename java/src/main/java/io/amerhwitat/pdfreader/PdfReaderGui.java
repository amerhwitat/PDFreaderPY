package io.amerhwitat.pdfreader;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SwingWorker;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

/** Native Swing GUI for viewing PDF pages with PDFBox rendering. */
public final class PdfReaderGui {
    private final JFrame frame = new JFrame("PDF Reader — Java");
    private final JLabel pageView = new JLabel("Open a PDF to begin", JLabel.CENTER);
    private final JLabel status = new JLabel("Ready");
    private final JSpinner pageNumber = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
    private File currentPdf;

    private PdfReaderGui() {
        JButton open = new JButton("Open PDF…");
        JButton render = new JButton("Render page");
        open.addActionListener(e -> choosePdf());
        render.addActionListener(e -> renderPage());
        JPanel toolbar = new JPanel();
        toolbar.add(open);
        toolbar.add(new JLabel("Page:"));
        toolbar.add(pageNumber);
        toolbar.add(render);
        pageView.setPreferredSize(new Dimension(900, 650));
        pageView.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        frame.setLayout(new BorderLayout(8, 8));
        frame.add(toolbar, BorderLayout.NORTH);
        frame.add(new JScrollPane(pageView), BorderLayout.CENTER);
        frame.add(status, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(720, 520));
        frame.setSize(1100, 820);
        frame.setLocationByPlatform(true);
    }

    private void choosePdf() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose a PDF document");
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return;
        currentPdf = chooser.getSelectedFile();
        try (PDDocument document = Loader.loadPDF(currentPdf)) {
            int pages = document.getNumberOfPages();
            if (pages < 1) throw new IOException("The PDF contains no pages.");
            pageNumber.setModel(new SpinnerNumberModel(1, 1, pages, 1));
            status.setText(currentPdf.getName() + " · " + pages + " pages");
            renderPage();
        } catch (Exception ex) {
            showError("Could not open PDF", ex);
        }
    }

    private void renderPage() {
        if (currentPdf == null) {
            status.setText("Choose a PDF first");
            return;
        }
        int selectedPage = (Integer) pageNumber.getValue();
        status.setText("Rendering page " + selectedPage + "…");
        new SwingWorker<BufferedImage, Void>() {
            @Override protected BufferedImage doInBackground() throws Exception {
                try (PDDocument document = Loader.loadPDF(currentPdf)) {
                    if (selectedPage < 1 || selectedPage > document.getNumberOfPages()) {
                        throw new IOException("Page is outside the document range.");
                    }
                    return new PDFRenderer(document).renderImageWithDPI(selectedPage - 1, 120, ImageType.RGB);
                }
            }
            @Override protected void done() {
                try {
                    BufferedImage image = get();
                    int maxWidth = Math.max(300, frame.getContentPane().getWidth() - 50);
                    int maxHeight = Math.max(300, frame.getContentPane().getHeight() - 150);
                    double scale = Math.min(1.0, Math.min((double) maxWidth / image.getWidth(),
                                                         (double) maxHeight / image.getHeight()));
                    Image scaled = image.getScaledInstance(
                        Math.max(1, (int) (image.getWidth() * scale)),
                        Math.max(1, (int) (image.getHeight() * scale)), Image.SCALE_SMOOTH);
                    pageView.setText(null);
                    pageView.setIcon(new ImageIcon(scaled));
                    status.setText(currentPdf.getName() + " · page " + selectedPage);
                } catch (Exception ex) {
                    showError("Could not render PDF page", ex);
                }
            }
        }.execute();
    }

    private void showError(String title, Exception ex) {
        Throwable cause = ex.getCause() == null ? ex : ex.getCause();
        JOptionPane.showMessageDialog(frame, cause.getMessage(), title, JOptionPane.ERROR_MESSAGE);
        status.setText("Error: " + cause.getMessage());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PdfReaderGui().frame.setVisible(true));
    }
}
