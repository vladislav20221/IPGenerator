package ipgenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Vladislav
 */
public class mainWindow extends JFrame {
    private static final Dimension SIZE = new Dimension ( 1_000, 455 );
    
    public static final DataModel tableModel = new DataModel ();
    public static final JProgressBar progresbar = new JProgressBar ();
    
    public static final JTabbedPane tabPane = new JTabbedPane ();
    
    private static final JTable table = new JTable( tableModel );    
    public static final JScrollPane scrol = new JScrollPane ( table );
    private static final JTextArea ipaddress = new JTextArea();
    private static final JTextArea number = new JTextArea();
    private static final IPv4address ipv4 = new IPv4address( "192.168.100.100 / 24" );
    // Общие сведения
    public static JLabel label_mask_number = new JLabel(); // Десятичное число занчение маски
    public static JLabel label_mask = new JLabel();
    public static JLabel label_mask_bin = new MyLabel( ipv4 );
    public static JLabel label_host_bit_count = new JLabel();
    public static JLabel label_network_bit_count = new JLabel();
    public static JLabel label_network_count = new JLabel();
    public static JLabel label_host_count = new JLabel();
    // Информация по выбранному адресу
    public static JLabel label_address_network = new JLabel(  );
    public static JLabel label_address_network_bin = new MyLabel( ipv4 );
    public static JLabel label_address_start = new JLabel();
    public static JLabel label_address_start_bin = new MyLabel( ipv4 );
    public static JLabel label_address_end = new JLabel();
    public static JLabel label_address_end_bin = new MyLabel( ipv4 );
    public static JLabel label_address_broadcast = new JLabel();
    public static JLabel label_address_broadcast_bin = new MyLabel( ipv4 );
    
    public mainWindow () {        
        super("Генерация ip адресов");       
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.setSize( SIZE );
        this.setResizable( false );        
        settingTable();                                     // настройка таблици
        JPanel center = new JPanel ();
        GridBagConstraints grid = new GridBagConstraints();
        center.setLayout( new GridBagLayout () );
        scrol.setMinimumSize( new Dimension ( 420, 0 ) );
        mainWindow.progresbar.setMinimum( 0 );
        mainWindow.progresbar.setPreferredSize( new Dimension ( 0, 6 ) );
        mainWindow.progresbar.setMinimumSize( new Dimension ( 0, 6 ) );
        mainWindow.progresbar.setMaximumSize( new Dimension ( 0, 6 ) );
        grid.weighty = 1;
        grid.fill = GridBagConstraints.BOTH;
        grid.gridx = 0; grid.gridy = 0;                
        center.add( scrol, grid );
        grid.weightx = 1;        
        grid.gridx = 1; grid.gridy = 0;
        center.add( getRightMenu(), grid );
        this.add( center, BorderLayout.CENTER );
        this.add( getPaneMenu(), BorderLayout.NORTH );
        this.add( getPaneInfo(), BorderLayout.SOUTH );
        this.setVisible( true );
    }
    
    private void settingTable () {
        tableModel.addColumnName( "Адрес сети" );
        tableModel.addColumnName( "Диапазон" );
        tableModel.addColumnName( "Broadcast" );
        
        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
        table.setAutoCreateColumnsFromModel( false );
        // Сообщаем об изменении в структуре данных в потоке рассылке событий
        tableModel.fireTableStructureChanged();
        
        table.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        table.getColumnModel().getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );        
        
        table.getColumnModel().getColumn( 0 ).setPreferredWidth( 100 );
        table.getColumnModel().getColumn( 0 ).setResizable( false );
        table.getColumnModel().getColumn( 1 ).setPreferredWidth( 200 );
        table.getColumnModel().getColumn( 1 ).setResizable( false );
        table.getColumnModel().getColumn( 2 ).setPreferredWidth( 100 );
        table.getColumnModel().getColumn( 2 ).setResizable( false );
        
        table.getSelectionModel().addListSelectionListener( new ListSelectionListener () {
            int count = 0;
            @Override
            public void valueChanged( ListSelectionEvent select ) {
                if ( tableModel.getData().isEmpty() ) return;
                if ( table.getSelectedRow() < 0 ) return;
                if ( count++%2 == 0 ) {
                    // Адрес сети
                    label_address_network.setText( tableModel.getValueAt( table.getSelectedRow(), 0 ).toString() );
                    label_address_network_bin.setText( IPv4address.showOctet( tableModel.getValueAt( table.getSelectedRow(), 0 ).toString() ) );
                    // Диапазон
                    String[] diapazon = tableModel.getValueAt( table.getSelectedRow(), 1 ).toString().split("-");
                    // Начальное значение диапазона
                    label_address_start.setText( diapazon[0] );
                    label_address_start_bin.setText( IPv4address.showOctet( diapazon[0] ) );
                    // Конечное значение диапазона
                    label_address_end.setText( diapazon[1] );
                    label_address_end_bin.setText( IPv4address.showOctet( diapazon[1] ) );
                    // Адрес широковещания
                    label_address_broadcast.setText( tableModel.getValueAt( table.getSelectedRow(), 2 ).toString() );
                    label_address_broadcast_bin.setText( IPv4address.showOctet( tableModel.getValueAt( table.getSelectedRow(), 2 ).toString() ) );                    
                    //System.out.println( tableModel.getValueAt( table.getSelectedRow(), 0 )  );
                    //System.out.println( tableModel.getValueAt( table.getSelectedRow(), 1 )  );
                    //System.out.println( tableModel.getValueAt( table.getSelectedRow(), 2 )  );
                    if ( count > 1_000 ) count = 0;
                }
            }
        });
    }
    private JPanel getPaneMenu () {
        Dimension sizeText = new Dimension ( 120, 18 );
        Dimension sizeTextSmall = new Dimension ( 40, 18 );
        ButtonGroup type_split = new ButtonGroup ();
        JToggleButton network = new JToggleButton ( "Число подсетей", true );
        JToggleButton host = new JToggleButton ( "Число узлов", false );
        type_split.add( host );
        type_split.add( network );
        
        JPanel menu = new JPanel ();
        menu.setLayout( new GridBagLayout () );
        GridBagConstraints grid = new GridBagConstraints();        
        
        JPanel pane = new JPanel ();
        pane.setLayout( new BoxLayout( pane, BoxLayout.X_AXIS ) );
        
        ipaddress.setPreferredSize( sizeText );
        ipaddress.setMinimumSize( sizeText );
        ipaddress.setMaximumSize( sizeText );
        ipaddress.setText( IPv4address.toString( ipv4.ip )+" / "+ipv4.mask );
        
        number.setPreferredSize( sizeTextSmall );
        number.setMinimumSize( sizeTextSmall );
        number.setMaximumSize( sizeTextSmall );
        number.setText("8");
        
        JButton calculation = new JButton ("Расчитать");
        calculation.addActionListener( new ActionListener () {
            @Override
            public void actionPerformed( ActionEvent ae ) {
                calculation.setEnabled( false );
                mainWindow.this.getContentPane().setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
                mainWindow.progresbar.setValue( 0 );
                table.setEnabled( false );
                Thread thread = new Thread ( ()->{
                    tableModel.clearRowAll();
                    ipv4.setIPaddress( ipaddress.getText() );
                    if ( host.isSelected() ) {
                        ipv4.splitHostsNumber( Integer.valueOf( number.getText() ) );
                    } else {
                        ipv4.splitNetworkNumber( Integer.valueOf( number.getText() ) );
                    }
                    SwingUtilities.invokeLater( ()->{
                        mainWindow.this.getContentPane().setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
                        calculation.setEnabled( true );
                        tableModel.update();
                        table.setEnabled( true );
                        //table.getSelectionModel().setSelectionInterval( 0, 0 );                        
                        mainWindow.progresbar.setValue( ipv4.netN );
                    } );
                });                
                thread.setDaemon( true );
                thread.setPriority( Thread.MAX_PRIORITY );
                thread.setName("Thread-calculation");
                thread.start();         
            }            
        });
        
        pane.add( Box.createHorizontalStrut( 10 ) );
        pane.add( new JLabel ("IPv4 адрс сети:") );
        pane.add( Box.createHorizontalStrut( 10 ) );        
        pane.add( ipaddress );
        pane.add( Box.createHorizontalGlue() );
        pane.add( network );
        pane.add( host );
        pane.add( Box.createHorizontalStrut( 10 ) );
        pane.add( number );
        pane.add( Box.createHorizontalStrut( 5 ) );
        pane.add( calculation );
        pane.add( Box.createHorizontalStrut( 10 ) );
        
        grid.weighty = 1;
        grid.fill = GridBagConstraints.HORIZONTAL;
        grid.weightx = 1;
        grid.gridx = 0; grid.gridy = 0;
        menu.add( pane, grid );
        grid.gridx = 0; grid.gridy = 1;
        menu.add( progresbar, grid );        
        calculation.doClick();
        return menu;
    }
    private JPanel getRightMenu () {
        JPanel menu = new JPanel ();
        menu.setLayout( new GridBagLayout () );
        GridBagConstraints grid = new GridBagConstraints();
        grid.insets = new Insets ( 0, 10, 0, 20 );
        grid.anchor = GridBagConstraints.WEST;
                
        grid.gridx = 0; grid.gridy = 0;
        menu.add( new JLabel ( "Маска сети:" ), grid );        
        grid.gridx = 1; grid.gridy = 0;
        menu.add( mainWindow.label_mask_number, grid );        
        grid.gridx = 2; grid.gridy = 0;
        menu.add( mainWindow.label_mask, grid );        
        grid.gridx = 4; grid.gridy = 0;        
        menu.add( getButtonCopyCliipboard( mainWindow.label_mask ), grid );        
        grid.gridx = 1; grid.gridy = 1;
        grid.gridwidth = 3;
        menu.add( mainWindow.label_mask_bin, grid );
        grid.gridwidth = 1;                         
        grid.gridx = 4; grid.gridy = 1;        
        menu.add( getButtonCopyCliipboard( mainWindow.label_mask_bin ), grid );  
        
        grid.gridwidth = 2;        
        grid.gridx = 0; grid.gridy = 2;
        menu.add( new JLabel ("Бит в узловой части:"), grid );
        grid.gridx = 3; grid.gridy = 2;
        grid.gridwidth = 1;
        menu.add( mainWindow.label_host_bit_count, grid );
        grid.gridx = 0; grid.gridy = 3;
        grid.gridwidth = 2;
        menu.add( new JLabel ("Бит в свободной сетевой части:"), grid );
        grid.gridx = 3; grid.gridy = 3;
        grid.gridwidth = 1;
        menu.add( mainWindow.label_network_bit_count, grid );
        
        grid.gridwidth = 2;        
        grid.gridx = 0; grid.gridy = 4;
        menu.add( new JLabel ("Число подсетей:"), grid );
        grid.gridx = 3; grid.gridy = 4;
        grid.gridwidth = 1;
        menu.add( mainWindow.label_network_count, grid );        
        grid.gridx = 0; grid.gridy = 5;
        grid.gridwidth = 2;
        menu.add( new JLabel ("Число узлов:"), grid );
        grid.gridx = 3; grid.gridy = 5;
        grid.gridwidth = 1;
        menu.add( mainWindow.label_host_count, grid );
                
        grid.gridheight = 1;
        grid.gridwidth = GridBagConstraints.REMAINDER;
        grid.weightx = 1;
        grid.weighty = 1;
        grid.fill = GridBagConstraints.BOTH;
        grid.gridx = 0; grid.gridy = 6;
        menu.add( getAddressInfoPane (), grid );
        return menu;
    }
    
    private JPanel getAddressInfoPane () {
        JPanel pane = new JPanel ();
        pane.setLayout( new GridBagLayout () );
        pane.setBorder( BorderFactory.createTitledBorder("Информация о выделенном адресе") );
        GridBagConstraints grid = new GridBagConstraints();
        grid.insets = new Insets ( 0, 10, 0, 20 );
        grid.anchor = GridBagConstraints.WEST;
        
        grid.gridx = 0; grid.gridy = 0;
        pane.add( new JLabel ( "Адрес сети:" ), grid );
        grid.gridx = 1; grid.gridy = 0;
        pane.add( mainWindow.label_address_network, grid );
        grid.gridx = 4; grid.gridy = 0;        
        pane.add( getButtonCopyCliipboard( mainWindow.label_address_network ), grid );  
        grid.gridx = 1; grid.gridy = 1;
        grid.gridwidth = 3;
        pane.add( mainWindow.label_address_network_bin, grid );
        grid.gridwidth = 1;
        grid.gridx = 4; grid.gridy = 1;        
        pane.add( getButtonCopyCliipboard( mainWindow.label_address_network_bin ), grid );  
        
        grid.gridx = 0; grid.gridy = 2;
        pane.add( new JLabel ( "Начальный адрес:" ), grid );
        grid.gridx = 1; grid.gridy = 2;
        pane.add( mainWindow.label_address_start, grid );
        grid.gridx = 4; grid.gridy = 2;        
        pane.add( getButtonCopyCliipboard( mainWindow.label_address_start ), grid );
        grid.gridx = 1; grid.gridy = 3;
        grid.gridwidth = 3;
        pane.add( mainWindow.label_address_start_bin, grid );
        grid.gridwidth = 1;
        grid.gridx = 4; grid.gridy = 3;
        pane.add( getButtonCopyCliipboard( mainWindow.label_address_start_bin ), grid );  
                
        grid.gridx = 0; grid.gridy = 4;
        pane.add( new JLabel ( "Конечный адрес:" ), grid );
        grid.gridx = 1; grid.gridy = 4;
        pane.add( mainWindow.label_address_end, grid );
        grid.gridx = 4; grid.gridy = 4;
        pane.add( getButtonCopyCliipboard( mainWindow.label_address_end ), grid );
        grid.gridx = 1; grid.gridy = 5;
        grid.gridwidth = 3;
        pane.add( mainWindow.label_address_end_bin, grid );
        grid.gridwidth = 1;
        grid.gridx = 4; grid.gridy = 5;
        pane.add( getButtonCopyCliipboard( mainWindow.label_address_end_bin ), grid );
        
        grid.gridx = 0; grid.gridy = 6;
        pane.add( new JLabel ( "Широковещательнрый адрес:" ), grid );
        grid.gridx = 1; grid.gridy = 6;
        pane.add( mainWindow.label_address_broadcast, grid );
        grid.gridx = 4; grid.gridy = 6;
        pane.add( getButtonCopyCliipboard( mainWindow.label_address_broadcast ), grid );
        grid.gridx = 1; grid.gridy = 7;
        grid.gridwidth = 3;
        pane.add( mainWindow.label_address_broadcast_bin, grid );
        grid.gridwidth = 1;
        grid.gridx = 4; grid.gridy = 7;
        pane.add( getButtonCopyCliipboard( mainWindow.label_address_broadcast_bin ), grid );
        
        grid.weightx = 1;
        grid.weighty = 1;
        grid.fill = GridBagConstraints.BOTH;
        grid.gridx = 20; grid.gridy = 20;
        pane.add( new JPanel (), grid );
        return pane;
    }
    
    private JPanel getPaneInfo () {
        Dimension size = new Dimension ( 20, 20 );
        JPanel pane = new JPanel ();
        pane.setLayout( new BoxLayout( pane, BoxLayout.X_AXIS ) );
        JLabel label_0 = new JLabel ();
            label_0.setOpaque( true );
            //label_0.setAlignmentY( JComponent.TOP_ALIGNMENT );
            label_0.setPreferredSize(size);
            label_0.setMaximumSize(size);
            label_0.setBackground( MyLabel.yellow );
        JLabel label_1 = new JLabel ();
            label_1.setOpaque( true );
            //label_1.setAlignmentY( JComponent.TOP_ALIGNMENT );
            label_1.setPreferredSize(size);
            label_1.setMaximumSize(size);
            label_1.setBackground( MyLabel.green );
        JLabel label_2 = new JLabel ();
            label_2.setOpaque( true );
            //label_2.setAlignmentY( JComponent.TOP_ALIGNMENT );
            label_2.setPreferredSize(size);
            label_2.setMaximumSize(size);
            label_2.setBackground( MyLabel.grey );
        
        pane.add( Box.createHorizontalStrut( 10 ) );    
        pane.add( label_0 );
        pane.add( new JLabel (" - биты занятой сетевой частью.") );
        pane.add( Box.createHorizontalStrut( 10 ) );  
        pane.add( label_1 );
        pane.add( new JLabel (" - биты свободной сетевой части.") );
        pane.add( Box.createHorizontalStrut( 10 ) );  
        pane.add( label_2 );
        pane.add( new JLabel (" - биты узловой части.") );        
        return pane;
    }
    
    private JButton getButtonCopyCliipboard ( final JLabel label ) {
        JButton but = new JButton ( new CopyCliipboardAction( label ) );
            but.setBorderPainted(false);
            but.setFocusPainted(false);
            but.setContentAreaFilled(false);
            but.setName( "copy-clip-board:"+label.getName() );
        return but;
    }
}