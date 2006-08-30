package orbital.moon.evolutionary;

import orbital.algorithm.evolutionary.*;
import java.awt.*;
import java.beans.*;
import java.awt.event.*;
import javax.swing.JOptionPane;

/**
 * @deprecated see orbital.algorithm.evolutionary.Chromosome
 */
public
class ChromosomeCustomizer extends Panel implements Customizer {
        private Chromosome chromosome = null;
        BorderLayout       borderLayout1 = new BorderLayout();
        TextField                  textData = new TextField();
        Label                      label1 = new Label();
        Panel                      panel1 = new Panel();
        FlowLayout                 flowLayout1 = new FlowLayout();
        Label                      label2 = new Label();
        TextField                  textLength = new TextField();

        public ChromosomeCustomizer() {
                try {
                        jbInit();
                } catch (Exception ex) {
                        ex.printStackTrace();
                } 
        }

        private void jbInit() throws Exception {
                this.setLayout(borderLayout1);
                label1.setText("Manipulate chromosome data:");
                panel1.setLayout(flowLayout1);
                label2.setText("chromosome length:");
                textData.addTextListener(new java.awt.event.TextListener() {

                        public void textValueChanged(TextEvent e) {
                                textData_textValueChanged(e);
                        } 
                });
                textData.addComponentListener(new java.awt.event.ComponentAdapter() {

                        public void componentHidden(ComponentEvent e) {
                                textData_componentHidden(e);
                        } 
                });
                textLength.setEditable(false);
                this.add(textData, BorderLayout.CENTER);
                this.add(label1, BorderLayout.NORTH);
                this.add(panel1, BorderLayout.SOUTH);
                panel1.add(label2, null);
                panel1.add(textLength, null);
        } 

        public void setObject(Object bean) {
                chromosome = (Chromosome) bean;
                textData.setText(chromosome.toString());
                textLength.setText("" + chromosome.length());
                setVisible(true);
        } 

        private final PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);
        public void addPropertyChangeListener(PropertyChangeListener listener) {
                propertyChangeListeners.addPropertyChangeListener(listener);
        } 

        public void removePropertyChangeListener(PropertyChangeListener listener) {
                propertyChangeListeners.removePropertyChangeListener(listener);
        } 

        void textData_textValueChanged(TextEvent e) {
                textLength.setText("" + textData.getText().length());

                // TODO: use windowDeactivated instead
                textData_componentHidden(null);
        } 

        void textData_componentHidden(ComponentEvent e) {
                try {
                        Chromosome n = Chromosome.valueOf(textData.getText());
                        System.err.println(n);
                        chromosome.setData(n.getData());
                        System.err.println(chromosome);
                } catch (NumberFormatException x) {
                        JOptionPane.showMessageDialog((Frame) getParent(), "Specified illegal characters for chromosome data: " + x, "Illegal Chromosome Data", JOptionPane.WARNING_MESSAGE);
                } 
        } 
}
