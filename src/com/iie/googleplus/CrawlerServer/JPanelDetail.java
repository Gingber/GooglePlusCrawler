package com.iie.googleplus.CrawlerServer;



/**
 *
 * @author shanjixi
 */
public class JPanelDetail extends javax.swing.JPanel {

    public String N22ame="userName";
    /**
     * Creates new form JPanelDetail
     */
    public JPanelDetail() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        
        jLabel1 = new javax.swing.JLabel();
        jTextFieldNodeID = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldURL = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldConnectStatus = new javax.swing.JTextField();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaNodeOutPut = new javax.swing.JTextArea();

        setPreferredSize(new java.awt.Dimension(800, 600));
        setLayout(null);

        jLabel1.setText("�ڵ�����");
        add(jLabel1);
        jLabel1.setBounds(0, 20, 80, 20);

       
        jTextFieldNodeID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldNodeIDActionPerformed(evt);
            }
        });
        add(jTextFieldNodeID);
        jTextFieldNodeID.setBounds(100, 20, 170, 21);

        jLabel2.setText("�ڵ㵱ǰ�ɼ�����");
        add(jLabel2);
        jLabel2.setBounds(0, 80, 96, 30);
        add(jTextFieldURL);
        jTextFieldURL.setBounds(100, 80, 370, 21);

        jLabel3.setText("�����������״̬");
        add(jLabel3);
        jLabel3.setBounds(0, 120, 100, 15);

        jTextFieldConnectStatus.setText("ERRoR");
        add(jTextFieldConnectStatus);
        jTextFieldConnectStatus.setBounds(100, 120, 370, 21);

        jTextField1.setText("CurrentTask");
        add(jTextField1);
        jTextField1.setBounds(100, 50, 370, 21);

        jLabel4.setText("��ǰ����");
        add(jLabel4);
        jLabel4.setBounds(0, 50, 80, 15);

        jTextAreaNodeOutPut.setColumns(20);
        jTextAreaNodeOutPut.setRows(5);
        jScrollPane2.setViewportView(jTextAreaNodeOutPut);

        add(jScrollPane2);
        jScrollPane2.setBounds(100, 170, 370, 340);

       
    }// </editor-fold>

    private void jTextFieldNodeIDActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        // TODO add your handling code here:
    }                                                

    // Variables declaration - do not modify
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextAreaNodeOutPut;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextFieldConnectStatus;
    private javax.swing.JTextField jTextFieldNodeID;
    private javax.swing.JTextField jTextFieldURL;
    // End of variables declaration
}